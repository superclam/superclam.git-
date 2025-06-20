#!/bin/bash

echo "========================================="
echo "🚀 ScreenHelper 在线构建脚本"
echo "========================================="
echo

# 检查是否在Gitpod环境中
if [ -n "$GITPOD_WORKSPACE_ID" ]; then
    echo "✅ 检测到Gitpod环境"
    ANDROID_HOME="/opt/android-sdk-linux"
elif [ -n "$CODESPACE_NAME" ]; then
    echo "✅ 检测到GitHub Codespaces环境"
    # 安装Android SDK
    echo "📦 正在安装Android SDK..."
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip
    unzip -q commandlinetools-linux-8512546_latest.zip
    mkdir -p android-sdk/cmdline-tools
    mv cmdline-tools android-sdk/cmdline-tools/latest
    export ANDROID_HOME="$PWD/android-sdk"
    export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
    
    # 接受许可证并安装必要组件
    yes | sdkmanager --licenses
    sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"
else
    echo "⚠️  未检测到在线环境，尝试使用本地配置"
    ANDROID_HOME="/opt/android-sdk"
fi

# 设置环境变量
export ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

echo "🔧 Android SDK路径: $ANDROID_HOME"

# 创建local.properties文件
echo "📝 创建local.properties文件..."
echo "sdk.dir=$ANDROID_HOME" > local.properties

# 设置gradlew权限
echo "🔑 设置执行权限..."
chmod +x gradlew

# 显示Java版本
echo "☕ Java版本信息:"
java -version

echo
echo "🧹 清理项目..."
./gradlew clean

echo
echo "🔨 构建Debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Debug APK构建成功！"
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        echo "📱 Debug APK位置: app/build/outputs/apk/debug/app-debug.apk"
        ls -lh app/build/outputs/apk/debug/app-debug.apk
    fi
else
    echo "❌ Debug APK构建失败"
    exit 1
fi

echo
echo "🔨 构建Release APK..."
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo "✅ Release APK构建成功！"
    if [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
        echo "📱 Release APK位置: app/build/outputs/apk/release/app-release-unsigned.apk"
        ls -lh app/build/outputs/apk/release/app-release-unsigned.apk
    fi
else
    echo "⚠️  Release APK构建失败，但Debug版本可用"
fi

echo
echo "========================================="
echo "🎉 构建完成！"
echo "========================================="
echo
echo "📁 APK文件位置："
find . -name "*.apk" -type f 2>/dev/null | while read apk; do
    echo "  📱 $apk ($(du -h "$apk" | cut -f1))"
done

echo
echo "📥 下载APK文件："
echo "1. 右键点击APK文件"
echo "2. 选择'Download'下载到本地"
echo "3. 传输到手机进行安装"
