# Windows 작업 스케줄러 등록 스크립트
# 이 스크립트를 관리자 권한으로 실행하면 작업 스케줄러에 자동으로 등록됩니다.
# 
# 주의: 이 스크립트는 현재 스크립트 파일이 있는 폴더를 기준으로 경로를 자동으로 설정합니다.
# 폴더를 옮겨도 이 스크립트를 실행하면 자동으로 올바른 경로로 등록됩니다.

# 현재 스크립트가 있는 디렉토리 경로 (폴더를 옮겨도 자동으로 올바른 경로로 설정됨)
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$TaskName = "PASSPROJECT5_FINAL_5"
# 스크립트 디렉토리 내의 saramin_crolling.py 파일 경로 (상대 경로 사용)
$PythonScript = Join-Path $ScriptDir "saramin_crolling.py"

# Python 경로 찾기 (환경 변수에서)
$PythonPath = (Get-Command python -ErrorAction SilentlyContinue).Source
if (-not $PythonPath) {
    $PythonPath = (Get-Command python3 -ErrorAction SilentlyContinue).Source
}
if (-not $PythonPath) {
    Write-Host "[ERROR] Python을 찾을 수 없습니다. PATH에 Python이 등록되어 있는지 확인하세요." -ForegroundColor Red
    exit 1
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "작업 스케줄러 등록 스크립트" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "작업 이름: $TaskName" -ForegroundColor Cyan
Write-Host "Python 경로: $PythonPath" -ForegroundColor Cyan
Write-Host "스크립트 경로: $PythonScript" -ForegroundColor Cyan
Write-Host "실행 시간: 매일 새벽 3시" -ForegroundColor Cyan
Write-Host ""

# 기존 작업이 있으면 삭제
$ExistingTask = Get-ScheduledTask -TaskName $TaskName -ErrorAction SilentlyContinue
if ($ExistingTask) {
    Write-Host "[INFO] 기존 작업을 삭제합니다..." -ForegroundColor Yellow
    Unregister-ScheduledTask -TaskName $TaskName -Confirm:$false
    Write-Host "[INFO] 기존 작업이 삭제되었습니다." -ForegroundColor Yellow
}

# 작업 실행 액션 정의
$Action = New-ScheduledTaskAction -Execute $PythonPath -Argument "`"$PythonScript`"" -WorkingDirectory $ScriptDir

# 트리거 정의 (매일 새벽 3시)
$Trigger = New-ScheduledTaskTrigger -Daily -At "03:00"

# 설정 정의
$Settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable -RunOnlyIfNetworkAvailable:$false

# 작업 설명
$Description = "매일 새벽 3시에 사람인 크롤링 작업을 실행합니다. (PASSPROJECT5_FINAL_5)"

try {
    # 작업 등록 (최고 수준 권한으로 실행)
    Register-ScheduledTask -TaskName $TaskName -Action $Action -Trigger $Trigger -Settings $Settings -Description $Description -RunLevel Highest -Force
    
    Write-Host "[SUCCESS] 작업 스케줄러 등록이 완료되었습니다!" -ForegroundColor Green
    Write-Host ""
    Write-Host "등록된 작업 정보:" -ForegroundColor Cyan
    Write-Host "  - 작업 이름: $TaskName" -ForegroundColor White
    Write-Host "  - 실행 시간: 매일 03:00" -ForegroundColor White
    Write-Host "  - 스크립트: $PythonScript" -ForegroundColor White
    Write-Host ""
    Write-Host "작업 스케줄러를 확인하려면 다음 명령어를 실행하세요:" -ForegroundColor Yellow
    Write-Host "  taskschd.msc" -ForegroundColor White
    Write-Host ""
    Write-Host "작업을 삭제하려면 다음 명령어를 실행하세요:" -ForegroundColor Yellow
    Write-Host "  Unregister-ScheduledTask -TaskName `"$TaskName`" -Confirm:`$false" -ForegroundColor White
    
} catch {
    Write-Host "[ERROR] 작업 스케줄러 등록 중 오류가 발생했습니다:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "관리자 권한으로 실행했는지 확인하세요!" -ForegroundColor Yellow
    exit 1
}

