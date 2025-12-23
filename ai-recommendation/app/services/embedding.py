# -*- coding: utf-8 -*-
from sentence_transformers import SentenceTransformer
import torch
from typing import List, Literal
import numpy as np


_model = None


def _get_model():
    global _model
    if _model is None:
        _model = SentenceTransformer('intfloat/multilingual-e5-small', device='cpu')
    return _model


def generate_embeddings(texts: List[str], mode: Literal["query", "passage"] = "passage", normalize: bool = True) -> np.ndarray:
    model = _get_model()
    if mode == "query":
        prefixed_texts = [f"query: {text}" for text in texts]
    else:
        prefixed_texts = [f"passage: {text}" for text in texts]
    
    embeddings = model.encode(prefixed_texts, convert_to_numpy=True, normalize_embeddings=normalize)
    return embeddings

