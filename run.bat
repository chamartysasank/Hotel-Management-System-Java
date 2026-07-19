@echo off

REM Switch Windows console to UTF-8
chcp 65001 > nul

cd /d "%~dp0"

echo Compiling...

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\model\*.java src\utility\*.java src\repository\*.java src\service\*.java src\main\*.java

if errorlevel 1 (
    pause
    exit
)

echo Launching Hotel Room Booking Console App...

java -Dfile.encoding=UTF-8 -cp bin main.Main

pause