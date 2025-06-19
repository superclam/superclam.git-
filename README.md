# ScreenHelper - 截屏检测绕过助手

[![Build Status](https://github.com/superclam/ScreenHelper/actions/workflows/build.yml/badge.svg)](https://github.com/superclam/ScreenHelper/actions)
[![Release](https://img.shields.io/github/v/release/superclam/ScreenHelper)](https://github.com/superclam/ScreenHelper/releases)
[![License](https://img.shields.io/github/license/superclam/ScreenHelper)](LICENSE)
[![Downloads](https://img.shields.io/github/downloads/superclam/ScreenHelper/total)](https://github.com/superclam/ScreenHelper/releases)

## 📱 快速下载

**[📥 点击下载最新版APK](https://github.com/superclam/ScreenHelper/releases/latest)**

或访问下载页面：**https://superclam.github.io/ScreenHelper/**

## 项目简介

ScreenHelper 是一个Android应用，专门用于绕过某些应用（如学习通）的截屏和分屏检测机制。通过虚拟环境模拟技术，让检测机制认为截屏操作发生在被监控环境之外。

## 核心原理

基于虚拟机环境中检测失效的原理：
- 在虚拟机内运行目标应用时，截屏和分屏检测会失效
- 因为实际的截屏操作发生在宿主机而非虚拟机内
- 本应用通过技术手段模拟这种环境特征

## 主要功能

1. **检测绕过模式** - 通过多种技术手段绕过截屏检测
2. **虚拟显示环境** - 创建虚拟显示层，模拟虚拟机环境
3. **增强截屏功能** - 提供多种截屏方式
4. **分屏检测绕过** - 隐藏或模拟单屏环境

## 技术实现

### 核心技术
- **窗口标志修改** - 移除FLAG_SECURE等安全标志
- **虚拟显示创建** - 使用VirtualDisplay API
- **系统属性模拟** - 修改系统属性模拟虚拟机环境
- **透明覆盖层** - 干扰检测机制
- **反射技术** - 动态修改系统行为

### 主要类说明
- `MainActivity` - 主界面，控制应用功能
- `DetectionBypassHelper` - 检测绕过核心类
- `VirtualDisplayHelper` - 虚拟显示辅助类
- `ScreenshotService` - 截屏服务

## 🚀 快速开始

### 1. 下载安装
- 从 [Releases页面](https://github.com/superclam/ScreenHelper/releases) 下载最新APK
- 或访问 [下载页面](https://superclam.github.io/ScreenHelper/) 直接下载
- 允许未知来源安装并安装APK

### 2. 权限设置
首次启动需要授予以下权限：
- 📁 存储权限（保存截图）
- 🔲 悬浮窗权限（创建覆盖层）
- 📹 屏幕录制权限（截屏功能）

### 3. 使用步骤
1. **启用绕过模式** - 打开"启用检测绕过"开关
2. **启动服务** - 点击"启动服务"并授予权限
3. **打开目标应用** - 如学习通等
4. **正常截屏** - 检测将被自动绕过

## 权限说明

应用需要以下权限：
- `WRITE_EXTERNAL_STORAGE` - 保存截图文件
- `READ_EXTERNAL_STORAGE` - 读取存储内容
- `SYSTEM_ALERT_WINDOW` - 创建悬浮窗
- `FOREGROUND_SERVICE` - 后台服务
- `CAPTURE_VIDEO_OUTPUT` - 屏幕捕获（系统级）

## 兼容性

- **最低版本**: Android 5.0 (API 21)
- **目标版本**: Android 14 (API 34)
- **推荐版本**: Android 7.0+ 以获得最佳体验

## 注意事项

1. **仅供学习研究** - 请勿用于违法违规用途
2. **权限要求** - 部分功能需要系统级权限
3. **兼容性** - 不同设备和系统版本效果可能不同
4. **检测更新** - 目标应用可能更新检测机制

## 免责声明

本项目仅供技术研究和学习使用，开发者不对使用本软件产生的任何后果承担责任。请用户遵守相关法律法规，合理使用本软件。

## 开发环境

- Android Studio 2023.1+
- Gradle 8.2
- Java 8
- Android SDK 34

## 构建说明

```bash
# 克隆项目
git clone https://github.com/superclam/ScreenHelper.git

# 进入项目目录
cd ScreenHelper

# 构建APK
./gradlew assembleRelease
```

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础截屏绕过功能
- 支持虚拟显示环境模拟
- 添加用户友好界面

## 联系方式

- GitHub: [@superclam](https://github.com/superclam)
- 项目地址: https://github.com/superclam/ScreenHelper

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。
