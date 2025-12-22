# -*- coding: utf-8 -*-
import os
import time
import math
import re
from datetime import datetime

import pymysql

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException


# =========================
# 0. MariaDB 연결 / INSERT
# =========================

def get_connection():
    """
    MariaDB 연결 객체 반환.
    ▶ 환경변수가 설정되어 있으면 환경변수 사용, 없으면 기본값 사용
    """
    conn = pymysql.connect(
        host=os.getenv("DB_HOST", "mayonedev.com"),
        user=os.getenv("DB_USER", "ssafybattle"),
        password=os.getenv("DB_PASSWORD", "SSAFYbattleADMIN1324"),
        database=os.getenv("DB_NAME", "ssafybattle2"),
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
        autocommit=False,
    )
    return conn


def insert_stage(conn, company_name, title, job_category, content, deadline, url=None):
    """
    DB에 채용공고 정보 저장.
    url 파라미터는 추후 확장용 (현재는 테이블에 url 컬럼이 없으면 사용되지 않음)
    """
    with conn.cursor() as cur:
        # 현재 DB 구조
        sql = """
        INSERT INTO stage (company_name, title, job_category, content, deadline)
        VALUES (%s, %s, %s, %s, %s)
        """
        cur.execute(sql, (company_name, title, job_category, content, deadline))

        # 추후 stage 테이블에 url 컬럼 추가 시 사용할 코드
        # sql = """
        # INSERT INTO stage (company_name, title, job_category, content, deadline, url)
        # VALUES (%s, %s, %s, %s, %s, %s)
        # """
        # cur.execute(sql, (company_name, title, job_category, content, deadline, url))

    conn.commit()


# def check_url_exists(conn, url):
#     """
#     URL 중복 확인 함수 (추후 stage 테이블에 url 컬럼 추가 시 사용)
#     """
#     with conn.cursor() as cur:
#         sql = "SELECT COUNT(*) as cnt FROM stage WHERE url = %s"
#         cur.execute(sql, (url,))
#         result = cur.fetchone()
#         return result['cnt'] > 0


# =========================
# 1. Selenium 드라이버 기본 설정
# =========================

def create_driver():
    options = Options()
    options.add_argument("--start-maximized")   # 디버깅용
    # 안정화되면 헤드리스로 돌리고 싶으면 사용
    # options.add_argument("--headless=new")

    # ★ CHANGED: 네 환경에 맞게 수정
    service = Service(r"C:\chromedriver\chromedriver-win64\chromedriver.exe")
    driver = webdriver.Chrome(service=service, options=options)
    driver.implicitly_wait(7)
    return driver


# =========================
# 2. 기본 설정값
# =========================

base_url = "https://www.saramin.co.kr/zf_user/jobs/list/job-category?page="
url_list = [
    "&cat_kewd=84",    # 백엔드/서버 개발
    "&cat_kewd=80",    # 게임 개발
    "&cat_kewd=2248",  # 데이터 사이언스
    "&cat_kewd=82",    # 데이터 분석가
    "&cat_kewd=83",    # 데이터 엔지니어
    "&cat_kewd=92",    # 프론트엔드 엔지니어
    "&cat_kewd=86",    # 앱개발
    "&cat_kewd=100",   # SE(시스템 엔지니어)
    "&cat_kewd=101",   # SI 개발
    "&cat_kewd=90",    # 정보보안
]
tail_url = "&panel_type=&search_optional_item=n&search_done=y&panel_count=y&preview=y"
PAGE_SIZE = 50

MAX_PAGES_PER_CAT = 5
MAX_DB_SAVE_PER_CAT = 5


# =========================
# 3. 전체 페이지 수 계산
# =========================

def get_total_pages(driver, cat_param: str) -> int:
    url = base_url + "1" + cat_param + tail_url
    print("[페이지 수 계산] URL:", url)

    driver.get(url)
    time.sleep(0.5)

    try:
        em = WebDriverWait(driver, 5).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "span.total_count em"))
        )
        total_count = int(em.text.replace(",", ""))
        pages = math.ceil(total_count / PAGE_SIZE)

        original_pages = pages
        pages = min(pages, MAX_PAGES_PER_CAT)
        if original_pages > pages:
            print(f"[INFO] MAX_PAGES_PER_CAT={MAX_PAGES_PER_CAT} 적용: {original_pages} -> {pages}")

        print(cat_param, "총 공고:", total_count, "처리 페이지:", pages)
        return pages

    except TimeoutException:
        print("[WARN]", cat_param, "total_count 못 찾음 → 1페이지만 수행")
        items = driver.find_elements(By.CSS_SELECTOR, ".list_item")
        return 1 if len(items) > 0 else 0


# =========================
# 4. 목록 페이지에서 직무명 가져오기
# =========================

def get_jobtype_name(driver) -> str:
    try:
        el = driver.find_element(By.CSS_SELECTOR, "h1.title_common + span.value")
        return el.text.strip()
    except Exception:
        try:
            el = driver.find_element(By.CSS_SELECTOR, "span.value")
            return el.text.strip()
        except Exception:
            return ""


