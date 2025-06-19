@echo off
echo ========================================
echo ScreenHelper 简化构建脚本
echo ========================================
echo.

echo 设置Java环境...
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

echo 当前Java版本:
"%JAVA_HOME%\bin\java" -version

echo.
echo 设置Gradle环境变量...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx2048m
set GRADLE_USER_HOME=%USERPROFILE%\.gradle

echo.
echo 开始构建...
echo ========================================

echo 尝试使用本地Gradle构建...
if exist gradlew.bat (
    echo 使用项目自带的Gradle Wrapper...
    call gradlew.bat clean assembleDebug assembleRelease --offline --no-daemon
    if %errorlevel% equ 0 goto :success
    
    echo.
    echo 离线构建失败，尝试在线构建...
    call gradlew.bat clean assembleDebug assembleRelease --no-daemon
    if %errorlevel% equ 0 goto :success
) else (
    echo 错误: 未找到gradlew.bat文件
    goto :error
)

echo.
echo 构建失败，尝试手动下载Gradle...
echo 请手动下载Gradle 7.6并解压到项目目录
goto :error

:success
echo.
echo ========================================
echo 构建成功！
echo ========================================

echo.
echo 检查APK文件...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✓ 找到Debug APK
    copy "app\build\outputs\apk\debug\app-debug.apk" "ScreenHelper-debug.apk"
    echo ✓ 已复制到: ScreenHelper-debug.apk
) else (
    echo ✗ 未找到Debug APK
)

if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
    echo ✓ 找到Release APK
    copy "app\build\outputs\apk\release\app-release-unsigned.apk" "ScreenHelper-release.apk"
    echo ✓ 已复制到: ScreenHelper-release.apk
) else (
    echo ✗ 未找到Release APK
)

echo.
echo ========================================
echo APK文件已准备就绪！
echo 您可以直接将APK文件传输到手机安装
echo ========================================
echo.
echo 传输方法:
echo 1. 使用USB数据线连接手机，复制APK到手机存储
echo 2. 使用QQ、微信等发送APK文件到手机
echo 3. 使用云盘（如百度网盘）上传后在手机下载
echo 4. 使用局域网文件传输工具
echo.
pause
exit /b 0

:error
echo.
echo ========================================
echo 构建失败！
echo ========================================
echo.
echo 可能的解决方案:
echo 1. 检查网络连接
echo 2. 检查Java环境是否正确
echo 3. 尝试手动下载Gradle
echo 4. 联系开发者获取预编译APK
echo.
pause
exit /b 1
