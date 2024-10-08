@echo off
echo Compiling files...

javac DONNE2/SOURCES/src/jvn/*.java
javac -cp DONNE2/SOURCES/src/jvn/*.java DONNE2/SOURCES/src/irc/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

echo Compilation successful
echo Launching %1 JVN1 clients with the Coordinator

cd DONNE2/SOURCES/src
start java jvn.JvnCoordImpl
timeout /t 4 >nul

for /L %%i in (1,1,%1) do (
    start java irc.Irc
    timeout /t 1 >nul
)
