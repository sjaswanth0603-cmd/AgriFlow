@echo off
title AgriFlow - Smart Farm Management & Optimization (Modules 1 to 3)
echo ==================================================================
echo    🌾 AgriFlow - Smart Farm Management & Optimization Platform 🌾
echo                 Modules 1 to 3 (CO1, CO2, CO3) Core
echo ==================================================================
echo.

if not exist "bin" mkdir bin

echo [1/3] Compiling Java codebase (Modules M1 to M3 + Embedded Server)...
javac -encoding UTF-8 -d bin src\main\java\com\agriflow\m1_strings\*.java src\main\java\com\agriflow\m2_suffix\*.java src\main\java\com\agriflow\m3_dp\*.java src\main\java\com\agriflow\server\*.java src\main\java\com\agriflow\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please check Java JDK 21+ installation.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Synchronizing Web Dashboard assets...
if not exist "bin\web" mkdir bin\web
xcopy /E /I /Y src\main\resources\web bin\web > nul

echo [3/3] Compilation successful!
echo.
echo Launching AgriFlow Dual-Mode Application (Embedded Server + Interactive CLI)...
echo.

java -cp bin com.agriflow.Main
pause
