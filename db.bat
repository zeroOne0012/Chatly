@echo off

REM 기본값 dev
set PROFILE=%1
if "%PROFILE%"=="" (
  set PROFILE=dev
)

if "%PROFILE%"=="dev" (
    docker-compose down && docker-compose -f docker-compose-dev.yml up -d
)
if "%PROFILE%"=="prod" (
    docker-compose -f docker-compose-dev.yml down && docker-compose up -d
)