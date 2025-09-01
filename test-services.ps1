Write-Host "=== Jiji Memorization Service Status Check ===" -ForegroundColor Green

# Test backend service
Write-Host "`n1. Checking Backend Service (http://localhost:8080)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/hello" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ Backend service is running normally" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Backend service is not started or connection failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# Test frontend service
Write-Host "`n2. Checking Frontend Service (http://localhost:3000)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ Frontend service is running normally" -ForegroundColor Green
    Write-Host "   Status Code: $($response.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Frontend service is not started or connection failed" -ForegroundColor Red
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Gray
}

Write-Host "`n=== Check Complete ===" -ForegroundColor Green
