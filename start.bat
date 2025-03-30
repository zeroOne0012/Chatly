@echo off

REM 기본값 dev
set PROFILE=%1
if "%PROFILE%"=="" (
  set PROFILE=dev
)
gradlew clean build -x test && java -jar ./build/libs/Chatly-0.0.1-SNAPSHOT.jar --spring.profiles.active=%PROFILE%