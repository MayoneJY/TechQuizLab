# -*- coding: utf-8 -*-
from typing import Dict, List, Optional
from app.services.tag_mapper import AXES, AXIS_KEYWORDS


def _calculate_gaps(required_scores: Dict[str, float], current_scores: Dict[str, float]) -> Dict[str, float]:
    gaps = {}
    for axis in AXES:
        required = required_scores.get(axis, 0.0)
        current = current_scores.get(axis, 0.0)
        gaps[axis] = max(0.0, required - current)
    return gaps


def recommend(
    required_scores: Dict[str, float],
    current_scores: Dict[str, float],
    evidence: Optional[Dict[str, List]] = None
) -> Dict:
    if evidence is None:
        evidence = {}
    
    gaps = _calculate_gaps(required_scores, current_scores)
    
    sorted_axes = sorted(gaps.items(), key=lambda x: x[1], reverse=True)
    
    for axis, gap in sorted_axes:
        if gap > 0 and axis != "OTHER":
            keywords = AXIS_KEYWORDS.get(axis, [])
            reason_text = f"{axis} axis current score ({current_scores.get(axis, 0.0):.1f}) is {gap:.1f} points lower than required ({required_scores.get(axis, 0.0):.1f})."
            
            return {
                "axis": axis,
                "keywords": keywords,
                "reason_code": "GAP_FOUND",
                "reason_text": reason_text,
                "evidence": evidence.get(axis, [])[:5],
                "gap": gap
            }
    
    return {
        "axis": "OTHER",
        "keywords": [],
        "reason_code": "NO_GAP",
        "reason_text": "All axis requirements are met.",
        "evidence": [],
        "gap": 0.0
    }

