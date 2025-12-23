# -*- coding: utf-8 -*-
from typing import List, Optional, Tuple


def parse_battle_tags(keyword_tags: str) -> Optional[Tuple[str, List[str]]]:
    if not keyword_tags or not keyword_tags.strip():
        return None
    
    tags = [tag.strip().upper() for tag in keyword_tags.split(",") if tag.strip()]
    
    if len(tags) < 2:
        return None
    
    axis = tags[0]
    keywords = tags[1:]
    valid_axes = ["TECH", "PROBLEM_SOLVING", "COMMUNICATION", "COLLABORATION", "GROWTH", "BUSINESS_IMPACT"]
    if axis not in valid_axes:
        return None
    
    return (axis, keywords)


def extract_axis_from_tags(keyword_tags: str) -> Optional[str]:
    parsed = parse_battle_tags(keyword_tags)
    if parsed:
        return parsed[0]
    return None

