Write-Host "=== Starting Frontend Service ===" -ForegroundColor Green

Write-Host "Starting Vue.js development server..." -ForegroundColor Yellow
Write-Host "Frontend will be available at: http://localhost:3000" -ForegroundColor Cyan

# Change to frontend directory and start
cd "C:\Users\maike\Desktop\jiji_memorization\jiji_memorization_frontend"

# Check if node_modules exists, if not install dependencies
if (-not (Test-Path "node_modules")) {
    Write-Host "Installing dependencies..." -ForegroundColor Yellow
    npm install
}

Write-Host "`nStarting development server..." -ForegroundColor Yellow
npm run dev

Write-Host "`nFrontend service stopped." -ForegroundColor Red
