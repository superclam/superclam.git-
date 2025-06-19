@echo off
echo ========================================
echo ScreenHelper 本地构建脚本
echo ========================================
echo.

echo 设置Java环境...
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

echo 检查Java环境...
"%JAVA_HOME%\bin\java" -version
if %errorlevel% neq 0 (
    echo 错误: Java环境设置失败
    echo 请确保Java已正确安装在 D:\Github\Java
    pause
    exit /b 1
)

echo.
echo 检查Android SDK...
if not exist "%ANDROID_HOME%" (
    echo 警告: 未设置ANDROID_HOME环境变量
    echo 请确保已安装Android SDK并设置环境变量
)

echo.
echo 开始构建...
echo ========================================

echo 清理项目...
call gradlew clean
if %errorlevel% neq 0 (
    echo 错误: 清理项目失败
    pause
    exit /b 1
)

echo.
echo 构建Debug版本...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo 错误: 构建Debug版本失败
    pause
    exit /b 1
)

echo.
echo 构建Release版本...
call gradlew assembleRelease
if %errorlevel% neq 0 (
    echo 错误: 构建Release版本失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo 构建完成！
echo ========================================

echo.
echo APK文件位置:
echo Debug版本: app\build\outputs\apk\debug\app-debug.apk
echo Release版本: app\build\outputs\apk\release\app-release-unsigned.apk

echo.
echo 复制APK到根目录...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    copy "app\build\outputs\apk\debug\app-debug.apk" "ScreenHelper-debug.apk"
    echo ✓ Debug APK已复制到: ScreenHelper-debug.apk
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    copy "app\build\outputs\apk\release\app-release-unsigned.apk" "ScreenHelper-release.apk"
    echo ✓ Release APK已复制到: ScreenHelper-release.apk
)

echo.
echo ========================================
echo 构建成功完成！
echo 您可以在项目根目录找到APK文件
echo ========================================
echo.

pause
