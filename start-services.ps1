Write-Host "=== Jiji Memorization Service Launcher ===" -ForegroundColor Green

# Set environment variables
$env:JAVA_HOME = "C:\Users\maike\.jdks\corretto-17.0.14"
$env:PATH = "C:\Users\maike\.jdks\corretto-17.0.14\bin;C:\Program Files\apache-maven-3.9.6\bin;" + $env:PATH

Write-Host "`n1. Starting Backend Service..." -ForegroundColor Yellow
Write-Host "   Backend will start at http://localhost:8080" -ForegroundColor Gray

# Start backend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'C:\Users\maike\Desktop\jiji_memorization\jiji_memorization_backend'; & 'C:\Program Files\apache-maven-3.9.6\bin\mvn.cmd' -s settings.xml spring-boot:run" -WindowStyle Normal

Write-Host "`n2. Starting Frontend Service..." -ForegroundColor Yellow
Write-Host "   Frontend will start at http://localhost:3000" -ForegroundColor Gray

# Start frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'C:\Users\maike\Desktop\jiji_memorization\jiji_memorization_frontend'; npm run dev" -WindowStyle Normal

Write-Host "`n✅ Service launch commands executed!" -ForegroundColor Green
Write-Host "   Please wait a few seconds for services to fully start..." -ForegroundColor Gray
Write-Host "   Then visit:" -ForegroundColor Cyan
Write-Host "   - Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "   - Backend: http://localhost:8080/api/hello" -ForegroundColor Cyan

Write-Host "`nPress any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
