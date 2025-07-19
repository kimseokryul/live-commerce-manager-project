# nginx + rtmp + ffmpeg 포함된 베이스 이미지 사용
FROM tiangolo/nginx-rtmp

# ffmpeg 설치 (영상 리사이징, 인코딩)
RUN apt-get update && apt-get install -y ffmpeg

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 1935 80
CMD ["nginx", "-g", "daemon off;"]