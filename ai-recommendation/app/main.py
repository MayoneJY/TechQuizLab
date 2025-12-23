# -*- coding: utf-8 -*-
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import uuid
import logging
import traceback
from typing import Dict, List, Optional
from app.schemas import (
    RecommendationRequest, RecommendationResponse,
    BattleBasedRecommendationRequest, BattleBasedRecommendationResponse
)
from app.utils.normalize import normalize_tag
from app.services.tag_mapper import map_tag
from app.services.scoring import calculate_score, calculate_weighted_average_score
from app.services.stage_analyzer import analyze_stage, recommend_stages_for_user_keywords
from app.services.recommender import recommend
from app.services.weakness_recommender import recommend_weakness_keywords
from app.services.association_recommender import recommend_by_association
from app.services.battle_data_processor import process_user_battle_data
from app.repositories.stage_repository import get_recent_stages
from app.repositories.snapshot_store import save_snapshot
from app.services.tag_mapper import AXES


# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI()


@app.get("/")
def read_root():
    return {"status": "ok"}


@app.post("/ai/v1/recommendations", response_model=RecommendationResponse)
def create_recommendation(request: RecommendationRequest):
    snapshot_id = request.snapshot_id or str(uuid.uuid4())
    
    normalized_tags = []
    for tag in request.tags:
        normalized = normalize_tag(tag)
        if normalized:
            normalized_tags.append(normalized)
    
    tag_mappings = []
    for tag in normalized_tags:
        mapping = map_tag(tag)
        tag_mappings.append({
            "tag": tag,
            "axis": mapping["axis"],
            "confidence": mapping["confidence"],
            "evidence": mapping["evidence"]
        })
    
    axis_scores = {}
    for axis in AXES:
        axis_damages = []
        axis_days = []
        axis_difficulty = []
        
        for i, mapping in enumerate(tag_mappings):
            if mapping["axis"] == axis:
                if i < len(request.damages):
                    axis_damages.append(request.damages[i])
                if i < len(request.days_since):
                    axis_days.append(request.days_since[i])
                if request.difficulty_weights and i < len(request.difficulty_weights):
                    axis_difficulty.append(request.difficulty_weights[i])
        
        if axis_damages:
            score = calculate_score(
                damages=axis_damages,
                days_since=axis_days,
                difficulty_weights=axis_difficulty if axis_difficulty else None
            )
            axis_scores[axis] = score
        else:
            axis_scores[axis] = 0.0
    
    stage_evidence = analyze_stage(request.stage)
    
    recommendation = recommend(
        required_scores=request.required_scores,
        current_scores=axis_scores,
        evidence=stage_evidence
    )
    
    response_data = {
        "snapshot_id": snapshot_id,
        "normalized_tags": normalized_tags,
        "tag_mappings": tag_mappings,
        "current_scores": axis_scores,
        "stage_evidence": stage_evidence,
        "recommendation": recommendation
    }
    
    save_snapshot(snapshot_id, response_data)
    
    return JSONResponse(content=response_data)


@app.post("/ai/v1/battle-based-recommendations", response_model=BattleBasedRecommendationResponse)
def create_battle_based_recommendation(request: BattleBasedRecommendationRequest):
    snapshot_id = request.snapshot_id or str(uuid.uuid4())
    
    battle_data = process_user_battle_data(user_id=request.user_id, limit=request.limit)
    
    axis_scores = {}
    for axis in AXES:
        axis_info = battle_data["axis_data"].get(axis, {})
        axis_details = axis_info.get("details", [])
        
        if axis_details:
            axis_damages = [detail.get("damage", 0) for detail in axis_details]
            axis_days = [detail.get("days_since", 0) for detail in axis_details]
            
            score = calculate_weighted_average_score(
                damages=axis_damages,
                days_since=axis_days
            )
            axis_scores[axis] = score
        else:
            axis_scores[axis] = 0.0
    
    weakness_recommendation = recommend_weakness_keywords(
        battle_data=battle_data,
        axis_scores=axis_scores
    )
    
    stage_content = request.stage.get("content", "")
    association_recommendations = []
    if stage_content:
        association_recommendations = recommend_by_association(
            battle_data=battle_data,
            stage_content=stage_content,
            top_k=5
        )
    
    battle_summary = {
        "total_battles": len(battle_data["tags"]),
        "axes_with_data": [axis for axis in AXES if len(battle_data["axis_data"][axis]["damages"]) > 0],
        "axis_counts": {
            axis: len(battle_data["axis_data"][axis]["damages"])
            for axis in AXES
        }
    }
    
    response_data = {
        "snapshot_id": snapshot_id,
        "user_id": request.user_id,
        "tag_mappings": battle_data["tag_mappings"],
        "current_scores": axis_scores,
        "weakness_recommendation": weakness_recommendation,
        "association_recommendations": association_recommendations,
        "battle_summary": battle_summary
    }
    
    save_snapshot(snapshot_id, response_data)
    
    return BattleBasedRecommendationResponse(**response_data)


