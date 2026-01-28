#!/bin/bash

# Bazi SDK 创建 Release 脚本

set -e

echo "========================================"
echo "  Bazi SDK - 创建 Release"
echo "========================================"
echo ""

cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk

# 1. 询问版本号
read -p "请输入版本号 (默认: 1.0.0): " version
version=${version:-1.0.0}

echo ""
echo "📋 准备创建版本: v${version}"
echo ""

# 2. 确认
read -p "确认创建版本 v${version}? (y/n): " confirm

if [ "$confirm" != "y" ]; then
    echo "❌ 已取消"
    exit 0
fi

# 3. 创建 Tag
echo ""
echo "🏷️  创建 Tag: v${version}..."
git tag v${version}

echo "✅ Tag 创建成功"
echo ""

# 4. 推送 Tag
echo "🚀 推送 Tag 到 GitHub..."
git push origin v${version}

echo ""
echo "========================================"
echo "✅ Tag 推送成功!"
echo "========================================"
echo ""
echo "下一步: 在 GitHub 创建 Release"
echo ""
echo "请访问:"
echo "https://github.com/zhihao93li/bazi-sdk/releases/new?tag=v${version}"
echo ""
echo "填写信息:"
echo "- Tag: v${version} (已自动选择)"
echo "- Title: v${version} - Initial Release"
echo "- Description: (复制 CHANGELOG.md 的内容)"
echo ""
echo "然后点击 'Publish release'"
echo ""
echo "GitHub Actions 将自动发布到 GitHub Packages! 🎉"
echo ""
