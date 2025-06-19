# 手动创建Release指南

如果GitHub Actions构建遇到问题，您可以手动创建Release：

## 方法1：等待自动构建
1. 访问 [GitHub Actions](https://github.com/superclam/superclam.git-/actions)
2. 等待构建完成（通常需要5-10分钟）
3. 构建成功后会自动创建Release

## 方法2：手动创建Release
如果自动构建失败，可以手动创建：

### 步骤1：访问Release页面
1. 打开 https://github.com/superclam/superclam.git-/releases
2. 点击 "Create a new release"

### 步骤2：填写Release信息
- **Tag version**: v1.0.1
- **Release title**: ScreenHelper v1.0.1
- **Description**: 
```
## ScreenHelper - 截屏检测绕过助手

### 功能特性
- 绕过应用截屏检测
- 虚拟环境模拟
- 增强截屏功能
- 分屏检测绕过

### 使用说明
1. 下载并安装APK文件
2. 授予必要权限
3. 启用检测绕过模式
4. 启动截屏服务
5. 在目标应用中正常使用

### 注意事项
- 仅供学习研究使用
- 请遵守相关法律法规
- 不同设备效果可能不同

### 下载说明
由于构建环境限制，本版本为手动上传。
如需最新自动构建版本，请等待GitHub Actions完成构建。
```

### 步骤3：上传APK文件
如果您有预构建的APK文件，可以直接拖拽上传。

## 方法3：使用GitHub Actions Artifacts
1. 访问 [GitHub Actions](https://github.com/superclam/superclam.git-/actions)
2. 点击最新的成功构建
3. 在页面底部找到 "Artifacts" 部分
4. 下载 app-debug 和 app-release 文件

## 方法4：本地构建
如果您有Android开发环境：

```bash
# 克隆仓库
git clone https://github.com/superclam/superclam.git-.git
cd superclam.git-

# 构建APK
./gradlew assembleDebug
./gradlew assembleRelease
```

构建完成后，APK文件位于：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 常见问题

### Q: GitHub Actions构建失败
A: 可能的原因：
- 网络问题导致依赖下载失败
- Android SDK版本不兼容
- 构建环境配置问题

### Q: 如何检查构建状态
A: 
1. 访问Actions页面
2. 查看最新的workflow运行状态
3. 点击具体的构建查看详细日志

### Q: APK文件在哪里下载
A: 
- 自动构建：Releases页面
- 手动构建：Actions页面的Artifacts
- 本地构建：项目的build目录

## 联系支持

如果遇到问题，请：
1. 查看 [GitHub Issues](https://github.com/superclam/superclam.git-/issues)
2. 创建新的Issue描述问题
3. 提供详细的错误信息和环境描述

---

**注意**：本指南仅在自动构建遇到问题时使用。正常情况下，GitHub Actions会自动处理所有构建和发布流程。
