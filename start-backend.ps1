Write-Host "=== Starting Backend Service ===" -ForegroundColor Green

# Set environment variables
$env:JAVA_HOME = "C:\Users\maike\.jdks\corretto-17.0.14"
$env:PATH = "C:\Users\maike\.jdks\corretto-17.0.14\bin;C:\Program Files\apache-maven-3.9.6\bin;" + $env:PATH

Write-Host "Java version:" -ForegroundColor Yellow
java -version

Write-Host "`nStarting Spring Boot application..." -ForegroundColor Yellow
Write-Host "Backend will be available at: http://localhost:8080" -ForegroundColor Cyan

# Change to backend directory and start
cd "C:\Users\maike\Desktop\jiji_memorization\jiji_memorization_backend"
& "C:\Program Files\apache-maven-3.9.6\bin\mvn.cmd" -s settings.xml spring-boot:run

Write-Host "`nBackend service stopped." -ForegroundColor Red
