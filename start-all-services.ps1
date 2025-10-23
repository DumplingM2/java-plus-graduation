# Скрипт для запуска всех сервисов в правильном порядке

Write-Host "Запуск всех сервисов..." -ForegroundColor Green

# 1. Запуск discovery-server
Write-Host "1. Запуск discovery-server..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "infra/discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 10

# 2. Запуск config-server
Write-Host "2. Запуск config-server..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "infra/config-server/target/config-server-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 10

# 3. Запуск gateway-server
Write-Host "3. Запуск gateway-server..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "infra/gateway-server/target/gateway-server-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 4. Запуск main-service
Write-Host "4. Запуск main-service..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "core/main-service/target/main-service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 5. Запуск user-service
Write-Host "5. Запуск user-service..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "core/user-service/target/user-service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 6. Запуск event-service
Write-Host "6. Запуск event-service..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "core/event-service/target/event-service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 7. Запуск request-service
Write-Host "7. Запуск request-service..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "core/request-service/target/request-service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 8. Запуск comment-service
Write-Host "8. Запуск comment-service..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "core/comment-service/target/comment-service-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 5

# 9. Запуск stats-server
Write-Host "9. Запуск stats-server..." -ForegroundColor Yellow
Start-Process -FilePath "java" -ArgumentList "-jar", "stats-service/server/target/stats-server-0.0.1-SNAPSHOT.jar" -WindowStyle Minimized

Write-Host "Все сервисы запущены!" -ForegroundColor Green
Write-Host "Проверьте логи в окнах приложений для диагностики проблем." -ForegroundColor Cyan
