# -*- coding: utf-8 -*-
import re
from typing import List, Tuple, Literal, Optional
from dataclasses import dataclass
import numpy as np
from app.utils.normalize import normalize_tag
from app.services.embedding import generate_embeddings


AXES = ["TECH", "PROBLEM_SOLVING", "COMMUNICATION", "COLLABORATION", "GROWTH", "BUSINESS_IMPACT"]

AXIS_DESCRIPTIONS = {
    "TECH": "Technical skills and knowledge in programming languages, frameworks, tools, and technologies.",
    "PROBLEM_SOLVING": "Ability to analyze problems, design solutions, debug issues, and think critically.",
    "COMMUNICATION": "Skills in written and verbal communication, documentation, presentation, and knowledge sharing.",
    "COLLABORATION": "Ability to work in teams, coordinate with others, manage conflicts, and contribute to group goals.",
    "GROWTH": "Continuous learning, self-improvement, adaptability, and willingness to take on new challenges.",
    "BUSINESS_IMPACT": "Understanding business needs, delivering value, measuring outcomes, and aligning work with organizational goals."
}

AXIS_KEYWORDS = {
    "TECH": ["programming", "code", "algorithm", "framework", "library", "api", "database", "server", "deploy", "infrastructure", "docker", "kubernetes", "aws", "python", "javascript", "java", "react", "spring"],
    "PROBLEM_SOLVING": ["debug", "troubleshoot", "analyze", "solve", "design", "architecture", "optimize", "refactor", "fix", "issue", "problem", "solution", "algorithm", "logic"],
    "COMMUNICATION": ["document", "present", "explain", "write", "speak", "presentation", "meeting", "report", "email", "wiki", "blog", "tutorial", "guide"],
    "COLLABORATION": ["team", "collaborate", "coordinate", "pair", "review", "mentor", "lead", "manage", "scrum", "agile", "meeting", "standup"],
    "GROWTH": ["learn", "study", "improve", "adapt", "challenge", "growth", "skill", "training", "course", "certification", "practice", "experiment"],
    "BUSINESS_IMPACT": ["business", "value", "impact", "metric", "kpi", "roi", "revenue", "cost", "efficiency", "productivity", "customer", "user", "stakeholder"]
}

AXIS_REGEX = {
    "TECH": [r"^programming", r"^coding", r"^api", r"^framework", r"^library", r"^database", r"^server", r"^deploy", r"^docker", r"^kubernetes"],
    "PROBLEM_SOLVING": [r"^debug", r"^troubleshoot", r"^analyze", r"^solve", r"^design", r"^optimize", r"^refactor"],
    "COMMUNICATION": [r"^document", r"^present", r"^write", r"^explain", r"^report", r"^tutorial"],
    "COLLABORATION": [r"^team", r"^collaborate", r"^pair", r"^review", r"^mentor", r"^lead"],
    "GROWTH": [r"^learn", r"^study", r"^improve", r"^adapt", r"^training", r"^course"],
    "BUSINESS_IMPACT": [r"^business", r"^value", r"^metric", r"^kpi", r"^roi", r"^revenue"]
}

_axis_embeddings = None
_axis_order = None


@dataclass
class TagAxisMatch:
    axis_id: str
    confidence: float
    source: Literal["RULE", "EMBEDDING", "OTHER"]
    evidence: dict
    normalized_tag: str


def _get_axis_embeddings():
    global _axis_embeddings, _axis_order
    if _axis_embeddings is None:
        axis_descriptions = [AXIS_DESCRIPTIONS[axis] for axis in AXES]
        _axis_embeddings = generate_embeddings(axis_descriptions)
        _axis_order = AXES 
    return _axis_embeddings, _axis_order


def _cosine_similarity(a: np.ndarray, b: np.ndarray) -> np.ndarray:
    a_norm = a / (np.linalg.norm(a, axis=-1, keepdims=True) + 1e-8) 
    b_norm = b / (np.linalg.norm(b, axis=-1, keepdims=True) + 1e-8) 
    return np.dot(a_norm, b_norm.T) 


