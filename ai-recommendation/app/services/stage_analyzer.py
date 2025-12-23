# -*- coding: utf-8 -*-
import re
import logging
from typing import Dict, List, Tuple, Optional
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from collections import Counter, defaultdict
from app.services.embedding import generate_embeddings
from app.services.tag_mapper import AXES, AXIS_KEYWORDS
from app.services.scoring import calculate_weighted_average_score

logger = logging.getLogger(__name__)


TECH_KEYWORDS = ["api", "framework", "library", "database", "server", "client", "protocol", "algorithm", "architecture", "stack", "tool", "platform", "sdk", "language", "syntax", "compile", "deploy", "infrastructure", "container", "microservice"]
BUSINESS_IMPACT_KEYWORDS = ["revenue", "profit", "cost", "efficiency", "productivity", "customer", "user", "market", "business", "strategy", "goal", "objective", "kpi", "metric", "roi", "value", "impact", "outcome", "result", "benefit"]


def _split_sentences(text: str) -> List[str]:
    sentences = re.split(r'[.!?\n]\s*', text)
    sentences = [s.strip() for s in sentences if s.strip()]
    filtered_sentences = [s for s in sentences if len(s) >= 10]
    if not filtered_sentences:
        return [text.strip()] if text.strip() else []
    
    return filtered_sentences


def _is_tech_focused(sentence: str) -> bool:
    sentence_lower = sentence.lower()
    tech_count = sum(1 for kw in TECH_KEYWORDS if kw in sentence_lower)
    business_count = sum(1 for kw in BUSINESS_IMPACT_KEYWORDS if kw in sentence_lower)
    return tech_count > business_count


def _calculate_sentence_scores(sentences: List[str]) -> Dict[str, List[Tuple[str, float]]]:
    if not sentences:
        return {axis: [] for axis in AXES}
    
    sentence_embeddings = generate_embeddings(sentences)
    
    axis_texts = [f"technology category: {axis.lower()}" for axis in AXES]
    axis_embeddings = generate_embeddings(axis_texts)
    
    similarities = cosine_similarity(sentence_embeddings, axis_embeddings)
    
    axis_scores = {axis: [] for axis in AXES}
    
    for idx, sentence in enumerate(sentences):
        sentence_lower = sentence.lower()
        
        for axis_idx, axis in enumerate(AXES):
            if axis == "OTHER":
                continue
            
            embedding_score = float(similarities[idx][axis_idx])
            
            keyword_score = 0.0
            keywords = AXIS_KEYWORDS.get(axis, [])
            for keyword in keywords:
                if keyword in sentence_lower:
                    keyword_score += 0.3
            
            combined_score = embedding_score + min(keyword_score, 0.5)
            
            if not _is_tech_focused(sentence) and axis != "OTHER":
                combined_score *= 0.3
            
            axis_scores[axis].append((sentence, combined_score))
    
    return axis_scores


def analyze_stage(stage: Dict) -> Dict:
    content = stage.get("content", "")
    if not content:
        return {axis: [] for axis in AXES}
    
    sentences = _split_sentences(content)
    
    if len(sentences) < 10:
        return {axis: [] for axis in AXES}
    
    axis_scores = _calculate_sentence_scores(sentences)
    
    result = {}
    top_k = 5
    
    for axis in AXES:
        scored_sentences = axis_scores[axis]
        scored_sentences.sort(key=lambda x: x[1], reverse=True)
        top_sentences = [s[0] for s in scored_sentences[:top_k]]
        result[axis] = top_sentences
    
    return result


