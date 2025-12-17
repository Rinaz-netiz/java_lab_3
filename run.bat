@echo off
echo Autonomous Taxi System

set OUT_DIR=out

if exist %OUT_DIR% rmdir /s /q %OUT_DIR%
mkdir %OUT_DIR%

echo Compiling...
dir /s /b src\*.java > sources.txt
javac -d %OUT_DIR% @sources.txt

if %ERRORLEVEL% == 0 (
    echo Starting...
    echo.
    java -cp %OUT_DIR% com.taxi.Main
) else (
    echo Compilation failed
    exit /b 1
)

del sources.txt