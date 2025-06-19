@echo off
echo ========================================
echo ScreenHelper APK 签名脚本
echo ========================================
echo.

REM 设置Java环境
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

REM APK文件路径
set UNSIGNED_APK=app\build\outputs\apk\release\app-release-unsigned.apk
set SIGNED_APK=app\build\outputs\apk\release\ScreenHelper-signed.apk
set KEYSTORE=screenhelper.keystore

echo 检查未签名APK是否存在...
if not exist "%UNSIGNED_APK%" (
    echo 错误：未找到未签名的APK文件！
    echo 请先运行 build_apk.bat 构建APK
    pause
    exit /b 1
)

echo 检查密钥库是否存在...
if not exist "%KEYSTORE%" (
    echo 创建新的密钥库...
    keytool -genkey -v -keystore %KEYSTORE% -alias screenhelper -keyalg RSA -keysize 2048 -validity 10000 -storepass 123456 -keypass 123456 -dname "CN=ScreenHelper, OU=Development, O=SuperClam, L=Beijing, S=Beijing, C=CN"
    if %ERRORLEVEL% NEQ 0 (
        echo 密钥库创建失败！
        pause
        exit /b 1
    )
    echo 密钥库创建成功！
)

echo 签名APK...
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore %KEYSTORE% -storepass 123456 -keypass 123456 "%UNSIGNED_APK%" screenhelper
if %ERRORLEVEL% NEQ 0 (
    echo APK签名失败！
    pause
    exit /b 1
)

echo 复制签名后的APK...
copy "%UNSIGNED_APK%" "%SIGNED_APK%"

echo 验证APK签名...
jarsigner -verify -verbose -certs "%SIGNED_APK%"

echo.
echo ========================================
echo 签名完成！
echo ========================================
echo.
echo 签名后的APK: %SIGNED_APK%
echo.
echo 注意：此签名仅用于测试，正式发布需要使用正式的签名证书。
echo.
echo 按任意键退出...
pause > nul
