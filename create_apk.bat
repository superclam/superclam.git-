@echo off
chcp 65001 >nul
echo ========================================
echo ScreenHelper APK Creator
echo ========================================
echo.

echo Due to SSL certificate issues with Gradle download,
echo we'll create a simple APK using alternative methods.
echo.

echo Method 1: Download pre-built Gradle
echo ========================================
echo.
echo Please follow these steps:
echo.
echo 1. Download Gradle 7.6 manually:
echo    https://gradle.org/releases/
echo    Download: gradle-7.6-bin.zip
echo.
echo 2. Extract to: %USERPROFILE%\.gradle\wrapper\dists\gradle-7.6-bin\
echo.
echo 3. Run this script again
echo.

set /p choice="Do you want to try downloading Gradle manually? (y/n): "
if /i "%choice%"=="y" goto :download_gradle
if /i "%choice%"=="n" goto :alternative

:download_gradle
echo.
echo Opening Gradle download page...
start https://gradle.org/releases/
echo.
echo After downloading and extracting Gradle:
echo 1. Extract gradle-7.6-bin.zip
echo 2. Copy the gradle-7.6 folder to: %USERPROFILE%\.gradle\wrapper\dists\gradle-7.6-bin\
echo 3. Run build_en.bat again
echo.
pause
exit /b 0

:alternative
echo.
echo Alternative Method: Use Android Studio
echo ========================================
echo.
echo If you have Android Studio installed:
echo 1. Open this project in Android Studio
echo 2. Wait for Gradle sync to complete
echo 3. Go to Build > Build Bundle(s) / APK(s) > Build APK(s)
echo 4. APK will be generated in app/build/outputs/apk/
echo.
echo Or use online build services:
echo 1. GitHub Codespaces (free)
echo 2. GitPod (free tier available)
echo 3. Replit (online IDE)
echo.

set /p choice2="Do you want to open Android Studio download page? (y/n): "
if /i "%choice2%"=="y" (
    start https://developer.android.com/studio
)

echo.
echo ========================================
echo Quick Transfer Guide
echo ========================================
echo.
echo Once you have the APK file:
echo.
echo Method 1 - USB Cable:
echo 1. Connect phone to computer via USB
echo 2. Enable File Transfer mode on phone
echo 3. Copy APK to phone's Download folder
echo 4. Open file manager on phone and install APK
echo.
echo Method 2 - QQ/WeChat:
echo 1. Send APK file to yourself via QQ/WeChat
echo 2. Download on phone and install
echo.
echo Method 3 - Cloud Storage:
echo 1. Upload APK to Baidu Netdisk or other cloud service
echo 2. Download on phone and install
echo.
echo Method 4 - Email:
echo 1. Email APK to yourself
echo 2. Download attachment on phone and install
echo.

pause
exit /b 0
