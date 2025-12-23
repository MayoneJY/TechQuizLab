# -*- coding: utf-8 -*-
from typing import Dict, List, Optional
from collections import defaultdict
from math import log
from app.services.tag_mapper import AXES
from app.services.scoring import calculate_score, calculate_weighted_average_score


def calculate_keyword_scores(battle_data: Dict) -> Dict[str, Dict[str, Dict]]:
    keyword_data = {axis: defaultdict(lambda: {"damages": [], "days_since": [], "battles": []}) 
                    for axis in AXES}
    for axis in AXES:
        axis_details = battle_data["axis_data"].get(axis, {}).get("details", [])
        
        for detail in axis_details:
            keywords = detail.get("keywords", [])
            damage = detail.get("damage", 0)  # 원본 damage (0-1000)
            days = detail.get("days_since", 0)
            keyword_tags = detail.get("keyword_tags", "")
            
            for keyword in keywords:
                keyword_upper = keyword.upper()
                keyword_data[axis][keyword_upper]["damages"].append(damage)
                keyword_data[axis][keyword_upper]["days_since"].append(days)
                keyword_data[axis][keyword_upper]["battles"].append({
                    "question_text": detail.get("question_text", ""),
                    "keyword_tags": keyword_tags,
                    "damage": damage,
                    "created_at": detail.get("created_at", "")
                })
    
    result = {}
    for axis in AXES:
        result[axis] = {}
        for keyword, data in keyword_data[axis].items():
            if data["damages"]:
                score = calculate_weighted_average_score(
                    damages=data["damages"],
                    days_since=data["days_since"]
                )
                result[axis][keyword] = {
                    "score": score,
                    "battles": data["battles"]
                }
    
    return result


def recommend_weakness_keywords(
    battle_data: Dict,
    axis_scores: Dict[str, float]
) -> Dict:
    keyword_data = calculate_keyword_scores(battle_data)
    all_keywords = []
    for axis in AXES:
        if axis == "OTHER":
            continue
        for keyword, data in keyword_data.get(axis, {}).items():
            all_keywords.append({
                "keyword": keyword,
                "axis": axis,
                "score": data["score"],
                "battles": data["battles"]
            })
    
    if not all_keywords:
        return {
            "weakKeywords": [],
            "learningPlan": []
        }
    
    all_keywords.sort(key=lambda x: x["score"])
    weak_keywords = []
    for kw_data in all_keywords[:10]:
        evidence = kw_data["battles"][:2]
        weak_keywords.append({
            "keyword": kw_data["keyword"],
            "axis": kw_data["axis"],
            "score": kw_data["score"],
            "reason_code": "WEAK_KEYWORD",
            "reason_text": f"{kw_data['axis']} axis keyword {kw_data['keyword']} has low score ({kw_data['score']:.2f}).",
            "evidence": evidence
        })
    
    learning_plan = []
    axis_counts = defaultdict(int)
    used_keyword_combinations = set()  # 이미 사용된 키워드 조합 추적
    
    for kw_data in all_keywords:
        if len(learning_plan) >= 3:
            break
        
        axis = kw_data["axis"]
        if axis_counts[axis] >= 2:
            continue
        
        same_axis_keywords = [
            item for item in all_keywords
            if item["axis"] == axis and item["keyword"] != kw_data["keyword"]
        ]
        
        if same_axis_keywords:
            second_kw = same_axis_keywords[0]
            # 키워드 조합을 정렬해서 튜플로 만들어 중복 체크
            keyword_combination = tuple(sorted([kw_data["keyword"], second_kw["keyword"]]))
            
            # 이미 사용된 조합이면 스킵
            if keyword_combination in used_keyword_combinations:
                continue
            
            used_keyword_combinations.add(keyword_combination)
            combined_evidence = (kw_data["battles"][:1] + second_kw["battles"][:1])
            learning_plan.append({
                "keywords": [kw_data["keyword"], second_kw["keyword"]],
                "axis": axis,
                "reason_code": "WEAK_AXIS",
                "reason_text": f"Focus on weak keywords ({kw_data['keyword']}, {second_kw['keyword']}) in {axis} axis.",
                "evidence": combined_evidence
            })
            axis_counts[axis] += 1
        else:
            # 단일 키워드도 조합으로 처리 (중복 방지)
            keyword_combination = tuple(sorted([kw_data["keyword"]]))
            
            if keyword_combination in used_keyword_combinations:
                continue
            
            used_keyword_combinations.add(keyword_combination)
            learning_plan.append({
                "keywords": [kw_data["keyword"]],
                "axis": axis,
                "reason_code": "WEAK_KEYWORD",
                "reason_text": f"Focus on keyword {kw_data['keyword']} in {axis} axis.",
                "evidence": kw_data["battles"][:2]
            })
            axis_counts[axis] += 1
    
    return {
        "weakKeywords": weak_keywords,
        "learningPlan": learning_plan
    }