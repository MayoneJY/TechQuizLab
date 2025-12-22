# -*- coding: utf-8 -*-
"""
매일 새벽 3시에 saramin_crolling.py의 크롤링 작업을 실행하는 스케줄러
"""
import schedule
import time
import subprocess
import sys
import os
from datetime import datetime

# Windows에서 UTF-8 인코딩 설정
if sys.platform == 'win32':
    os.environ['PYTHONIOENCODING'] = 'utf-8'

def run_crawler():
    """
    크롤링 실행 함수
    """
    print(f"\n{'='*60}")
    print(f"[스케줄러] 작업 시작 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"{'='*60}\n")
    
    try:
        # 스크립트 파일이 있는 디렉토리 경로 가져오기
        script_dir = os.path.dirname(os.path.abspath(__file__))
        
        # saramin_crolling.py 파일 실행
        # Windows에서 인코딩 문제 해결을 위해 환경 변수 설정
        env = os.environ.copy()
        env['PYTHONIOENCODING'] = 'utf-8'
        
        result = subprocess.run(
            [sys.executable, "saramin_crolling.py"],
            cwd=script_dir,  # 스크립트 파일이 있는 디렉토리에서 실행
            capture_output=False,
            text=True,
            encoding='utf-8',
            errors='replace',  # 인코딩 오류 시 대체 문자 사용
            env=env
        )
        
        if result.returncode == 0:
            print(f"\n[스케줄러] 작업 완료: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        else:
            print(f"\n[스케줄러] 작업 실패 (종료 코드: {result.returncode}): {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            
    except Exception as e:
        print(f"\n[스케줄러] 오류 발생: {e}")
        print(f"[스케줄러] 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")


def main():
    """
    메인 함수: 스케줄 등록 및 실행
    """
    # 원래 설정: 매일 새벽 3시에 실행하도록 스케줄 등록
    schedule.every().day.at("03:00").do(run_crawler)
    
    print("[스케줄러] PASSPROJECT5_FINAL_5 스케줄러가 시작되었습니다.")
    print("[스케줄러] 매일 새벽 3시에 크롤링 작업이 실행됩니다.")
    print(f"[스케줄러] 현재 시간: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("[스케줄러] 종료하려면 Ctrl+C를 누르세요.\n")
    
    # 스케줄러 루프 실행
    while True:
        schedule.run_pending()
        time.sleep(60)  # 1분마다 스케줄 확인


if __name__ == "__main__":
    try:
         main()
    except KeyboardInterrupt:
        print("\n[스케줄러] 스케줄러가 종료되었습니다.")
        sys.exit(0)
