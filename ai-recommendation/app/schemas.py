# -*- coding: utf-8 -*-
from pydantic import BaseModel
from typing import Optional, List, Dict


class RecommendationRequest(BaseModel):
    tags: List[str]
    stage: Dict
    required_scores: Dict[str, float]
    damages: List[float]
    days_since: List[int]
    difficulty_weights: Optional[List[float]] = None
    snapshot_id: Optional[str] = None


class RecommendationResponse(BaseModel):
    snapshot_id: str
    normalized_tags: List[str]
    tag_mappings: List[Dict]
    current_scores: Dict[str, float]
    stage_evidence: Dict[str, List[str]]
    recommendation: Dict


class BattleBasedRecommendationRequest(BaseModel):
    user_id: int
    required_scores: Dict[str, float]
    stage: Dict
    limit: Optional[int] = None
    snapshot_id: Optional[str] = None


class BattleBasedRecommendationResponse(BaseModel):
    snapshot_id: str
    user_id: int
    tag_mappings: List[Dict]
    current_scores: Dict[str, float]
    weakness_recommendation: Dict
    association_recommendations: List[Dict]
    battle_summary: Dict