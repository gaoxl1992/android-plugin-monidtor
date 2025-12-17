#!/bin/bash

echo "========================================"
echo "编译 OTC 通知监听插件"
echo "========================================"

cd "$(dirname "$0")/android"

# 检查 Android SDK
if [ ! -d "$HOME/Library/Android/sdk" ]; then
    echo "❌ 错误：未找到 Android SDK"
    echo "请先安装 Android Studio 或配置 Android SDK"
    exit 1
fi

# 检查 libs 目录
if [ ! -d "libs" ]; then
    mkdir libs
    echo "✅ 创建 libs 目录"
fi

# 检查 uni-app SDK
if [ ! -f "libs/uniapp-v8-release.aar" ]; then
    echo "⚠️  警告：未找到 uniapp-v8-release.aar"
    echo ""
    echo "请从以下位置复制 uniapp-v8-release.aar 到 libs 目录："
    echo "  /Applications/HBuilderX.app/Contents/HBuilderX/plugins/android/libs/"
    echo ""
    echo "或者从 DCloud 官网下载 Android 离线 SDK"
    echo ""
    read -p "是否继续编译？(y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "开始编译..."
echo ""

# 清理之前的构建
./gradlew clean

# 编译 AAR
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "✅ 编译成功！"
    echo "========================================"
    echo ""
    echo "生成的 AAR 文件："
    echo "  build/outputs/aar/android-release.aar"
    echo ""
    echo "下一步："
    echo "1. 将生成的 AAR 复制到项目的 android 目录"
    echo "2. 在 HBuilderX 中制作自定义基座"
    echo ""
else
    echo ""
    echo "========================================"
    echo "❌ 编译失败"
    echo "========================================"
    echo ""
    echo "请检查错误信息，或使用 HBuilderX 云打包"
    echo ""
    exit 1
fi