@app.post("/ai/v1/stage-recommendations")
def create_stage_recommendations(request: Dict):
    user_id = request.get("user_id")
    limit = request.get("limit", 100)
    stage_limit = request.get("stage_limit", 100)
    
    if not user_id:
        return JSONResponse(
            status_code=400,
            content={"error": "user_id is required"}
        )
    
    battle_data = process_user_battle_data(user_id=user_id, limit=limit)
    stages = get_recent_stages(limit=stage_limit)
    
    recommendations = recommend_stages_for_user_keywords(
        battle_data=battle_data,
        stages=stages,
        top_n=5
    )
    
    return JSONResponse(content={
        "user_id": user_id,
        "recommendations": recommendations,
        "total_stages": len(stages)
    })


@app.get("/ai/v1/users/{user_id}/learning-plan")
def get_learning_plan(user_id: int, limit: Optional[int] = 200):
    try:
        battle_data = process_user_battle_data(user_id=user_id, limit=limit)
        
        axis_scores = {}
        for axis in AXES:
            axis_info = battle_data["axis_data"].get(axis, {})
            axis_details = axis_info.get("details", [])
            
            if axis_details:
                axis_damages = [detail.get("damage", 0) for detail in axis_details]
                axis_days = [detail.get("days_since", 0) for detail in axis_details]
                
                score = calculate_weighted_average_score(
                    damages=axis_damages,
                    days_since=axis_days
                )
                axis_scores[axis] = score
            else:
                axis_scores[axis] = 0.0
        
        weakness_recommendation = recommend_weakness_keywords(
            battle_data=battle_data,
            axis_scores=axis_scores
        )
        
        return JSONResponse(content={
            "userId": user_id,
            "weakKeywords": weakness_recommendation.get("weakKeywords", []),
            "learningPlan": weakness_recommendation.get("learningPlan", [])
        })
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate learning plan: {str(e)}")


@app.get("/ai/v1/users/{user_id}/stage-recommendations")
def get_stage_recommendations(user_id: int, limit: Optional[int] = 200, stageLimit: Optional[int] = 300):
    try:
        logger.info(f"[스테이지 추천] 시작 - user_id: {user_id}, limit: {limit}, stageLimit: {stageLimit}")
        
        # 1단계: 배틀 데이터 조회
        logger.info(f"[스테이지 추천] 배틀 데이터 조회 시작")
        battle_data = process_user_battle_data(user_id=user_id, limit=limit)
        logger.info(f"[스테이지 추천] 배틀 데이터 조회 완료 - tags: {len(battle_data.get('tags', []))}")
        
        # 2단계: 유저의 상위 job_category 조회
        logger.info(f"[스테이지 추천] 유저의 상위 job_category 조회 시작")
        from app.repositories.battle_detail_repository import get_user_top_job_categories
        top_job_categories = get_user_top_job_categories(user_id, limit=3)
        logger.info(f"[스테이지 추천] 상위 job_category: {top_job_categories}")
        
        # 3단계: job_category로 스테이지 필터링
        logger.info(f"[스테이지 추천] 스테이지 데이터 조회 시작 (job_category 필터링)")
        from app.repositories.stage_repository import get_stages_by_job_categories
        stages = get_stages_by_job_categories(top_job_categories, limit=stageLimit)
        logger.info(f"[스테이지 추천] 스테이지 데이터 조회 완료 - 개수: {len(stages)}")
        
        if not stages:
            logger.warning(f"[스테이지 추천] 스테이지 데이터가 없습니다")
            return JSONResponse(content={
                "userId": user_id,
                "queryKeywords": [],
                "recommendedStages": []
            })
        
        # 4단계: 추천 생성 (하이브리드 방식)
        logger.info(f"[스테이지 추천] 추천 생성 시작")
        recommendations = recommend_stages_for_user_keywords(
            battle_data=battle_data,
            stages=stages,
            top_n=5
        )
        logger.info(f"[스테이지 추천] 추천 생성 완료 - 개수: {len(recommendations)}")
        
        # 5단계: 쿼리 키워드 추출
        logger.info(f"[스테이지 추천] 쿼리 키워드 추출 시작")
        from app.services.stage_analyzer import _extract_user_query_keywords
        query_keywords = _extract_user_query_keywords(battle_data)
        logger.info(f"[스테이지 추천] 쿼리 키워드 추출 완료 - 키워드: {query_keywords}")
        
        # 6단계: 응답 생성
        logger.info(f"[스테이지 추천] 응답 생성 시작")
        recommended_stages = []
        for rec in recommendations:
            # recommendations에 이미 stage 정보가 포함되어 있으므로 직접 사용
            recommended_stages.append({
                "stageId": rec.get("stage_id"),
                "title": rec.get("title", ""),
                "companyName": rec.get("company_name", ""),
                "jobCategory": rec.get("job_category", ""),
                "score": rec.get("similarity_score", 0.0),
                "evidence": rec.get("evidence", [])
            })
        
        logger.info(f"[스테이지 추천] 완료 - 추천 개수: {len(recommended_stages)}")
        
        return JSONResponse(content={
            "userId": user_id,
            "queryKeywords": query_keywords,
            "recommendedStages": recommended_stages
        })
    except Exception as e:
        error_traceback = traceback.format_exc()
        logger.error(f"[스테이지 추천] 에러 발생:\n{error_traceback}")
        raise HTTPException(status_code=500, detail=f"Failed to generate stage recommendations: {str(e)}")