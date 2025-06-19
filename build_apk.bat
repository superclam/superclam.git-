@echo off
echo ========================================
echo ScreenHelper APK 构建脚本
echo ========================================
echo.

REM 设置Java环境
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

echo 检查Java版本...
java -version
echo.

echo 检查Gradle版本...
gradlew.bat --version
echo.

echo 清理项目...
gradlew.bat clean
echo.

echo 构建Debug APK...
gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo Debug构建失败！
    pause
    exit /b 1
)
echo.

echo 构建Release APK...
gradlew.bat assembleRelease
if %ERRORLEVEL% NEQ 0 (
    echo Release构建失败！
    pause
    exit /b 1
)
echo.

echo ========================================
echo 构建完成！
echo ========================================
echo.
echo APK文件位置：
echo Debug版本: app\build\outputs\apk\debug\app-debug.apk
echo Release版本: app\build\outputs\apk\release\app-release-unsigned.apk
echo.

REM 检查文件是否存在
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✓ Debug APK 构建成功
) else (
    echo ✗ Debug APK 构建失败
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo ✓ Release APK 构建成功
) else (
    echo ✗ Release APK 构建失败
)

echo.
echo 按任意键退出...
pause > nul
