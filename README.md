# Android 通知监听插件（OTC Notification Listener）

## 📖 插件简介

这是一个用于 uni-app 的原生 Android 插件，可以实时监听系统通知消息，包括短信、微信、QQ、钉钉等所有应用的通知。适用于需要读取或监控系统通知的应用场景。

**插件 ID**：`otc-notification-listener`

**版本**：1.0.0

**支持平台**：Android

## ✨ 功能特性

- ✅ 实时监听系统所有应用的通知消息
- ✅ 获取通知的应用包名、标题、内容
- ✅ 支持长文本内容提取
- ✅ 提供权限检查和引导开启功能
- ✅ 支持服务运行状态检测
- ✅ 自动处理生命周期，防止内存泄漏

## 📦 安装方式

### 方式一：云端插件（推荐）

1. 登录 [DCloud 插件市场](https://ext.dcloud.net.cn/)
2. 搜索 `otc-notification-listener` 或访问插件详情页
3. 点击 **使用 HBuilderX 导入插件** 或 **购买插件**
4. 在 HBuilderX 中打开你的项目
5. 在 `manifest.json` 中配置插件
6. 提交云打包

### 方式二：本地插件

1. 将插件包解压到项目的 `nativeplugins` 目录：
   ```
   /nativeplugins/otc-notification-listener/
   ```

2. 在 `manifest.json` 的 App 原生插件配置中添加：
   ```json
   {
     "nativePlugins": {
       "otc-notification-listener": {
         "type": "module",
         "name": "OtcNotificationListener"
       }
     }
   }
   ```

3. 制作自定义基座或提交云打包

## ⚙️ 权限配置

### 1. manifest.json 权限配置

插件会自动添加所需权限，无需手动配置。但如果需要，可以在 `manifest.json` 的 Android 配置中确认：

```json
{
  "app-plus": {
    "distribute": {
      "android": {
        "permissions": [
          "<uses-permission android:name=\"android.permission.BIND_NOTIFICATION_LISTENER_SERVICE\"/>"
        ]
      }
    }
  }
}
```

### 2. 用户授权

Android 通知监听需要用户手动授权，插件提供了便捷的授权引导方法：

```javascript
// 1. 检查是否已授权
const hasPermission = notificationModule.checkPermission();

// 2. 如果未授权，引导用户开启
if (!hasPermission) {
  notificationModule.openSettings();
}
```

用户需要在系统设置中找到你的应用，开启"通知使用权"。

## 📚 API 说明

### 引入模块

```javascript
const notificationModule = uni.requireNativePlugin('OtcNotificationListener');
```

### 方法列表

#### 1. checkPermission()

检查是否已获得通知监听权限。

**参数**：无

**返回值**：`Boolean`
- `true`：已授权
- `false`：未授权

**示例**：
```javascript
const hasPermission = notificationModule.checkPermission();
console.log('是否有权限：', hasPermission);
```

---

#### 2. openSettings()

打开系统的通知监听设置页面，引导用户授权。

**参数**：无

**返回值**：无

**示例**：
```javascript
notificationModule.openSettings();
```

---

#### 3. startListening(callback)

开始监听系统通知，并设置回调函数接收通知数据。

**参数**：
- `callback`：`Function` - 回调函数，会被多次调用（每次收到通知都会触发）

**回调函数接收的数据对象**：
```javascript
{
  packageName: String,  // 应用包名，如 "com.tencent.mm"
  title: String,        // 通知标题
  text: String,         // 通知内容
  timestamp: Number     // 时间戳（毫秒）
}
```

**示例**：
```javascript
notificationModule.startListening((data) => {
  console.log('收到通知：', data);
  console.log('应用包名：', data.packageName);
  console.log('标题：', data.title);
  console.log('内容：', data.text);
  console.log('时间：', new Date(data.timestamp));
});
```

---

#### 4. stopListening()

停止监听通知。

**参数**：无

**返回值**：无

**示例**：
```javascript
notificationModule.stopListening();
```

---

#### 5. isServiceRunning()

检查通知监听服务是否正在运行。

**参数**：无

**返回值**：`Boolean`
- `true`：服务运行中
- `false`：服务未运行

**示例**：
```javascript
const isRunning = notificationModule.isServiceRunning();
console.log('服务状态：', isRunning);
```

## 💡 使用示例

### 完整示例代码

```vue
<template>
  <view class="container">
    <view class="header">
      <text class="title">通知监听插件</text>
    </view>
    
    <view class="status-box">
      <text class="status-label">权限状态：</text>
      <text :class="hasPermission ? 'status-yes' : 'status-no'">
        {{ hasPermission ? '已授权' : '未授权' }}
      </text>
    </view>
    
    <view class="status-box">
      <text class="status-label">服务状态：</text>
      <text :class="isRunning ? 'status-yes' : 'status-no'">
        {{ isRunning ? '运行中' : '未运行' }}
      </text>
    </view>
    
    <view class="btn-group">
      <button @click="checkPermission">检查权限</button>
      <button @click="openSettings" v-if="!hasPermission">开启权限</button>
      <button @click="startListening" :disabled="isListening">开始监听</button>
      <button @click="stopListening" :disabled="!isListening">停止监听</button>
    </view>
    
    <view class="log-box">
      <text class="log-title">通知日志：</text>
      <scroll-view scroll-y class="log-scroll">
        <view v-for="(log, index) in logs" :key="index" class="log-item">
          <text class="log-time">{{ log.time }}</text>
          <text class="log-app">{{ log.app }}</text>
          <text class="log-title-text">{{ log.title }}</text>
          <text class="log-content">{{ log.content }}</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      notificationModule: null,
      hasPermission: false,
      isRunning: false,
      isListening: false,
      logs: []
    }
  },
  
  onLoad() {
    // 引入插件
    this.notificationModule = uni.requireNativePlugin('OtcNotificationListener');
    
    // 初始检查
    this.checkPermission();
    this.checkServiceStatus();
  },
  
  onUnload() {
    // 页面卸载时停止监听
    if (this.isListening) {
      this.stopListening();
    }
  },
  
  methods: {
    // 检查权限
    checkPermission() {
      this.hasPermission = this.notificationModule.checkPermission();
      uni.showToast({
        title: this.hasPermission ? '已有权限' : '未授权',
        icon: 'none'
      });
    },
    
    // 打开设置页面
    openSettings() {
      uni.showModal({
        title: '需要授权',
        content: '请在系统设置中开启通知使用权限，然后重新打开应用',
        confirmText: '去设置',
        success: (res) => {
          if (res.confirm) {
            this.notificationModule.openSettings();
          }
        }
      });
    },
    
    // 检查服务状态
    checkServiceStatus() {
      this.isRunning = this.notificationModule.isServiceRunning();
    },
    
    // 开始监听
    startListening() {
      if (!this.hasPermission) {
        uni.showToast({
          title: '请先授权',
          icon: 'none'
        });
        return;
      }
      
      this.notificationModule.startListening((data) => {
        console.log('收到通知：', data);
        
        // 添加到日志
        this.logs.unshift({
          time: this.formatTime(data.timestamp),
          app: this.getAppName(data.packageName),
          title: data.title || '(无标题)',
          content: data.text || '(无内容)'
        });
        
        // 最多保留50条
        if (this.logs.length > 50) {
          this.logs.pop();
        }
      });
      
      this.isListening = true;
      this.checkServiceStatus();
      
      uni.showToast({
        title: '开始监听',
        icon: 'success'
      });
    },
    
    // 停止监听
    stopListening() {
      this.notificationModule.stopListening();
      this.isListening = false;
      
      uni.showToast({
        title: '已停止监听',
        icon: 'success'
      });
    },
    
    // 格式化时间
    formatTime(timestamp) {
      const date = new Date(timestamp);
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
    },
    
    // 获取应用名称（可以自己维护一个包名映射表）
    getAppName(packageName) {
      const appNames = {
        'com.tencent.mm': '微信',
        'com.tencent.mobileqq': 'QQ',
        'com.alibaba.android.rimet': '钉钉',
        'com.tencent.wework': '企业微信',
        'com.eg.android.AlipayGphone': '支付宝'
      };
      return appNames[packageName] || packageName;
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
}

.header {
  margin-bottom: 30px;
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

.status-box {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 5px;
}

.status-label {
  font-size: 16px;
  margin-right: 10px;
}

.status-yes {
  color: #07c160;
  font-weight: bold;
}

.status-no {
  color: #ff0000;
  font-weight: bold;
}

.btn-group {
  margin: 20px 0;
}

.btn-group button {
  margin-bottom: 10px;
}

.log-box {
  margin-top: 20px;
}

.log-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.log-scroll {
  height: 400px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  padding: 10px;
}

.log-item {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f9f9f9;
  border-left: 3px solid #07c160;
}

.log-time {
  color: #999;
  font-size: 12px;
  display: block;
  margin-bottom: 5px;
}

.log-app {
  color: #07c160;
  font-weight: bold;
  font-size: 14px;
  display: block;
  margin-bottom: 5px;
}

.log-title-text {
  font-size: 15px;
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
}

.log-content {
  color: #666;
  font-size: 14px;
  display: block;
}
</style>
```

### 简单示例

```javascript
// 引入模块
const notificationModule = uni.requireNativePlugin('OtcNotificationListener');

// 1. 检查权限
const hasPermission = notificationModule.checkPermission();

// 2. 如果没有权限，打开设置页面
if (!hasPermission) {
  notificationModule.openSettings();
} else {
  // 3. 开始监听
  notificationModule.startListening((data) => {
    console.log('收到通知：', data);
    // 处理通知数据
  });
}

// 4. 不需要时停止监听
// notificationModule.stopListening();
```

## 📱 常见应用包名

为了方便识别不同应用的通知，以下是一些常见应用的包名：

| 应用名称 | 包名 |
|---------|------|
| 微信 | com.tencent.mm |
| QQ | com.tencent.mobileqq |
| 钉钉 | com.alibaba.android.rimet |
| 企业微信 | com.tencent.wework |
| 支付宝 | com.eg.android.AlipayGphone |
| 短信 | com.android.mms |
| 抖音 | com.ss.android.ugc.aweme |
| 淘宝 | com.taobao.taobao |
| 京东 | com.jingdong.app.mall |

## ❓ 常见问题

### 1. 为什么检查权限返回 true，但收不到通知？

**原因**：可能是以下几种情况：
- 通知监听服务还没有启动
- 系统延迟启动服务
- 应用被杀死后需要重新启动

**解决方案**：
```javascript
// 检查服务是否运行
const isRunning = notificationModule.isServiceRunning();
if (!isRunning) {
  // 提示用户重启应用或稍等片刻
  uni.showToast({
    title: '服务启动中，请稍候',
    icon: 'none'
  });
}
```

### 2. 如何过滤特定应用的通知？

在回调函数中根据 `packageName` 进行过滤：

```javascript
notificationModule.startListening((data) => {
  // 只监听微信通知
  if (data.packageName === 'com.tencent.mm') {
    console.log('微信通知：', data);
  }
  
  // 或者排除某些应用
  const excludeApps = ['com.android.systemui', 'com.miui.securitycenter'];
  if (!excludeApps.includes(data.packageName)) {
    console.log('通知：', data);
  }
});
```

### 3. 应用进入后台或息屏后还能监听吗？

可以，但需要注意：
- 部分厂商（小米、华为等）的省电优化可能杀死后台服务
- 建议在应用中引导用户关闭省电优化
- 或者申请后台运行白名单

### 4. 获取不到通知内容怎么办？

部分应用的通知可能不包含完整内容，或者内容被加密：
```javascript
notificationModule.startListening((data) => {
  if (!data.text) {
    console.log('该通知无可读内容');
  }
});
```

### 5. 如何在应用重启后自动开始监听？

在 `App.vue` 的 `onLaunch` 中启动：

```javascript
// App.vue
export default {
  onLaunch() {
    const notificationModule = uni.requireNativePlugin('OtcNotificationListener');
    
    // 检查权限
    if (notificationModule.checkPermission()) {
      // 自动开始监听
      notificationModule.startListening((data) => {
        // 使用 uni.$emit 发送全局事件
        uni.$emit('notification', data);
      });
    }
  }
}
```

## ⚠️ 注意事项

### 1. 隐私合规

- 通知监听是敏感权限，务必在隐私政策中说明用途
- 收集到的数据请妥善保管，不要上传敏感信息
- 遵守相关法律法规，不要用于非法用途

### 2. 性能优化

- 避免在回调函数中进行耗时操作
- 及时清理不需要的通知日志数据
- 不使用时及时调用 `stopListening()`

### 3. 兼容性

- 插件仅支持 Android 平台
- 建议 Android 5.0（API 21）及以上
- 部分厂商可能有额外限制

### 4. 测试建议

- 在真机上测试，模拟器可能无法正常工作
- 测试多个不同厂商的设备（小米、华为、OPPO、vivo等）
- 测试不同场景：前台、后台、息屏等

## 🔧 开发和编译

如果你需要修改插件源码或自行编译：

### 环境要求

- Android Studio 4.0+
- Gradle 6.0+
- uni-app 离线 SDK

### 编译步骤

1. 下载 uni-app Android 离线 SDK
2. 将 `uniapp-v8-release.aar` 放入 `android/libs/` 目录
3. 在 Android Studio 中打开 `android` 目录
4. 运行编译命令：
   ```bash
   ./gradlew assembleRelease
   ```
5. 编译产物位于：`android/build/outputs/aar/`

详细步骤请参考项目中的 `下载SDK步骤.md` 和 `超简单编译步骤.md`。

## 📄 开源协议

本插件遵循 MIT 协议开源。

## 🔗 相关链接

- [插件市场地址](https://ext.dcloud.net.cn/)
- [GitHub 仓库](https://github.com/gaoxl1992/android-plugin-monidtor)
- [问题反馈](https://github.com/gaoxl1992/android-plugin-monidtor/issues)

## 📮 联系方式

如有问题或建议，欢迎通过以下方式联系：

- GitHub Issues
- Email: 19528863269@163.com

## 🙏 致谢

感谢 DCloud 团队提供的优秀开发框架。

---

**版本历史**

- v1.0.0 (2024-12-17)
  - 首次发布
  - 支持基本的通知监听功能
  - 提供完整的权限管理接口

