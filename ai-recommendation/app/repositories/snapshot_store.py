# -*- coding: utf-8 -*-
import json
import os
import threading
from typing import Optional, Dict
from pathlib import Path


_STORE_DIR = Path("data/snapshots")
_LOCK = threading.Lock()


def _ensure_dir():
    _STORE_DIR.mkdir(parents=True, exist_ok=True)


def _get_snapshot_path(snapshot_id: str) -> Path:
    return _STORE_DIR / f"{snapshot_id}.json"


def save_snapshot(snapshot_id: str, data: Dict):
    with _LOCK:
        _ensure_dir()
        snapshot_path = _get_snapshot_path(snapshot_id)
        with open(snapshot_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)


def get_snapshot(snapshot_id: str) -> Optional[Dict]:
    with _LOCK:
        snapshot_path = _get_snapshot_path(snapshot_id)
        if not snapshot_path.exists():
            return None
        
        try:
            with open(snapshot_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except (json.JSONDecodeError, IOError):
            return None


def upsert_snapshot(snapshot_id: str, data: Dict):
    with _LOCK:
        snapshot_path = _get_snapshot_path(snapshot_id)
        existing = None
        if snapshot_path.exists():
            try:
                with open(snapshot_path, 'r', encoding='utf-8') as f:
                    existing = json.load(f)
            except (json.JSONDecodeError, IOError):
                existing = None
        
        if existing:
            existing.update(data)
            _ensure_dir()
            with open(snapshot_path, 'w', encoding='utf-8') as f:
                json.dump(existing, f, ensure_ascii=False, indent=2)
        else:
            _ensure_dir()
            with open(snapshot_path, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)

