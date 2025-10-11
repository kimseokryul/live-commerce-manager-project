# 🛒 라이브 커머스 관리자 시스템

> 실시간 방송 및 채팅을 통해 상품을 판매하고 관리할 수 있는 판매자 전용 웹 플랫폼

<br>

## 📋 목차
- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [시스템 아키텍처](#-시스템-아키텍처)
- [담당 기능](#-담당-기능)
- [핵심 구현 사항](#-핵심-구현-사항)
- [설치 및 실행](#-설치-및-실행)
- [트러블슈팅](#-트러블슈팅)

<br>

## 🎯 프로젝트 소개

### 개요
- **서비스명**: 라이브 커머스 관리자 시스템
- **개발 기간**: 2025.05 ~ 2025.07
- **팀 구성**: 3명 (백엔드 2명, 프론트엔드 1명)
- **역할**: 백엔드 개발 (실시간 채팅 시스템, 판매자 CRUD)

### 프로젝트 설명
판매자가 라이브 방송을 진행하며 시청자와 실시간 채팅으로 소통하고, 상품을 효율적으로 관리할 수 있는 통합 관리 시스템입니다.

<br>

## ✨ 주요 기능

### 1️⃣ 실시간 채팅 시스템
- **WebSocket + STOMP** 기반 양방향 실시간 채팅
- **판매자/관리자 권한 분리**
  - 판매자: 공지사항 등록, 특정 사용자 채팅 금지 (5분)
  - 관리자: 전체 채팅 제어 및 관리
- **Redis 기반 상태 관리**
  - 채팅 참여자 수 실시간 집계
  - 채팅 금지 사용자 TTL 관리 (자동 해제)
- **로그인/비로그인 사용자 구분**
  - JWT 기반 인증된 사용자 관리
  - UUID 기반 비회원 식별

### 2️⃣ 판매자 상품 관리 (CRUD)
- **상품 등록/수정/삭제**
  - 다중 이미지 업로드 및 관리
  - 카테고리별 상품 분류
  - 재고 관리 및 가격 설정
- **상품 리뷰 관리**
  - 리뷰 목록 조회 및 상세 보기
  - 판매자 답변 등록
- **상품 문의 관리**
  - 1:1 문의 목록 조회
  - 답변 등록 및 관리
- **상품 목록 조회**
  - 페이징, 정렬, 필터링 기능
  - 판매 상태별 조회 (판매중/품절/중단)

### 3️⃣ 라이브 방송 관리
- **RTMP → HLS 스트리밍**
  - Nginx RTMP 서버를 통한 영상 변환
  - OBS WebSocket 연동으로 방송 제어
- **방송 정보 관리**
  - 방송 생성/수정/종료
  - 실시간 시청자 수 표시

### 4️⃣ 주문/매출 관리
- **주문 목록 조회**
  - 상태별 주문 조회 (신규/처리중/완료/취소)
  - 주문 상세 정보 및 배송 처리
- **매출 통계 대시보드**
  - 일별/월별 매출 현황
  - 상품별 판매 순위
  - Chart.js 기반 시각화

<br>

## 🛠 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.1.3, Spring Security, Spring Data JPA
- **Database**: MariaDB, MyBatis
- **Cache**: Redis
- **Real-time**: WebSocket, STOMP, SockJS
- **Auth**: JWT (jsonwebtoken)
- **API Docs**: Swagger (SpringDoc OpenAPI)

### Frontend
- **Framework**: Vue.js 3
- **Build Tool**: Vite
- **State Management**: Pinia
- **HTTP Client**: Axios
- **UI**: Tailwind CSS
- **Real-time**: @stomp/stompjs, SockJS-client
- **Media**: HLS.js (영상 스트리밍)
- **Editor**: Toast UI Editor (리치 텍스트 에디터)
- **Charts**: Chart.js, ECharts

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **Web Server**: Nginx (Reverse Proxy, Load Balancer)
- **Streaming**: Nginx RTMP (alfg/nginx-rtmp)
- **CI/CD**: GitHub Actions
- **Cloud**: AWS EC2

<br>

## 🏗 시스템 아키텍처

```
┌─────────────────┐
│   사용자        │
│  (판매자/관리자) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Nginx          │  ← Reverse Proxy
│  (Port 80)      │
└────────┬────────┘
         │
    ┌────┴─────┬───────────┬──────────┐
    ▼          ▼           ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│Frontend│ │Backend │ │ Redis  │ │ RTMP   │
│Vue.js  │ │Spring  │ │Cache   │ │Streaming│
│(8082)  │ │(8081)  │ │(6379)  │ │(1935)  │
└────────┘ └────┬───┘ └────────┘ └────────┘
               │
               ▼
         ┌──────────┐
         │ MariaDB  │
         └──────────┘
```

<br>

## 👨‍💻 담당 기능

### 1. 실시간 채팅 시스템 (100%)
**Backend**
- WebSocket + STOMP 프로토콜 기반 실시간 통신 구현
- Redis를 활용한 채팅 상태 관리 (참여자 수, 금지 사용자)
- 권한 기반 채팅 제어 로직 (판매자/관리자 분리)
- JWT 인증 연동 및 비회원 UUID 식별
- 채팅 히스토리 DB 저장 및 조회

**Frontend**
- Vue.js 기반 채팅 UI/UX 구현
- STOMP 클라이언트 연동
- 실시간 메시지 송수신 처리
- 이모지, 공지사항, 참여자 수 표시

### 2. 판매자 상품 관리 CRUD (100%)
**Backend**
- 상품 등록/수정/삭제 REST API 개발
- 다중 이미지 업로드 처리
- 리뷰/문의 관리 API
- 페이징, 정렬, 필터링 로직

**Frontend**
- 상품 목록 조회 화면
- 상품 등록/수정 폼
- 리뷰/문의 관리 UI
- Toast UI Editor 연동 (상품 설명 입력)

<br>

## 🔥 핵심 구현 사항

### 1. WebSocket + STOMP 실시간 채팅 구현

**문제**
- 라이브 방송 중 판매자와 시청자 간 실시간 소통 필요
- 로그인/비로그인 사용자 모두 채팅 참여 가능해야 함
- 판매자는 부적절한 채팅을 제어할 수 있어야 함

**해결**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 구독 prefix
        config.setApplicationDestinationPrefixes("/app"); // 발행 prefix
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS fallback 지원
    }
}
```

**성과**
- 안정적인 양방향 실시간 채팅 구현
- 로그인/비로그인 사용자 구분 및 식별
- SockJS fallback으로 브라우저 호환성 확보

---

### 2. Redis 기반 채팅 상태 관리

**문제**
- 채팅 참여자 수를 실시간으로 집계해야 함
- 채팅 금지된 사용자는 5분 후 자동 해제되어야 함
- DB 조회 없이 빠른 상태 확인 필요

**해결**
```java
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    // 참여자 수 증가
    public void incrementViewerCount(Long broadcastId) {
        String key = "broadcast:" + broadcastId + ":viewers";
        redisTemplate.opsForValue().increment(key);
    }
    
    // 채팅 금지 (5분 TTL)
    public void banUser(String userId, Long broadcastId) {
        String key = "chat:ban:" + broadcastId + ":" + userId;
        redisTemplate.opsForValue().set(key, "banned", Duration.ofMinutes(5));
    }
    
    // 금지 여부 확인
    public boolean isBanned(String userId, Long broadcastId) {
        String key = "chat:ban:" + broadcastId + ":" + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
```

**성과**
- 채팅 참여자 수 실시간 집계 (DB 부하 제로)
- TTL 기반 자동 금지 해제 (스케줄러 불필요)
- Redis 도입으로 응답 속도 향상

---

### 3. 권한 기반 채팅 제어

**문제**
- 판매자는 공지 등록 및 특정 사용자 채팅 금지만 가능
- 관리자는 전체 제어 가능
- 권한 없는 사용자의 요청은 차단되어야 함

**해결**
```java
@MessageMapping("/chat/notice")
public void sendNotice(@Payload ChatMessageDTO message, 
                       @Header("Authorization") String token) {
    // JWT에서 사용자 역할 추출
    String role = jwtUtil.getRoleFromToken(token);
    
    // 판매자 또는 관리자만 공지 가능
    if (!"SELLER".equals(role) && !"ADMIN".equals(role)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 없음");
    }
    
    message.setType("NOTICE");
    messagingTemplate.convertAndSend("/topic/chat/" + message.getBroadcastId(), message);
}

@MessageMapping("/chat/ban")
public void banUser(@Payload Map<String, Object> payload,
                    @Header("Authorization") String token) {
    String role = jwtUtil.getRoleFromToken(token);
    
    // 판매자 또는 관리자만 금지 가능
    if (!"SELLER".equals(role) && !"ADMIN".equals(role)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 없음");
    }
    
    String userId = (String) payload.get("userId");
    Long broadcastId = Long.valueOf(payload.get("broadcastId").toString());
    
    chatService.banUser(userId, broadcastId);
}
```

**성과**
- 판매자/관리자 권한 명확히 분리
- JWT 기반 안전한 권한 검증
- 무분별한 기능 사용 방지

---

### 4. 상품 CRUD 및 이미지 업로드

**문제**
- 판매자가 상품을 등록할 때 여러 이미지를 업로드해야 함
- 이미지는 서버에 저장되고, DB에는 경로만 저장
- 상품 수정 시 기존 이미지 유지/삭제 처리 필요

**해결**
```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {
    
    private final ProductService productService;
    private final FileStorageService fileStorageService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> registerProduct(
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        
        // 이미지 업로드 처리
        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = fileStorageService.storeFiles(images);
            productDTO.setImageUrls(imagePaths);
        }
        
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.ok(created);
    }
}
```

**성과**
- 다중 이미지 업로드 안정적 처리
- 파일 저장 로직 분리로 유지보수성 향상
- 상품 이미지 미리보기 기능 구현

<br>

## 📦 설치 및 실행

### 사전 요구사항
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MariaDB 10+
- Redis 6+

### 1. 저장소 클론
```bash
git clone https://github.com/kimseokryul/live-commerce-manager-project.git
cd live-commerce-manager-project
```

### 2. 환경 변수 설정
`.env` 파일 생성:
```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=live_commerce
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

### 3. Docker Compose 실행
```bash
docker-compose up -d
```

### 4. 접속
- **프론트엔드**: http://localhost:8082
- **백엔드 API**: http://localhost:8081
- **Redis**: localhost:6379
- **RTMP 서버**: rtmp://localhost:1935

<br>

## 🐛 트러블슈팅

### 1. WebSocket 연결 불안정 문제
**문제**
- 일부 브라우저에서 WebSocket 연결 실패
- 프록시 환경에서 연결 끊김 현상

**해결**
- SockJS fallback 옵션 추가
- Nginx WebSocket 프록시 설정 추가
```nginx
location /ws-chat {
    proxy_pass http://backend:8080;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

**결과**
- 모든 브라우저에서 안정적인 연결
- 프록시 환경에서도 정상 동작

---

### 2. Redis 캐시 데이터 정합성 문제
**문제**
- 채팅 참여자 수가 실제와 다르게 집계됨
- 서버 재시작 시 Redis 데이터 손실

**해결**
- WebSocket 연결/해제 이벤트 리스너 추가
- 서버 시작 시 Redis 데이터 초기화
```java
@EventListener
public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    chatSessionManager.removeSession(sessionId);
    // Redis 참여자 수 감소
    chatService.decrementViewerCount(broadcastId);
}
```

**결과**
- 정확한 참여자 수 집계
- 연결 끊김 시 자동 정리

---

### 3. 다중 이미지 업로드 성능 문제
**문제**
- 여러 이미지 업로드 시 응답 시간 지연
- 대용량 이미지 업로드 시 메모리 부족

**해결**
- 이미지 리사이징 처리 추가
- 비동기 업로드 적용
```java
@Async
public CompletableFuture<String> storeFileAsync(MultipartFile file) {
    // 이미지 리사이징 (800x800)
    BufferedImage resized = Scalr.resize(originalImage, 800);
    String path = saveImage(resized);
    return CompletableFuture.completedFuture(path);
}
```

**결과**
- 업로드 시간 60% 단축
- 메모리 사용량 감소

<br>

## 📈 성과 및 배운 점

### 성과
- ✅ WebSocket 기반 실시간 채팅 시스템 구축
- ✅ Redis 활용한 효율적인 상태 관리
- ✅ 권한 기반 기능 제어로 보안성 확보
- ✅ 판매자 중심의 직관적인 UI/UX 설계

### 배운 점
- **실시간 통신**: WebSocket, STOMP 프로토콜 이해 및 구현 경험
- **캐시 전략**: Redis를 활용한 성능 최적화 방법
- **권한 관리**: JWT 기반 역할 분리 및 보안 처리
- **비즈니스 로직**: 사용자 관점에서 기능 설계의 중요성

<br>

## 📞 문의

- **GitHub**: [kimseokryul](https://github.com/kimseokryul)
- **Email**: kimseokryul96@gmail.com
- **Blog**: [ryuldev.tistory.com](https://ryuldev.tistory.com/)

<br>

## 📄 License

This project is licensed under the MIT License.
