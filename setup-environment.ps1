Write-Host "=== Jiji Memorization Environment Setup ===" -ForegroundColor Green

# Set system environment variables
$javaHome = "C:\Users\maike\.jdks\corretto-17.0.14"
$mavenHome = "C:\Program Files\apache-maven-3.9.6"

Write-Host "`nConfiguring Java environment variables..." -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "User")

Write-Host "Configuring Maven environment variables..." -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, "User")

Write-Host "Updating PATH environment variable..." -ForegroundColor Yellow
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
$newPath = "$javaHome\bin;$mavenHome\bin;$currentPath"
[Environment]::SetEnvironmentVariable("PATH", $newPath, "User")

Write-Host "`n✅ Environment variables configured successfully!" -ForegroundColor Green
Write-Host "   Please restart PowerShell window to apply changes." -ForegroundColor Gray

Write-Host "`nVerification:" -ForegroundColor Cyan
Write-Host "JAVA_HOME: $([Environment]::GetEnvironmentVariable('JAVA_HOME', 'User'))" -ForegroundColor Gray
Write-Host "MAVEN_HOME: $([Environment]::GetEnvironmentVariable('MAVEN_HOME', 'User'))" -ForegroundColor Gray

Write-Host "`nPress any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
