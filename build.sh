#!/bin/bash

# Unimed-Cloud 构建脚本
# 用法: ./build.sh [module]
# 示例: 
#   ./build.sh          # 构建所有 7 个模块
#   ./build.sh unimed-dh-relay # 只构建 unimed-dh-relay

set -e

MODULES=(
  "unimed-gateway:unimed-gateway:unimed-gateway/Dockerfile"
  "unimed-auth:unimed-auth:unimed-auth/Dockerfile"
  "unimed-system:unimed-modules/unimed-system:unimed-modules/unimed-system/Dockerfile"
  "unimed-resource:unimed-modules/unimed-resource:unimed-modules/unimed-resource/Dockerfile"
  "unimed-dh:unimed-dh/unimed-dh-core:unimed-dh/unimed-dh-core/Dockerfile"
  "unimed-dh-relay:unimed-dh/unimed-dh-relay:unimed-dh/unimed-dh-relay/Dockerfile"
  "unimed-nacos:unimed-visual/unimed-nacos:unimed-visual/unimed-nacos/Dockerfile"
)

TARGET_MODULES=("$@")

build_module() {
  local module_name=$1
  local module_path=$2
  local dockerfile=$3
  
  echo "=========================================="
  echo "开始构建: $module_name"
  echo "=========================================="
  
  # 构建 JAR
  echo ">>> 构建 JAR: $module_name"
  mvn clean package -pl "$module_path" -am -DskipTests
  
  # 构建 Docker 镜像
  echo ">>> 构建镜像: $module_name"
  docker build -t "$module_name:latest" -f "$dockerfile" "$module_path/"
  
  echo ">>> 完成: $module_name"
  echo ""
}

# 检查 Docker
if ! command -v docker &> /dev/null; then
  echo "错误: docker 未安装"
  exit 1
fi

# 如果没有指定模块，构建所有
if [ ${#TARGET_MODULES[@]} -eq 0 ]; then
  echo "构建所有 7 个模块..."
  for item in "${MODULES[@]}"; do
    IFS=':' read -r name path dockerfile <<< "$item"
    build_module "$name" "$path" "$dockerfile"
  done
else
  # 构建指定模块
  echo "构建指定模块: ${TARGET_MODULES[*]}"
  for target in "${TARGET_MODULES[@]}"; do
    found=false
    for item in "${MODULES[@]}"; do
      IFS=':' read -r name path dockerfile <<< "$item"
      if [[ "$name" == "$target" ]]; then
        build_module "$name" "$path" "$dockerfile"
        found=true
        break
      fi
    done
    if [ "$found" = false ]; then
      echo "警告: 未知模块 '$target'，跳过"
    fi
  done
fi

# 导出镜像
echo "=========================================="
echo "导出所有镜像到 unimed-images.tar"
echo "=========================================="

IMAGE_NAMES=(
  "unimed-gateway:latest"
  "unimed-auth:latest"
  "unimed-system:latest"
  "unimed-resource:latest"
  "unimed-dh:latest"
  "unimed-dh-relay:latest"
  "unimed-nacos:latest"
)

docker save -o unimed-images.tar "${IMAGE_NAMES[@]}"

echo ""
echo "=========================================="
echo "构建完成！"
echo "镜像文件: unimed-images.tar"
echo "=========================================="
