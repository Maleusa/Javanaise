@echo off
echo Compiling files...

javac -cp DONNE2/SOURCES/src/ DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java DONNE2/SOURCES/src/tests/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

echo Compilation successful
echo Launching cache server tests for JVN1 (x%1)

cd DONNE2/SOURCES/src
start java jvn.JvnCoordImpl
timeout /t 4 >nul

for /L %%i in (1,1,%1) do (
    start java tests.ClientLimitedCacheTest
    timeout /t 1 >nul
)