# =========================
# 5. 마감일 처리 (핵심)
# =========================

def normalize_deadline_text(text: str) -> str:
    """마감일 텍스트 전처리"""
    if not text:
        return ""
    t = text.strip()
    t = re.sub(r"\s+", " ", t)
    return t


def parse_deadline(text: str):
    """
    사람인 마감일 텍스트를 datetime으로 변환.

    처리 전략:
    - "채용시까지", "상시", "수시" 등 날짜가 없는 케이스: None 반환
      (DB에는 NULL 저장 → 정렬/필터링은 SQL에서 처리)
    - "YYYY.MM.DD HH:MM" 형식: 그대로 파싱
    - "YYYY.MM.DD" 만 있을 경우: 23:59로 보정
    - 기타: None
    """
    t = normalize_deadline_text(text)
    if not t:
        return None

    # 날짜 없는 유형
    no_date_keywords = ["채용시까지", "상시", "수시", "모집시", "until filled"]
    if any(k in t for k in no_date_keywords):
        return None

    # 혹시 "2026.01.15(목) 23:59" 같은 요일 포함 케이스 제거
    t = re.sub(r"\([^)]+\)", "", t).strip()
    
    # 공백 정리
    t = re.sub(r"\s+", " ", t).strip()

    # 1) datetime 형식 (시간 포함)
    datetime_formats = [
        "%Y.%m.%d %H:%M",      # 2025.12.31 23:59
        "%Y-%m-%d %H:%M",      # 2025-12-31 23:59
        "%Y/%m/%d %H:%M",      # 2025/12/31 23:59
        "%Y.%m.%d %H:%M:%S",   # 초까지 포함
    ]
    
    for fmt in datetime_formats:
        try:
            return datetime.strptime(t, fmt)
        except ValueError:
            continue

    # 2) date only → 23:59로 보정
    date_formats = [
        "%Y.%m.%d",            # 2025.12.31
        "%Y-%m-%d",            # 2025-12-31
        "%Y/%m/%d",            # 2025/12/31
    ]
    
    for fmt in date_formats:
        try:
            d = datetime.strptime(t, fmt)
            return d.replace(hour=23, minute=59, second=0, microsecond=0)
        except ValueError:
            continue

    return None


def extract_deadline_text(driver) -> str:
    """
    마감일
    """
    driver.switch_to.default_content()

    # 셀렉터 우선순위: dl.info_period dt.end + dd (현재 사람인 구조)
    selectors = [
        "dl.info_period dt.end + dd",  #  기본
        ".info_period dt.end + dd",     # dl 태그가 없는 경우 대비
        "dl.info_period dd",            # dt.end 클래스가 없는 경우 대비
    ]

    # CSS 셀렉터 시도
    for css in selectors:
        try:
            el = WebDriverWait(driver, 3).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, css))
            )
            text = el.text.strip()
            if text:
                return text
        except (TimeoutException, Exception):
            continue

    # Fallback: XPath로 dt 텍스트에 '마감' 포함되는 dd 찾기
    try:
        xpath = "//dl[contains(@class,'info_period')]//dt[contains(normalize-space(.),'마감')]/following-sibling::dd[1]"
        els = driver.find_elements(By.XPATH, xpath)
        if els:
            text = els[0].text.strip()
            if text:
                return text
    except Exception:
        pass

    # 추가 Fallback: info_period 내 모든 dd에서 '마감' 관련 텍스트 찾기
    try:
        period_els = driver.find_elements(By.CSS_SELECTOR, ".info_period, dl.info_period")
        for period in period_els:
            dts = period.find_elements(By.TAG_NAME, "dt")
            for dt in dts:
                if "마감" in dt.text:
                    dds = period.find_elements(By.TAG_NAME, "dd")
                    for dd in dds:
                        text = dd.text.strip()
                        if text and len(text) > 3:
                            return text
    except Exception:
        pass

    return ""


# =========================
# 6. 상세페이지 크롤링 (회사/제목/본문/마감일)
# =========================

CONTENT_SELECTORS = [
    ".cont_recruit",
    ".jview_content",
    ".wrap_jv_cont",
    ".section_content",
    ".user_content",
    ".view_contents",
]


def extract_company(driver) -> str:
    els = driver.find_elements(By.CSS_SELECTOR, ".title_inner a.company")
    return els[0].text.strip() if els else ""


def extract_title_and_company_from_h1(driver):
    """
    h1.tit_job에서 제목/회사명 분리 시도
    """
    company_name = ""
    title_text = ""

    try:
        h1 = driver.find_element(By.CSS_SELECTOR, "h1.tit_job")
        full_title = h1.text.strip()

        if full_title.startswith("[") and "]" in full_title:
            end_idx = full_title.find("]")
            company_name = full_title[1:end_idx].strip()
            title_text = full_title[end_idx + 1:].strip()
        else:
            title_text = full_title
    except Exception:
        pass

    return company_name, title_text