def _extract_user_query_keywords(battle_data: Dict) -> List[str]:
    keyword_scores = defaultdict(lambda: {"damages": [], "days_since": []})
    keyword_counts = Counter()
    
    for axis in AXES:
        if axis == "OTHER":
            continue
        axis_details = battle_data["axis_data"].get(axis, {}).get("details", [])
        
        for detail in axis_details:
            keywords = detail.get("keywords", [])
            damage = detail.get("damage", 0)
            days = detail.get("days_since", 0)
            
            for keyword in keywords:
                keyword_upper = keyword.upper()
                keyword_scores[keyword_upper]["damages"].append(damage)
                keyword_scores[keyword_upper]["days_since"].append(days)
                keyword_counts[keyword_upper] += 1
    
    strong_keywords = []
    for keyword, data in keyword_scores.items():
        if data["damages"]:
            score = calculate_weighted_average_score(
                damages=data["damages"],
                days_since=data["days_since"]
            )
            strong_keywords.append((keyword, score))
    
    strong_keywords.sort(key=lambda x: x[1], reverse=True)
    frequent_keywords = keyword_counts.most_common()
    selected_keywords = []
    used_keywords = set()
    
    for keyword, score in strong_keywords[:5]:
        if keyword not in used_keywords:
            selected_keywords.append(keyword)
            used_keywords.add(keyword)
    
    for keyword, count in frequent_keywords:
        if len(selected_keywords) >= 5:
            break
        if keyword not in used_keywords:
            selected_keywords.append(keyword)
            used_keywords.add(keyword)
    if len(selected_keywords) < 5:
        weak_keywords = []
        for keyword, data in keyword_scores.items():
            if keyword not in used_keywords and data["damages"]:
                score = calculate_weighted_average_score(
                    damages=data["damages"],
                    days_since=data["days_since"]
                )
                weak_keywords.append((keyword, score))
        
        weak_keywords.sort(key=lambda x: x[1])
        
        for keyword, score in weak_keywords:
            if len(selected_keywords) >= 5:
                break
            if keyword not in used_keywords:
                selected_keywords.append(keyword)
                used_keywords.add(keyword)
    
    return selected_keywords[:5]


