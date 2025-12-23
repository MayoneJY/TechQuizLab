# -*- coding: utf-8 -*-
from typing import List, Dict, Optional
from datetime import datetime
from app.repositories.battle_detail_repository import get_user_battle_details
from app.utils.tag_parser import parse_battle_tags, extract_axis_from_tags
from app.services.tag_mapper import AXES


def convert_damage_to_score(damage: int) -> float:
    return max(0.0, min(100.0, damage / 10.0))


def convert_difficulty_to_weight(difficulty: str) -> float:
    difficulty_upper = difficulty.upper() if difficulty else "MEDIUM"
    
    difficulty_map = {
        "EASY": 0.5,
        "MEDIUM": 1.0,
        "HARD": 1.5,
        "BEGINNER": 0.5,
        "INTERMEDIATE": 1.0,
        "ADVANCED": 1.5,
        "EXPERT": 1.5
    }
    
    return difficulty_map.get(difficulty_upper, 1.0)


def calculate_days_since(created_at_str: str) -> int:
    try:
        if isinstance(created_at_str, str):
            if 'T' in created_at_str:
                created_at = datetime.fromisoformat(created_at_str.replace('Z', '+00:00'))
            else:
                created_at = datetime.strptime(created_at_str, "%Y-%m-%d %H:%M:%S")
        else:
            created_at = created_at_str
        
        now = datetime.now()
        delta = now - created_at.replace(tzinfo=None) if created_at.tzinfo else now - created_at
        return max(0, delta.days)
    except Exception:
        return 0


def process_user_battle_data(user_id: int, limit: Optional[int] = None) -> Dict:
    battle_details = get_user_battle_details(user_id, limit=limit)
    
    tags = []
    damages = []
    days_since = []
    difficulty_weights = []
    tag_mappings = []
    axis_data = {axis: {
        "damages": [],
        "days_since": [],
        "difficulty_weights": [],
        "details": []
    } for axis in AXES}
    
    for detail in battle_details:
        keyword_tags = detail.get("keyword_tags", "")
        if not keyword_tags:
            continue
        
        parsed = parse_battle_tags(keyword_tags)
        if not parsed:
            continue
        
        axis, keywords = parsed
        damage = detail.get("damage", 0)
        damage_score = convert_damage_to_score(damage)
        created_at = detail.get("created_at", "")
        days = calculate_days_since(created_at)
        difficulty = detail.get("difficulty", "MEDIUM")
        difficulty_weight = convert_difficulty_to_weight(difficulty)
        
        tags.append(keyword_tags)
        damages.append(damage_score)
        days_since.append(days)
        difficulty_weights.append(difficulty_weight)
        
        tag_mappings.append({
            "tag": keyword_tags,
            "axis": axis,
            "keywords": keywords,
            "detail_id": detail.get("detail_id"),
            "question_text": detail.get("question_text", "")[:100]
        })
        if axis in axis_data:
            axis_data[axis]["damages"].append(damage_score)
            axis_data[axis]["days_since"].append(days)
            axis_data[axis]["difficulty_weights"].append(difficulty_weight)
            axis_data[axis]["details"].append({
                "detail_id": detail.get("detail_id"),
                "question_text": detail.get("question_text", ""),
                "keyword_tags": keyword_tags,
                "keywords": keywords,
                "damage": damage,
                "damage_score": damage_score,
                "days_since": days,
                "difficulty": difficulty,
                "difficulty_weight": difficulty_weight,
                "created_at": created_at
            })
    
    return {
        "tags": tags,
        "damages": damages,
        "days_since": days_since,
        "difficulty_weights": difficulty_weights,
        "tag_mappings": tag_mappings,
        "axis_data": axis_data
    }

