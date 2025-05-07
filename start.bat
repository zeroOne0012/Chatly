@echo off

@REM .\start.bat dev false

REM 기본값 dev
set PROFILE=%1
if "%PROFILE%"=="" (
  set PROFILE=dev
)
set AOP_LOG=""
@REM set "AOP_LOG=--log.aop=%2"
REM 두 번째 인자가 1 또는 true일 경우
IF "%~2"=="1" (
    set "AOP_LOG=--log.aop=true"
) ELSE IF /I "%~2"=="true" (
    set "AOP_LOG=--log.aop=true"
)
gradlew clean build -x test && java -jar ./build/libs/Chatly-0.0.1-SNAPSHOT.jar --spring.profiles.active=%PROFILE% %AOP_LOG%