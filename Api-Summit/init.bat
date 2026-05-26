@echo off
echo ==========================
echo Restarting Docker Ambient
echo ==========================

docker-compose down -v

echo.
echo ==========================
echo Loading Containers
echo ==========================

docker-compose up --build