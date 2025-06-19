# ScreenHelper 构建说明

## 自动构建（推荐）

项目已配置GitHub Actions自动构建，每次推送代码到main分支都会自动构建APK并创建Release。

### 查看构建状态
1. 访问 [GitHub Actions](https://github.com/superclam/superclam.git-/actions)
2. 查看最新的构建状态
3. 构建完成后会自动创建Release并上传APK文件

### 下载APK
- 访问 [Releases页面](https://github.com/superclam/superclam.git-/releases)
- 下载最新版本的APK文件

## 手动构建

如果需要在本地构建APK，请按照以下步骤：

### 环境要求
- Java 17+ (已安装在 D:\Github\Java)
- Android SDK (可选，GitHub Actions会自动下载)
- Git

### 构建步骤

#### 方法1：使用构建脚本（推荐）
```bash
# 运行构建脚本
build_apk.bat
```

#### 方法2：手动命令
```bash
# 设置Java环境
set JAVA_HOME=D:\Github\Java
set PATH=%JAVA_HOME%\bin;%PATH%

# 清理项目
gradlew.bat clean

# 构建Debug版本
gradlew.bat assembleDebug

# 构建Release版本
gradlew.bat assembleRelease
```

### 输出文件位置
- Debug APK: `app\build\outputs\apk\debug\app-debug.apk`
- Release APK: `app\build\outputs\apk\release\app-release-unsigned.apk`

## APK签名（可选）

Release版本的APK是未签名的，如果需要签名：

```bash
# 运行签名脚本
sign_apk.bat
```

签名后的APK位置：`app\build\outputs\apk\release\ScreenHelper-signed.apk`

## 常见问题

### 1. 构建失败
- 检查Java环境是否正确设置
- 确保网络连接正常（需要下载依赖）
- 查看错误日志定位问题

### 2. 权限问题
- 确保有写入权限
- 以管理员身份运行命令

### 3. 网络问题
- 如果下载依赖失败，可以配置代理
- 或者使用国内镜像源

## 发布流程

1. **代码提交**
   ```bash
   git add .
   git commit -m "版本更新说明"
   git push
   ```

2. **自动构建**
   - GitHub Actions自动触发构建
   - 构建成功后自动创建Release

3. **APK分发**
   - 从GitHub Releases下载APK
   - 可以分享到蓝奏云、百度网盘等平台

## 版本管理

- 版本号格式：v1.0.{构建号}
- 每次推送到main分支会自动递增构建号
- 可以在`app/build.gradle`中修改版本信息

## 注意事项

1. **仅供学习研究使用**
2. **请遵守相关法律法规**
3. **不同设备效果可能不同**
4. **目标应用可能更新检测机制**

## 技术支持

如有问题，请通过以下方式联系：
- GitHub Issues: https://github.com/superclam/superclam.git-/issues
- 项目地址: https://github.com/superclam/superclam.git-
