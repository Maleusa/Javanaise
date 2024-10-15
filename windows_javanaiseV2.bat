@echo off
echo Compiling files...

javac -cp DONNE2/SOURCES/src/ DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java DONNE2/SOURCES/src/tests/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

echo Compilation successful
echo Launching JVN2 clients (x%1) with the Coordinator

cd DONNE2/SOURCES/src
start java jvn.JvnCoordImpl
timeout /t 4 >nul

for /L %%i in (1,1,%1) do (
    start java tests.ClientReadLockATest
    timeout /t 1 >nul
    start java tests.ClientWriteLockATest
    timeout /t 1 >nul
    start java tests.ClientReadWriteLockATest
    timeout /t 1 >nul
)
