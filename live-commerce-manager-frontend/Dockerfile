# 1단계: Vue 앱 빌드
FROM node:18-alpine AS build-stage
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 2단계: RTMP 모듈 포함 Nginx 사용
FROM tiangolo/nginx-rtmp:latest AS production-stage

# ✅ /opt/data/hls 및 /opt/data/hls/live 디렉토리 생성
RUN mkdir -p /opt/data/hls/live

# ✅ 설정파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# ✅ 빌드된 정적파일 복사
COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
