# 1단계: Vue 앱 빌드 단계
FROM node:18-alpine AS build-stage

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# 2단계: 실행 단계 (Nginx 사용)
FROM nginx:alpine AS production-stage

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf               
COPY default.conf /etc/nginx/conf.d/default.conf       

# 정적 파일 복사
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 포트 오픈
EXPOSE 80

# Nginx 실행 명령
CMD ["nginx", "-g", "daemon off;"]
