# ScreenHelper Release 信息

## 手动创建Release指南

如果GitHub Actions构建仍然有问题，您可以手动创建Release：

### Release信息模板

**Tag version**: `v1.0.1`
**Release title**: `ScreenHelper v1.0.1 - 截屏检测绕过助手`

**Description**:
```markdown
## ScreenHelper - 截屏检测绕过助手

### 🎯 功能特性
- ✅ 绕过应用截屏检测
- ✅ 虚拟环境模拟技术
- ✅ 增强截屏功能
- ✅ 分屏检测绕过
- ✅ 后台服务支持

### 📱 支持的应用
- 学习通
- 其他具有截屏检测的应用

### 🚀 使用说明
1. **下载安装**: 下载APK文件并安装到手机
2. **权限授予**: 启动应用后授予必要权限
   - 存储权限（保存截图）
   - 悬浮窗权限（创建覆盖层）
   - 屏幕录制权限（截屏功能）
3. **启用功能**: 
   - 打开"启用检测绕过"开关
   - 点击"启动服务"
4. **正常使用**: 在目标应用中正常截屏，检测将被绕过

### 📋 技术原理
基于虚拟机环境中检测失效的原理：
- 在虚拟机内运行目标应用时，截屏检测会失效
- 本应用通过技术手段模拟虚拟环境特征
- 让检测机制认为应用运行在虚拟环境中

### 🔧 兼容性
- **最低版本**: Android 5.0 (API 21)
- **推荐版本**: Android 7.0+ 
- **测试设备**: 主流Android设备

### 📥 下载说明
- **Debug版本**: 包含调试信息，文件较大，适合开发测试
- **Release版本**: 优化版本，推荐日常使用

### ⚠️ 重要提醒
- 本应用仅供学习研究和技术交流使用
- 请勿用于违法违规用途
- 使用本软件产生的任何后果由用户自行承担
- 请遵守相关法律法规，合理使用本软件
- 不同设备和系统版本效果可能不同
- 目标应用可能会更新检测机制

### 🔒 隐私安全
- 不收集用户数据
- 不上传任何信息
- 所有功能均在本地执行
- 开源透明，代码可审查

### 📞 技术支持
- **GitHub Issues**: [提交问题](https://github.com/superclam/superclam.git-/issues)
- **项目地址**: [GitHub仓库](https://github.com/superclam/superclam.git-)
- **下载页面**: [在线下载](https://superclam.github.io/superclam.git-/)

### 📄 开源协议
本项目采用 MIT 开源协议，详见 [LICENSE](https://github.com/superclam/superclam.git-/blob/main/LICENSE) 文件。

---

**构建时间**: 2024年6月19日  
**版本**: v1.0.1  
**开发者**: superclam
```

### APK文件命名
- `ScreenHelper-debug.apk` (Debug版本)
- `ScreenHelper-release.apk` (Release版本)

### 创建步骤
1. 访问 [创建Release页面](https://github.com/superclam/superclam.git-/releases/new)
2. 填入上述信息
3. 如果有预构建的APK文件，直接上传
4. 如果没有APK文件，可以先发布Release，后续通过GitHub Actions自动添加

### 备注
- 如果GitHub Actions成功构建，APK文件会自动添加到Release
- 如果需要手动上传APK，请确保文件名正确
- Release创建后可以随时编辑和添加文件
