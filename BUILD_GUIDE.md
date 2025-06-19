# ScreenHelper 构建指南

由于本地环境SSL证书问题，推荐使用以下方法获取APK文件：

## 🚀 方法1：在线构建服务（推荐）

### GitHub Codespaces（免费）
1. 访问项目页面：https://github.com/superclam/superclam.git-
2. 点击绿色的"Code"按钮
3. 选择"Codespaces"标签
4. 点击"Create codespace on main"
5. 等待环境启动完成
6. 在终端中运行：
   ```bash
   ./gradlew assembleDebug assembleRelease
   ```
7. 下载生成的APK文件

### GitPod（免费）
1. 访问：https://gitpod.io/#https://github.com/superclam/superclam.git-
2. 登录GitPod账号（可用GitHub登录）
3. 等待工作空间启动
4. 运行构建命令：
   ```bash
   ./gradlew assembleDebug assembleRelease
   ```
5. 下载APK文件

## 🛠️ 方法2：Android Studio

### 安装Android Studio
1. 下载：https://developer.android.com/studio
2. 安装并启动Android Studio
3. 打开项目：选择项目文件夹
4. 等待Gradle同步完成
5. 构建APK：Build > Build Bundle(s) / APK(s) > Build APK(s)

## 📱 方法3：手动下载Gradle

### 解决SSL问题
1. 下载Gradle 7.6：https://gradle.org/releases/
2. 下载文件：gradle-7.6-bin.zip
3. 解压到：`%USERPROFILE%\.gradle\wrapper\dists\gradle-7.6-bin\`
4. 重新运行构建脚本

### 或者修改Gradle配置
在`gradle/wrapper/gradle-wrapper.properties`中：
```properties
distributionUrl=file:///path/to/gradle-7.6-bin.zip
```

## 📥 方法4：预编译APK

如果以上方法都不可行，可以：

1. **联系朋友帮助**：请有Android开发环境的朋友帮忙构建
2. **使用在线IDE**：如Replit、CodeSandbox等
3. **等待GitHub Actions修复**：定期检查Actions是否成功

## 📲 APK传输到手机

### USB传输
1. 用USB线连接手机和电脑
2. 手机选择"文件传输"模式
3. 将APK复制到手机的Download文件夹
4. 在手机上打开文件管理器安装APK

### 无线传输
1. **QQ/微信**：发送APK文件给自己，手机端下载
2. **云盘**：上传到百度网盘等，手机端下载
3. **邮件**：发送到自己邮箱，手机端下载附件
4. **局域网工具**：使用文件传输工具

### 安装注意事项
1. 允许安装未知来源应用
2. 如果提示安全警告，选择"仍要安装"
3. 安装后授予必要权限

## 🔧 故障排除

### 构建失败
- 检查网络连接
- 尝试使用VPN
- 清理Gradle缓存：`./gradlew clean`
- 重新下载依赖

### 安装失败
- 检查手机存储空间
- 确认允许未知来源安装
- 尝试重启手机后安装

## 📞 获取帮助

如果遇到问题：
1. 查看GitHub Issues
2. 联系项目维护者
3. 寻求技术朋友帮助

---

**最后更新**：2024年6月19日
