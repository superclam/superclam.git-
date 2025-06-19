#!/bin/bash

echo "========================================"
echo "ScreenHelper 本地构建脚本"
echo "========================================"
echo

echo "检查Java环境..."
java -version
if [ $? -ne 0 ]; then
    echo "错误: 未找到Java环境，请确保已安装Java 8或更高版本"
    exit 1
fi

echo
echo "检查Android SDK..."
if [ -z "$ANDROID_HOME" ]; then
    echo "警告: 未设置ANDROID_HOME环境变量"
    echo "请确保已安装Android SDK并设置环境变量"
fi

echo
echo "开始构建..."
echo "========================================"

echo "清理项目..."
./gradlew clean
if [ $? -ne 0 ]; then
    echo "错误: 清理项目失败"
    exit 1
fi

echo
echo "构建Debug版本..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "错误: 构建Debug版本失败"
    exit 1
fi

echo
echo "构建Release版本..."
./gradlew assembleRelease
if [ $? -ne 0 ]; then
    echo "错误: 构建Release版本失败"
    exit 1
fi

echo
echo "========================================"
echo "构建完成！"
echo "========================================"

echo
echo "APK文件位置:"
echo "Debug版本: app/build/outputs/apk/debug/app-debug.apk"
echo "Release版本: app/build/outputs/apk/release/app-release-unsigned.apk"

echo
echo "复制APK到根目录..."
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    cp "app/build/outputs/apk/debug/app-debug.apk" "ScreenHelper-debug.apk"
    echo "✓ Debug APK已复制到: ScreenHelper-debug.apk"
fi

if [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
    cp "app/build/outputs/apk/release/app-release-unsigned.apk" "ScreenHelper-release.apk"
    echo "✓ Release APK已复制到: ScreenHelper-release.apk"
fi

echo
echo "========================================"
echo "构建成功完成！"
echo "您可以在项目根目录找到APK文件"
echo "========================================"
echo
