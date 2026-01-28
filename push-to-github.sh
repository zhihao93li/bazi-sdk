#!/bin/bash

# Bazi SDK 推送到 GitHub 脚本
# 解决 401/403 认证问题

set -e

echo "========================================"
echo "  Bazi SDK - GitHub 推送脚本"
echo "========================================"
echo ""

# 1. 检查 Git 状态
echo "📋 检查 Git 状态..."
cd /Users/zhihaoli/Documents/项目/tafu-both/bazi-sdk
git status

echo ""
echo "========================================"
echo ""

# 2. 提示配置 GitHub Token
echo "⚠️  重要: GitHub 现在需要使用 Personal Access Token 进行认证"
echo ""
echo "如果您还没有生成 Token,请按照以下步骤操作:"
echo ""
echo "1. 访问: https://github.com/settings/tokens/new"
echo "2. Note: Bazi SDK"
echo "3. Scopes: 勾选 'repo' (完整仓库访问权限)"
echo "4. 点击 'Generate token'"
echo "5. 复制生成的 Token (只显示一次!)"
echo ""
echo "========================================"
echo ""

# 3. 询问是否已有 Token
read -p "您是否已经生成了 GitHub Personal Access Token? (y/n): " has_token

if [ "$has_token" != "y" ]; then
    echo ""
    echo "❌ 请先生成 Token 后再运行此脚本"
    echo ""
    echo "生成地址: https://github.com/settings/tokens/new"
    echo ""
    exit 1
fi

echo ""
read -p "请输入您的 GitHub Token: " github_token
echo ""

if [ -z "$github_token" ]; then
    echo "❌ Token 不能为空"
    exit 1
fi

# 4. 更新 remote URL (使用 Token)
echo "🔧 更新 Git remote URL (使用 Token 认证)..."
git remote set-url origin https://${github_token}@github.com/zhihao93li/bazi-sdk.git

echo "✅ Remote URL 已更新"
echo ""

# 5. 提交更改
echo "📝 提交更改..."
git add .
git commit -m "chore: update GitHub username to zhihao93li" || echo "No changes to commit"

echo ""

# 6. 推送到 GitHub
echo "🚀 推送到 GitHub..."
echo ""

git push -u origin main

echo ""
echo "========================================"
echo "✅ 推送成功!"
echo "========================================"
echo ""
echo "下一步:"
echo "1. 创建 Tag: git tag v1.0.0"
echo "2. 推送 Tag: git push origin v1.0.0"
echo "3. 在 GitHub 创建 Release: https://github.com/zhihao93li/bazi-sdk/releases/new"
echo ""
echo "或者运行: ./create-release.sh"
echo ""
