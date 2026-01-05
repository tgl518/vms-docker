# VMS 前端 Dockerfile（使用预构建的 dist）
FROM nginx:alpine

# 复制预构建的前端文件
COPY dist/ /usr/share/nginx/html/

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