def _rule_based_match(normalized_tag: str) -> Optional[dict]:
    tag_lower = normalized_tag.lower()
    matched_keywords = []
    matched_regex = []
    matched_axis = None
    
    for axis in AXES:
        if axis == "OTHER":
            continue
        
        keywords = AXIS_KEYWORDS.get(axis, [])
        for keyword in keywords:
            if keyword in tag_lower:
                matched_keywords.append(keyword)
                matched_axis = axis
        
        patterns = AXIS_REGEX.get(axis, [])
        for pattern in patterns:
            if re.search(pattern, tag_lower, re.IGNORECASE):
                matched_regex.append(pattern)
                matched_axis = axis
        
        if matched_axis:
            break
    
    if matched_axis:
        return {
            "axis": matched_axis,
            "evidence": {
                "matched_keywords": matched_keywords,
                "matched_regex": matched_regex,
                "axis": matched_axis
            }
        }
    return None


def _embedding_based_match(normalized_tag: str, low_confidence_threshold: float) -> dict:
    axis_embeddings, axis_order = _get_axis_embeddings()
    tag_embedding = generate_embeddings([normalized_tag])
    
    similarities = _cosine_similarity(tag_embedding, axis_embeddings)[0]
    
    top_indices = np.argsort(similarities)[::-1]
    top3_results = [
        {"axis": axis_order[idx], "sim": float(similarities[idx])}
        for idx in top_indices[:3]
    ]
    
    top1_sim = similarities[top_indices[0]]
    top2_sim = similarities[top_indices[1]] if len(top_indices) > 1 else 0.0
    
    confidence = max(0.0, top1_sim - top2_sim)
    selected_axis = axis_order[top_indices[0]]
    
    if confidence < low_confidence_threshold:
        return {
            "axis": "OTHER",
            "confidence": confidence,
            "evidence": {
                "top3": top3_results,
                "selected_axis": selected_axis,
                "method": "cosine_margin",
                "threshold": low_confidence_threshold
            }
        }
    
    return {
        "axis": selected_axis,
        "confidence": confidence,
        "evidence": {
            "top3": top3_results,
            "selected_axis": selected_axis,
            "method": "cosine_margin"
        }
    }


def map_tag_to_axis(
    tag_text: str,
    low_confidence_threshold: float = 0.08
) -> TagAxisMatch:
    if not tag_text or not tag_text.strip():
        return TagAxisMatch(
            axis_id="OTHER",
            confidence=0.0,
            source="OTHER",
            evidence={"reason": "EMPTY"},
            normalized_tag=""
        )
    
    normalized = normalize_tag(tag_text)
    if not normalized:
        return TagAxisMatch(
            axis_id="OTHER",
            confidence=0.0,
            source="OTHER",
            evidence={"reason": "INVALID"},
            normalized_tag=""
        )
    
    rule_result = _rule_based_match(normalized)
    if rule_result:
        return TagAxisMatch(
            axis_id=rule_result["axis"],
            confidence=1.0,
            source="RULE",
            evidence=rule_result["evidence"],
            normalized_tag=normalized
        )
    
    embedding_result = _embedding_based_match(normalized, low_confidence_threshold)
    source = "EMBEDDING" if embedding_result["axis"] != "OTHER" else "OTHER"
    
    return TagAxisMatch(
        axis_id=embedding_result["axis"],
        confidence=embedding_result["confidence"],
        source=source,
        evidence=embedding_result["evidence"],
        normalized_tag=normalized
    )


def map_tags_to_axes(
    tags: List[str],
    low_confidence_threshold: float = 0.08
) -> Tuple[List[TagAxisMatch], List[dict]]:
    if not tags:
        return [], []
    
    matches = []
    unmapped_tags = []
    
    for tag in tags:
        match = map_tag_to_axis(tag, low_confidence_threshold)
        matches.append(match)
        
        if match.source == "OTHER":
            reason = match.evidence.get("reason", "LOW_CONFIDENCE")
            if reason == "LOW_CONFIDENCE" and match.confidence < low_confidence_threshold:
                reason = "LOW_CONFIDENCE"
            unmapped_tags.append({
                "tag": tag,
                "reason": reason
            })
    
    return matches, unmapped_tags


def map_tag(tag_text: str, low_confidence_threshold: float = 0.08) -> dict:
    match = map_tag_to_axis(tag_text, low_confidence_threshold)
    return {
        "axis": match.axis_id,
        "confidence": match.confidence,
        "evidence": match.evidence
    }

