@echo off
rem run.bat - run the PIMS app on Windows (looks for exe, jar, or compiles from src using lib\)
setlocal

set "EXE=dist\pims.exe"
set "JAR=target\pims-1.0.jar"
set "LIB_DIR=lib"
set "SRC_DIR=src"
set "OUT_DIR=out\classes"
set "MAIN_CLASS=LoginPage"

rem Prefer an included exe
if exist "%EXE%" (
  echo Launching executable: %EXE%
  start "" "%EXE%"
  endlocal
  exit /b 0
)

rem Prefer a provided jar
if exist "%JAR%" (
  echo Found jar: %JAR% - running...
  java -jar "%JAR%"
  endlocal
  exit /b %ERRORLEVEL%
)

rem Fall back to compiling sources using jars in lib\
if not exist "%LIB_DIR%" (
  echo ERROR: "%LIB_DIR%" not found. Create a lib\ folder and put mysql-connector-java.jar and flatlaf.jar inside, or build the jar with Maven.
  pause
  endlocal
  exit /b 1
)

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

rem set console to utf-8 for nicer output (optional)
chcp 65001 >nul

echo Compiling .java files from %SRC_DIR% using jars in %LIB_DIR% (UTF-8) ...
javac -encoding UTF-8 -cp "%LIB_DIR%\*;." %SRC_DIR%\*.java -d %OUT_DIR%
if errorlevel 1 (
  echo Compilation failed.
  pause
  endlocal
  exit /b 1
)

echo Running %MAIN_CLASS% ...
java -cp "%OUT_DIR%;%LIB_DIR%\*;." %MAIN_CLASS%

endlocal