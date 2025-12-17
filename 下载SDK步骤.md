# 下载 uni-app Android SDK

## 步骤 1：下载 SDK

访问 DCloud 官网下载页面：

**直接下载链接**：
```
https://nativesupport.dcloud.net.cn/AppDocs/download/android.html
```

或搜索：**"uni-app Android 离线SDK"**

## 步骤 2：解压 SDK

下载后得到一个 zip 文件，解压它。

在解压后的文件夹中找到：
```
SDK/libs/uniapp-v8-release.aar
```

## 步骤 3：复制到项目

把 `uniapp-v8-release.aar` 复制到：
```
/Users/xuelinggao/Documents/develop/work/otc-app/nativeplugins/otc-notification-listener/android/libs/
```

## 步骤 4：重新编译

在 Android Studio 的 Terminal 中运行：
```bash
./gradlew clean assembleRelease
```

---

## 如果下载困难

告诉我，我给你提供**方案 2：简化版插件**（不需要 SDK）
