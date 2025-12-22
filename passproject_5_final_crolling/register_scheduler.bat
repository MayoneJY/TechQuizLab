@echo off
chcp 65001 >nul
echo ========================================
echo 작업 스케줄러 등록 스크립트
echo ========================================
echo.
echo 관리자 권한으로 실행 중...
echo.

REM 관리자 권한 확인
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [ERROR] 이 스크립트는 관리자 권한이 필요합니다.
    echo 마우스 우클릭 -^> 관리자 권한으로 실행을 선택하세요.
    pause
    exit /b 1
)

REM PowerShell 스크립트 실행
powershell.exe -ExecutionPolicy Bypass -File "%~dp0register_scheduler.ps1"

echo.
pause

