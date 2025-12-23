# -*- coding: utf-8 -*-
import re
from typing import Optional


def normalize_tag(tag_text: str) -> Optional[str]:
    if not tag_text:
        return None
    
    text = tag_text.strip(',').lower()
    
    text = text.replace('c++', 'C_PLUS_PLUS_PLACEHOLDER')
    text = text.replace('c#', 'C_SHARP_PLACEHOLDER')
    text = text.replace('node.js', 'NODE_JS_PLACEHOLDER')
    
    text = re.sub(r'[\s\-_]', ' ', text)
    
    text = re.sub(r'[^\w\s]', '', text)
    
    text = text.replace('C_PLUS_PLUS_PLACEHOLDER', 'c++')
    text = text.replace('C_SHARP_PLACEHOLDER', 'c#')
    text = text.replace('NODE_JS_PLACEHOLDER', 'node.js')
    
    text = re.sub(r'\s+', ' ', text)
    text = text.strip()
    
    if len(text) < 2:
        return None
    
    return text

