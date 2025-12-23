# -*- coding: utf-8 -*-
import os
from typing import List, Dict, Optional
from datetime import datetime
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


def get_recent_stages(limit: int = 100) -> List[Dict]:
    conn = get_db_connection()
    try:
        with conn.cursor() as cursor:
            query = """
                SELECT 
                    stage_id,
                    title,
                    company_name,
                    job_category,
                    content,
                    deadline
                FROM stage
                ORDER BY deadline DESC
                LIMIT %s
            """
            cursor.execute(query, (limit,))
            
            results = cursor.fetchall()
            
            stages = []
            for row in results:
                stage = dict(row)
                # deadline 처리
                if stage.get("deadline"):
                    if isinstance(stage["deadline"], datetime):
                        stage["deadline"] = stage["deadline"].isoformat()
                    else:
                        stage["deadline"] = str(stage["deadline"])
                stages.append(stage)
            
            return stages
    finally:
        conn.close()

def get_stages_by_job_categories(job_categories: List[str], limit: int = 100) -> List[Dict]:
    """job_category로 stage 필터링"""
    conn = get_db_connection()
    try:
        with conn.cursor() as cursor:
            if not job_categories:
                # job_category가 없으면 전체 조회
                query = """
                    SELECT 
                        stage_id,
                        title,
                        company_name,
                        job_category,
                        content,
                        deadline
                    FROM stage
                    ORDER BY stage_id DESC
                    LIMIT %s
                """
                cursor.execute(query, (limit,))
            else:
                # job_category로 필터링
                placeholders = ','.join(['%s'] * len(job_categories))
                query = f"""
                    SELECT 
                        stage_id,
                        title,
                        company_name,
                        job_category,
                        content,
                        deadline
                    FROM stage
                    WHERE job_category IN ({placeholders})
                    ORDER BY stage_id DESC
                    LIMIT %s
                """
                cursor.execute(query, job_categories + [limit])
            
            results = cursor.fetchall()
            
            stages = []
            for row in results:
                stage = dict(row)
                if stage.get("deadline"):
                    if isinstance(stage["deadline"], datetime):
                        stage["deadline"] = stage["deadline"].isoformat()
                    else:
                        stage["deadline"] = str(stage["deadline"])
                stages.append(stage)
            
            return stages
    finally:
        conn.close()
