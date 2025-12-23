# -*- coding: utf-8 -*-
import os
from typing import List, Dict, Optional
from datetime import datetime, timedelta
import pymysql
from pymysql.cursors import DictCursor
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()


def get_db_connection():
    return pymysql.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", 3306)),
        user=os.getenv("DB_USER", "root"),
        password=os.getenv("DB_PASSWORD", ""),
        database=os.getenv("DB_NAME", "your_database"),
        charset="utf8mb4",
        cursorclass=DictCursor
    )


def get_user_battle_details(user_id: int, limit: Optional[int] = None) -> List[Dict]:
    conn = get_db_connection()
    try:
        with conn.cursor() as cursor:
            if limit:
                query = """
                    SELECT 
                        user_id,
                        battle_id,
                        detail_id,
                        question_text,
                        keyword_tags,
                        difficulty,
                        user_answer,
                        ai_feedback,
                        ai_feedback_good,
                        ai_feedback_bad,
                        damage,
                        created_at
                    FROM battle_detail
                    WHERE user_id = %s
                    ORDER BY created_at DESC
                    LIMIT %s
                """
                cursor.execute(query, (user_id, limit))
            else:
                query = """
                    SELECT 
                        user_id,
                        battle_id,
                        detail_id,
                        question_text,
                        keyword_tags,
                        difficulty,
                        user_answer,
                        ai_feedback,
                        ai_feedback_good,
                        ai_feedback_bad,
                        damage,
                        created_at
                    FROM battle_detail
                    WHERE user_id = %s
                    ORDER BY created_at DESC
                """
                cursor.execute(query, (user_id,))
            
            results = cursor.fetchall()
            
            battle_details = []
            for row in results:
                detail = dict(row)
                if detail.get("created_at"):
                    if isinstance(detail["created_at"], datetime):
                        detail["created_at"] = detail["created_at"].isoformat()
                    else:
                        detail["created_at"] = str(detail["created_at"])
                battle_details.append(detail)
            
            return battle_details
    finally:
        conn.close()


def get_user_battle_details_by_axis(user_id: int, axis: str, limit: Optional[int] = None) -> List[Dict]:
    conn = get_db_connection()
    try:
        with conn.cursor() as cursor:
            if limit:
                query = """
                    SELECT 
                        user_id,
                        battle_id,
                        detail_id,
                        question_text,
                        keyword_tags,
                        difficulty,
                        user_answer,
                        ai_feedback,
                        ai_feedback_good,
                        ai_feedback_bad,
                        damage,
                        created_at
                    FROM battle_detail
                    WHERE user_id = %s 
                    AND keyword_tags LIKE %s
                    ORDER BY created_at DESC
                    LIMIT %s
                """
                cursor.execute(query, (user_id, f"{axis},%", limit))
            else:
                query = """
                    SELECT 
                        user_id,
                        battle_id,
                        detail_id,
                        question_text,
                        keyword_tags,
                        difficulty,
                        user_answer,
                        ai_feedback,
                        ai_feedback_good,
                        ai_feedback_bad,
                        damage,
                        created_at
                    FROM battle_detail
                    WHERE user_id = %s 
                    AND keyword_tags LIKE %s
                    ORDER BY created_at DESC
                """
                cursor.execute(query, (user_id, f"{axis},%"))
            
            results = cursor.fetchall()
            
            battle_details = []
            for row in results:
                detail = dict(row)
                if detail.get("created_at"):
                    if isinstance(detail["created_at"], datetime):
                        detail["created_at"] = detail["created_at"].isoformat()
                    else:
                        detail["created_at"] = str(detail["created_at"])
                battle_details.append(detail)
            
            return battle_details
    finally:
        conn.close()


def get_user_top_job_categories(user_id: int, limit: int = 3) -> List[str]:
    """유저가 많이 푼 job_category 상위 N개 반환"""
    conn = get_db_connection()
    try:
        with conn.cursor() as cursor:
            query = """
                SELECT s.job_category, COUNT(*) as count
                FROM battle_detail bd
                JOIN battle b ON bd.battle_id = b.battle_id AND bd.user_id = b.user_id
                JOIN stage s ON b.stage_id = s.stage_id
                WHERE bd.user_id = %s
                AND s.job_category IS NOT NULL
                GROUP BY s.job_category
                ORDER BY count DESC
                LIMIT %s
            """
            cursor.execute(query, (user_id, limit))
            results = cursor.fetchall()
            
            job_categories = [row['job_category'] for row in results if row['job_category']]
            return job_categories
    finally:
        conn.close()