def recommend_stages_for_user_keywords(
    battle_data: Dict,
    stages: List[Dict],
    top_n: int = 5
) -> List[Dict]:
    try:
        query_keywords = _extract_user_query_keywords(battle_data)
        
        if not query_keywords:
            logger.warning("[추천 생성] 쿼리 키워드가 없습니다")
            return []
        
        if not stages:
            logger.warning("[추천 생성] 스테이지 데이터가 없습니다")
            return []
        
        logger.info(f"[추천 생성] 쿼리 키워드: {query_keywords}, 스테이지 개수: {len(stages)}")
        
        # 1단계: 간단한 키워드 매칭으로 빠르게 필터링
        query_keywords_lower = [kw.lower() for kw in query_keywords]
        stage_scores = []
        
        for stage in stages:
            try:
                content = stage.get("content", "")
                title = stage.get("title", "")
                company_name = stage.get("company_name", "")
                job_category = stage.get("job_category", "")
                
                if not content:
                    continue
                
                # 전체 텍스트를 소문자로 변환
                full_text = f"{title} {company_name} {job_category} {content}".lower()
                
                # 키워드 매칭 점수 계산
                match_count = 0
                matched_keywords = []
                
                for keyword in query_keywords_lower:
                    if keyword in full_text:
                        match_count += 1
                        matched_keywords.append(keyword.upper())
                
                if match_count > 0:
                    # 매칭된 키워드 수로 점수 계산
                    score = match_count / len(query_keywords_lower)
                    
                    stage_scores.append({
                        "stage_id": stage.get("stage_id"),
                        "content": content,
                        "title": title,
                        "company_name": company_name,
                        "job_category": job_category,
                        "score": score,
                        "matchedKeywords": matched_keywords,
                        "stage": stage  # 전체 stage 정보 저장
                    })
            except Exception as e:
                logger.warning(f"[추천 생성] 스테이지 {stage.get('stage_id')} 처리 실패: {str(e)}")
                continue
        
        # 점수순으로 정렬
        stage_scores.sort(key=lambda x: x["score"], reverse=True)
        
        # 상위 5개만 선택 (embedding 계산용)
        top_stages_for_embedding = stage_scores[:5]
        
        if not top_stages_for_embedding:
            logger.warning("[추천 생성] 키워드 매칭된 스테이지가 없습니다")
            return []
        
        logger.info(f"[추천 생성] 키워드 매칭 완료 - 상위 5개 선택, embedding 계산 시작")
        
        # 2단계: 상위 5개만 embedding으로 정확도 재계산
        user_query = " ".join(query_keywords)
        query_embedding = generate_embeddings([user_query], mode="query", normalize=True)
        
        final_recommendations = []
        
        for stage_data in top_stages_for_embedding:
            try:
                content = stage_data["content"]
                sentences = _split_sentences(content)
                
                if not sentences:
                    # 문장이 없으면 키워드 매칭 점수 사용
                    final_recommendations.append({
                        "stage_id": stage_data["stage_id"],
                        "content": content,
                        "title": stage_data.get("title", ""),
                        "company_name": stage_data.get("company_name", ""),
                        "job_category": stage_data.get("job_category", ""),
                        "similarity_score": stage_data["score"],
                        "matchedKeywords": stage_data["matchedKeywords"],
                        "reason_code": "KEYWORD_MATCH",
                        "reason_text": f"User keywords ({', '.join(stage_data['matchedKeywords'])}) match stage content.",
                        "evidence": []
                    })
                    continue
                
                # Embedding으로 정확도 계산
                sentence_embeddings = generate_embeddings(sentences, mode="passage", normalize=True)
                similarities = np.dot(query_embedding, sentence_embeddings.T)[0]
                top_k = min(3, len(similarities))
                top_indices = np.argsort(similarities)[::-1][:top_k]
                embedding_score = float(np.mean([similarities[idx] for idx in top_indices]))
                
                # 키워드 매칭 점수와 embedding 점수 결합 (가중 평균)
                combined_score = (stage_data["score"] * 0.3) + (embedding_score * 0.7)
                
                evidence = []
                for idx in top_indices:
                    evidence.append({
                        "sentence": sentences[idx][:200],  # 최대 200자
                        "similarity": float(similarities[idx])
                    })
                
                keywords_str = ", ".join(stage_data["matchedKeywords"])
                final_recommendations.append({
                    "stage_id": stage_data["stage_id"],
                    "content": content,
                    "title": stage_data.get("title", ""),
                    "company_name": stage_data.get("company_name", ""),
                    "job_category": stage_data.get("job_category", ""),
                    "similarity_score": combined_score,
                    "matchedKeywords": stage_data["matchedKeywords"],
                    "reason_code": "HYBRID",
                    "reason_text": f"User keywords ({keywords_str}) match stage content with high similarity.",
                    "evidence": evidence
                })
            except Exception as e:
                logger.warning(f"[추천 생성] Embedding 계산 실패 (stage_id: {stage_data['stage_id']}): {str(e)}")
                # Embedding 실패 시 키워드 매칭 점수만 사용
                keywords_str = ", ".join(stage_data["matchedKeywords"])
                final_recommendations.append({
                    "stage_id": stage_data["stage_id"],
                    "content": stage_data["content"],
                    "title": stage_data.get("title", ""),
                    "company_name": stage_data.get("company_name", ""),
                    "job_category": stage_data.get("job_category", ""),
                    "similarity_score": stage_data["score"],
                    "matchedKeywords": stage_data["matchedKeywords"],
                    "reason_code": "KEYWORD_MATCH",
                    "reason_text": f"User keywords ({keywords_str}) match stage content.",
                    "evidence": []
                })
        
        # 최종 점수순 정렬
        final_recommendations.sort(key=lambda x: x["similarity_score"], reverse=True)
        
        logger.info(f"[추천 생성] 완료 - 추천 개수: {len(final_recommendations)}")
        return final_recommendations[:top_n]
        
    except Exception as e:
        logger.error(f"[추천 생성] 에러 발생: {str(e)}", exc_info=True)
        return []