def extract_text_from_current_context(driver) -> str:
    for s in CONTENT_SELECTORS:
        els = driver.find_elements(By.CSS_SELECTOR, s)
        if els:
            return els[0].text.strip()
    return ""


def extract_content(driver) -> str:
    """
    본문은 iframe 안에 있는 경우가 많아서:
    1) 메인에서 한 번 시도
    2) iframe 있으면 들어가서 다시 시도
    """
    driver.switch_to.default_content()

    # 1) 메인에서 시도 (iframe 없는 공고 대비)
    content = extract_text_from_current_context(driver)
    if content:
        return content

    # 2) iframe 들어가서 시도
    try:
        iframes = driver.find_elements(By.TAG_NAME, "iframe")
        if iframes:
            driver.switch_to.frame(iframes[0])
            content = extract_text_from_current_context(driver)
    except Exception:
        content = ""

    finally:
        try:
            driver.switch_to.default_content()
        except Exception:
            pass

    return content.strip()


def crawl_and_save_job(driver, conn, url, job_category_name) -> bool:
    try:
        driver.get(url)
        time.sleep(0.3)

        # 마감일
        deadline_text = extract_deadline_text(driver)

        # 제목/회사명
        driver.switch_to.default_content()
        company_name = extract_company(driver)

        h1_company, title_text = extract_title_and_company_from_h1(driver)
        if h1_company:
            company_name = h1_company

        # 본문
        content = extract_content(driver)

        if not content:
            print("[SKIP] 본문 없음 → skip:", url)
            return False

        deadline_dt = parse_deadline(deadline_text)

        # 디버깅 로그
        print(f"[DEBUG] deadline_text='{deadline_text}' → deadline_dt={deadline_dt}")

        # url 중복 체크
        # if check_url_exists(conn, url):
        #     print("[SKIP] 이미 존재하는 URL →", url)
        #     return False

        try:
            insert_stage(
                conn,
                company_name=company_name or "",
                title=title_text or "",
                job_category=job_category_name or "",
                content=content,
                deadline=deadline_dt,
                url=url,  # 추후 url 추가한다면
            )
            print("[DB] INSERT OK:", company_name, "/", title_text, "/", job_category_name)
            return True
        except Exception as e:
            print("[DB] INSERT ERROR:", e)
            return False

    except Exception as e:
        print("[ERROR] 크롤링 중 오류:", e, "URL:", url)
        return False


# =========================
# 7. 메인 크롤링 로직
# =========================

def crawl_all():
    driver = create_driver()
    conn = get_connection()

    total_saved = 0

    try:
        for cat_idx, cat_param in enumerate(url_list):
            print(f"\n{'='*60}")
            print(f"[카테고리 {cat_idx + 1}/{len(url_list)}] 시작 {cat_param}")
            print(f"{'='*60}")

            # 1) 첫 페이지에서 직무명
            first_url = base_url + "1" + cat_param + tail_url
            driver.get(first_url)
            time.sleep(0.5)

            job_category_name = get_jobtype_name(driver)
            print(f"[INFO] 직무명(목록페이지): {job_category_name}")

            # 2) 페이지 수
            max_pages = get_total_pages(driver, cat_param)
            if max_pages == 0:
                print("[INFO] 처리할 페이지 없음 → 다음 카테고리")
                continue

            # 3) 페이지 순회
            db_save_count = 0

            for page_num in range(1, max_pages + 1):
                if db_save_count >= MAX_DB_SAVE_PER_CAT:
                    print(f"[INFO] {MAX_DB_SAVE_PER_CAT}개 저장 완료 → 다음 카테고리")
                    break

                page_url = base_url + str(page_num) + cat_param + tail_url
                print(f"\n[LIST] GET: {page_url}")
                driver.get(page_url)
                time.sleep(0.2)

                job_items = driver.find_elements(By.CSS_SELECTOR, ".list_item")
                print(f"  - page {page_num}, found {len(job_items)}개")

                for job in job_items:
                    if db_save_count >= MAX_DB_SAVE_PER_CAT:
                        break

                    try:
                        link_el = job.find_element(By.CSS_SELECTOR, ".job_tit a")
                        href = link_el.get_attribute("href")
                        if not href:
                            continue
                    except Exception:
                        continue

                    if crawl_and_save_job(driver, conn, href, job_category_name):
                        db_save_count += 1
                        total_saved += 1

                    time.sleep(0.15)

            print(f"[INFO] 카테고리 '{job_category_name}' 완료: {db_save_count}개 저장")

    finally:
        try:
            driver.quit()
        except Exception:
            pass
        try:
            conn.close()
        except Exception:
            pass

    print(f"\n{'='*60}")
    print(f"[INFO] 전체 작업 완료! 총 {total_saved}개 저장됨")
    print(f"{'='*60}")


# =========================
# 8. MAIN
# =========================

if __name__ == "__main__":
    crawl_all()
    print("[INFO] 작업 끝!")
