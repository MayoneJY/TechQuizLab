# -*- coding: utf-8 -*-
from typing import Dict, List, Optional
from collections import Counter, defaultdict
import numpy as np
from app.services.tag_mapper import AXES, AXIS_KEYWORDS
from app.services.embedding import generate_embeddings
from app.services.scoring import calculate_score


def _cosine_similarity(a: np.ndarray, b: np.ndarray) -> np.ndarray:
    a_norm = a / (np.linalg.norm(a, axis=-1, keepdims=True) + 1e-8)
    b_norm = b / (np.linalg.norm(b, axis=-1, keepdims=True) + 1e-8)
    return np.dot(a_norm, b_norm.T)


def build_user_experience_profile(battle_data: Dict) -> str:
    axis_weights = {}
    axis_keywords_list = defaultdict(list)
    
    for axis in AXES:
        axis_info = battle_data["axis_data"].get(axis, {})
        damages = axis_info.get("damages", [])
        days_since = axis_info.get("days_since", [])
        difficulty_weights = axis_info.get("difficulty_weights", [])
        details = axis_info.get("details", [])
        
        if damages:
            score = calculate_score(
                damages=damages,
                days_since=days_since,
                difficulty_weights=difficulty_weights if difficulty_weights else None
            )
            axis_weights[axis] = score
            
            for detail in details:
                keywords = detail.get("keywords", [])
                days = detail.get("days_since", 0)
                damage_score = detail.get("damage_score", 0.0)
                time_weight = 0.5 ** (days / 30)
                performance_weight = damage_score / 100.0
                weight = time_weight * performance_weight
                
                for keyword in keywords:
                    axis_keywords_list[axis].append((keyword.upper(), weight))
    
    sorted_axes = sorted(
        [(axis, score) for axis, score in axis_weights.items() if axis != "OTHER"],
        key=lambda x: x[1],
        reverse=True
    )[:3]
    
    profile_parts = []
    
    for axis, axis_score in sorted_axes:
        keyword_weights = defaultdict(float)
        for keyword, weight in axis_keywords_list[axis]:
            keyword_weights[keyword] += weight
        
        sorted_keywords = sorted(
            keyword_weights.items(),
            key=lambda x: x[1],
            reverse=True
        )[:5]
        
        keywords_text = ", ".join([kw for kw, _ in sorted_keywords])
        
        if keywords_text:
            axis_description = AXIS_KEYWORDS.get(axis, [])
            axis_desc_text = " ".join(axis_description[:3])
            profile_parts.append(f"{axis_desc_text} {keywords_text}")
    
    return " ".join(profile_parts)


def recommend_by_association(
    battle_data: Dict,
    stage_content: str,
    top_k: int = 5
) -> List[Dict]:
    user_profile = build_user_experience_profile(battle_data)
    
    if not user_profile or not user_profile.strip():
        return []
    
    profile_embedding = generate_embeddings([user_profile])
    stage_embedding = generate_embeddings([stage_content])
    similarity = _cosine_similarity(profile_embedding, stage_embedding)[0][0]
    
    return [{
        "similarity_score": float(similarity),
        "user_profile": user_profile,
        "recommendation_type": "ASSOCIATION"
    }]

