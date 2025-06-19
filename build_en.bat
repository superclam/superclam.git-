@echo off
chcp 65001 >nul
echo ========================================
echo ScreenHelper Build Script
echo ========================================
echo.

echo Setting up Java environment...
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

echo Current Java version:
"%JAVA_HOME%\bin\java" -version

echo.
echo Setting up Gradle environment...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx2048m

echo.
echo Starting build...
echo ========================================

echo Trying to build with Gradle Wrapper...
if exist gradlew.bat (
    echo Using project Gradle Wrapper...
    call gradlew.bat clean
    if %errorlevel% neq 0 goto :error
    
    call gradlew.bat assembleDebug --no-daemon
    if %errorlevel% neq 0 goto :error
    
    call gradlew.bat assembleRelease --no-daemon
    if %errorlevel% neq 0 goto :error
    
    goto :success
) else (
    echo Error: gradlew.bat not found
    goto :error
)

:success
echo.
echo ========================================
echo Build successful!
echo ========================================

echo.
echo Checking APK files...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo [OK] Found Debug APK
    copy "app\build\outputs\apk\debug\app-debug.apk" "ScreenHelper-debug.apk" >nul
    echo [OK] Copied to: ScreenHelper-debug.apk
) else (
    echo [FAIL] Debug APK not found
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo [OK] Found Release APK
    copy "app\build\outputs\apk\release\app-release-unsigned.apk" "ScreenHelper-release.apk" >nul
    echo [OK] Copied to: ScreenHelper-release.apk
) else (
    echo [FAIL] Release APK not found
)

echo.
echo ========================================
echo APK files are ready!
echo You can transfer APK files to your phone
echo ========================================
echo.
echo Transfer methods:
echo 1. USB cable - copy APK to phone storage
echo 2. QQ/WeChat - send APK file to phone
echo 3. Cloud storage - upload and download on phone
echo 4. Local network file transfer
echo.
pause
exit /b 0

:error
echo.
echo ========================================
echo Build failed!
echo ========================================
echo.
echo Possible solutions:
echo 1. Check network connection
echo 2. Check Java environment
echo 3. Try manual Gradle download
echo 4. Contact developer for pre-built APK
echo.
pause
exit /b 1
