# -*- coding: utf-8 -*-
import numpy as np
from typing import List, Dict, Optional
from math import log


def calculate_weighted_average_score(
    damages: List[float],
    days_since: List[int]
) -> float:
    if not damages or len(damages) == 0:
        return 0.0
    
    damage_scaled = [log(1 + max(d, 0)) for d in damages]
    time_weights = [0.5 ** (ds / 30) for ds in days_since]
    weighted_sum = sum(d * w for d, w in zip(damage_scaled, time_weights))
    total_weight = sum(time_weights)
    
    if total_weight == 0:
        return 0.0
    
    return weighted_sum / total_weight


def calculate_score(
    damages: List[float],
    days_since: List[int],
    difficulty_weights: Optional[List[float]] = None,
    axis_weights: Optional[Dict[str, float]] = None
) -> float:
    if not damages or len(damages) == 0:
        return 0.0
    
    n = len(damages)
    
    damage_scaled = [log(1 + d) for d in damages]
    
    time_weights = [0.5 ** (ds / 30) for ds in days_since]
    
    if difficulty_weights is None:
        difficulty_weights = [1.0] * n
    
    final_weights = [tw * dw for tw, dw in zip(time_weights, difficulty_weights)]
    
    if axis_weights:
        weighted_values = []
        for i, (damage, weight) in enumerate(zip(damage_scaled, final_weights)):
            axis = list(axis_weights.keys())[i % len(axis_weights)]
            axis_weight = axis_weights.get(axis, 1.0)
            weighted_values.append(damage * weight * axis_weight)
        
        weighted_sum = sum(weighted_values)
        total_weight = sum([w * axis_weights.get(list(axis_weights.keys())[i % len(axis_weights)], 1.0) 
                           for i, w in enumerate(final_weights)])
    else:
        weighted_sum = sum(d * w for d, w in zip(damage_scaled, final_weights))
        total_weight = sum(final_weights)
    
    if total_weight == 0:
        return 0.0
    
    raw_score = weighted_sum / total_weight
    
    if n >= 5:
        p10 = np.percentile(damage_scaled, 10)
        p90 = np.percentile(damage_scaled, 90)
        clipped = [max(p10, min(p90, d)) for d in damage_scaled]
        
        min_val = min(clipped)
        max_val = max(clipped)
        
        if max_val == min_val:
            normalized = 50.0
        else:
            normalized = ((raw_score - min_val) / (max_val - min_val)) * 100
    else:
        sigmoid_score = 1 / (1 + np.exp(-raw_score))
        normalized = sigmoid_score * 100
    
    return max(0.0, min(100.0, normalized))

