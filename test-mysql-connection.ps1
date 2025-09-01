Write-Host "=== MySQL Connection Test ===" -ForegroundColor Green

# Test MySQL connection using PowerShell
$connectionString = "Server=localhost;Port=3306;Database=jiji_memorization;Uid=root;Pwd=root;"

try {
    # Try to connect using .NET MySQL connector
    Add-Type -Path "C:\Program Files (x86)\MySQL\MySQL Connector NET 8.0.33\Assemblies\v4.5.2\MySql.Data.dll" -ErrorAction SilentlyContinue
    
    $connection = New-Object MySql.Data.MySqlClient.MySqlConnection($connectionString)
    $connection.Open()
    
    Write-Host "✅ MySQL connection successful!" -ForegroundColor Green
    Write-Host "   Database: $($connection.Database)" -ForegroundColor Gray
    Write-Host "   Server Version: $($connection.ServerVersion)" -ForegroundColor Gray
    
    $connection.Close()
    
} catch {
    Write-Host "❌ MySQL connection failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "`nTroubleshooting tips:" -ForegroundColor Yellow
    Write-Host "1. Make sure MySQL service is running" -ForegroundColor Gray
    Write-Host "2. Verify username and password are correct" -ForegroundColor Gray
    Write-Host "3. Check if database 'jiji_memorization' exists" -ForegroundColor Gray
    Write-Host "4. Ensure MySQL is listening on port 3306" -ForegroundColor Gray
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
