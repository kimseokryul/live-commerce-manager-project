<template>
  <div class="broadcast-page">
    <!-- 전체 페이지 좌우 섹션 구성 -->
    <div class="main-content">
      
      <!-- 왼쪽: 영상, 정보, 제어버튼, OBS 설정 정보 -->
      <div class="left-section">
        <!-- 방송 영상 플레이어 영역 -->
        <div class="video-section">
          <!-- HLS.js가 붙는 video 태그 -->
          <video ref="videoRef" controls autoplay muted></video>
        </div>

        <!-- 방송 정보 표시 -->
        <div class="info-section">
          <h2>{{ broadcast.title }}</h2>
          <p>방송자: {{ broadcast.broadcaster_id }}</p>
          <p>{{ broadcast.description }}</p>
        </div>

        <!-- 방송 시작/중지 버튼 -->
        <div class="control-buttons">
          <button @click="startBroadcast">방송 시작</button>
          <button @click="stopBroadcast">방송 중지</button>
        </div>

        <!-- RTMP 서버 주소 안내 -->
        <div class="form-group">
          <label>OBS 서버 주소 설정</label>
          <input type="text" :value="rtmp_url" readonly />
        </div>

        <!-- 스트림 키 안내 + 보기/복사 -->
        <div class="form-group">
          <label>OBS 스트림 키</label>
          <div style="display: flex; align-items: center;">
            <input
              ref="streamKeyInput"
              :type="'text'"
              :value="stream_key"
              readonly
              :style="showStreamKey ? '' : 'webkitTextSecurity: disc;'"
              style="flex: 1;"
            />
            <button type="button" @click="toggleStreamKey" style="margin-left: 8px;">
              {{ showStreamKey ? '🙈' : '👁️' }}
            </button>
            <button type="button" @click="copyStreamKey" style="margin-left: 8px;">
              📋
            </button>
          </div>
        </div>
      </div>

      <!-- 오른쪽: 상품 목록, 채팅, 제어 버튼 -->
      <div class="right-section">

        <!-- 시청자 수 뱃지 -->
        <div class="viewer-info">
          <div class="viewer-count-badge">
            <svg class="viewer-icon">...</svg>
            <span class="viewer-count">{{ chatStore.participantCount }}명 시청 중</span>
          </div>
        </div>

        <!-- 상품 목록 -->
        <div class="product-list">
          <div class="product-header" @click="toggleProductList">
            <h3>상품 목록</h3>
            <button class="toggle-button">{{ showProducts ? '접기 ▲' : '펼치기 ▼' }}</button>
          </div>

          <!-- 펼쳤을 때 상품 테이블 표시 -->
          <table v-if="showProducts" class="product-table">
            <thead>
              <tr>
                <th>번호</th>
                <th>상품명</th>
                <th>가격</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in broadcast.productList" :key="index">
                <td>{{ index + 1 }}</td>
                <td>{{ product.product.name }}</td>
                <td>{{ product.product.price.toLocaleString() }}원</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 실시간 채팅창 -->
        <div :class="['chat-box', { collapsed: isCollapsed }]" v-if="broadcast.broadcast_id">
          <SellerChat :broadcastId="String(broadcast.broadcast_id)" />
        </div>

        <!-- 방송 제어 버튼 -->
        <div class="right-buttons">
          <div class="horizontal-buttons">
            <button @click="sendToBroadcast">방송 송출</button>
            <button @click="exitBroadcast">방송 종료</button>
          </div>
          <button class="exit-btn" @click="exitPage">나가기</button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
// Vue의 Composition API 관련 기능 및 외부 라이브러리 import
import { onMounted, reactive, ref } from 'vue'
import OBSWebSocket from 'obs-websocket-js'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Hls from 'hls.js'
import SellerChat from '@/components/chat/SellerChat.vue'
import { useChatStore } from '@/stores/chatStore'

// 방송 정보를 저장하는 reactive 객체 (broadcast_id, 제목, 설명 등)
const broadcast = reactive({
  broadcast_id: '',
  broadcaster_id: '',
  title: '',
  description: '',
  current_viewers: '',
  like_count: '',
  scheduled_start_time: '',
  scheduled_end_time: '',
  productList: [],       // 방송 중 연결된 상품 목록
  viewerList: []         // 시청자 목록
})

// 상태 관리용 ref 변수들
const isCollapsed = ref(false) // 채팅 박스 접기 여부
const stream_key = ref('')     // 스트림 키
const rtmp_url = ref('')       // RTMP 서버 주소
const stream_url = ref('')     // HLS 스트리밍 URL
const showProducts = ref(true) // 상품 목록 표시 여부
const showStreamKey = ref(false) // 스트림 키 노출 여부
const streamKeyInput = ref(null) // 스트림 키 input 태그 참조
const videoRef = ref(null)       // video 태그 참조

// 라우터 및 현재 URL 정보
const router = useRouter()
const route = useRoute()

const chatStore = useChatStore()


// 로그인 시 저장된 JWT 토큰 가져오기
const token = ref(localStorage.getItem('jwt') || sessionStorage.getItem('jwt'))

// 상품 목록 토글 함수
const toggleProductList = () => {
  showProducts.value = !showProducts.value
}

// 방송 상세 정보 조회 및 스트림 정보 설정
const getBroadCasts = async () => {
  if (!broadcast.broadcast_id) {
    console.warn("⛔ broadcast_id가 없습니다:", broadcast.broadcast_id)
    return
  }
  try{
    const response = await axios.get(`/api/broadcast/${broadcast.broadcast_id}`, {
      headers: { Authorization: `Bearer ${token.value}`}
    })
    
    console.log("✅ response.data:", response.data)
    
    Object.assign(broadcast, response.data.broadcast)
    stream_key.value = response.data.stream_key
    rtmp_url.value = response.data.rtmp_url
    stream_url.value = response.data.stream_url

    console.log("broadcast: ", broadcast)
    console.log("stream_url: ", stream_url)
    console.log("stream_key: ", stream_key)
    console.log("rtmp_url: ", rtmp_url)

    playStream()
  } catch(error){
    alert('데이터를 불러오는데 실패했습니다.')
  }
}

// 스트리밍 URL을 기반으로 HLS.js를 사용한 영상 재생 함수
const playStream = () => {
  const hlsUrl = stream_url.value
  console.log(stream_url.value)
  if (Hls.isSupported()) {
    const hls = new Hls({
      liveSyncDuration: 5, // 현재 방송 시간보다 약 5초 뒤로 동기화 (지연 증가)
      liveMaxLatencyDuration: 10, // 최대 10초까지 지연 허용
      enableWorker: true,
      lowLatencyMode: false // 저지연모드 꺼서 버퍼 확보
    })
    hls.loadSource(hlsUrl)
    hls.attachMedia(videoRef.value)
  } else if (videoRef.value.canPlayType('application/vnd.apple.mpegurl')) {
    videoRef.value.src = hlsUrl
  }
}

// 방송 시작 요청
const startBroadcast = async () => {
  console.log("✅ 요청 전에 broadcast_id 확인:", broadcast.broadcast_id);
  console.log("✅ 요청 전에 token 확인:", token);

  try {
    const res = await axios.post(`/api/broadcast/start`, {
      broadcast_id: broadcast.broadcast_id
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })

    console.log("✅ 요청 후에 token 확인:", token);

    if (res.data.status === 'success') {
      alert("방송이 시작되었습니다!");
      // 필요한 경우 스트림 URL 새로고침
      
      broadcast.stream_url = res.data.stream_url;
    } else {
      alert("방송 시작 실패: " + res.data.message);
      
    }
  } catch (e) {
    console.error(e);
    alert("서버 오류로 방송 시작에 실패했습니다.");
    console.error("❌ 요청 실패:", e);
    console.error("📛 에러 메시지:", e.message);
    console.error("📛 응답:", e.response);
  }
};

// 방송 시작 요청
const stopBroadcast = async () => {
  try {
    const res = await axios.post(`/api/broadcast/stop`, {
      broadcast_id: broadcast.broadcast_id
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })

    console.log("✅ 요청 후에 token 확인:", token);

    if (res.data.status === 'success') {
      alert("방송이 중지되었습니다!");
      // 필요한 경우 스트림 URL 새로고침
      
      broadcast.stream_url = res.data.stream_url;
    } else {
      alert("방송 시작 실패: " + res.data.message);
      
    }
  } catch (e) {
    console.error(e);
    alert("서버 오류로 방송 시작에 실패했습니다.");
    console.error("❌ 요청 실패:", e);
    console.error("📛 에러 메시지:", e.message);
    console.error("📛 응답:", e.response);
  }
};

// OBS 설정 이후 실제 송출 시작 요청 (방송 상태를 LIVE로 변경)
const sendToBroadcast = async () => {
  try {
    const res = await axios.post(`/api/broadcast/live`, {
      broadcast_id: broadcast.broadcast_id
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })

    const now = new Date().toISOString()
    updateBroadcastStatus({
      broadcast_id: broadcast.broadcast_id,
      broadcast_status: 'LIVE',
      is_public: 1,
      actual_start_time: formatDateToMySQL(now)
    })

    console.log("✅ 요청 후에 token 확인:", token);

    if (res.data.status === 'success') {
      alert("방송 송출을 시작했습니다!");
      // 필요한 경우 스트림 URL 새로고침
      
      broadcast.stream_url = res.data.stream_url;
    } else {
      alert("방송 시작 실패: " + res.data.message);
      
    }
  } catch (e) {
    console.error(e);
    alert("서버 오류로 방송 시작에 실패했습니다.");
    console.error("❌ 요청 실패:", e);
    console.error("📛 에러 메시지:", e.message);
    console.error("📛 응답:", e.response);
  }
};

// 방송 종료 및 영상 업로드 요청
const exitBroadcast = async () => {
  try {
    const now = new Date().toISOString()

    // 1. 방송 상태 업데이트
    updateBroadcastStatus({
      broadcast_id: broadcast.broadcast_id,
      broadcast_status: 'ENDED',
      is_public: 0,
      actual_end_time: formatDateToMySQL(now)
    });

    // 2. 방송 종료 요청 (예: OBS 녹화 종료와 연동됨)
    const res = await axios.post(`/api/broadcast/ended`, {
      broadcast_id: broadcast.broadcast_id
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    });

    // 3. 업로드 요청도 여기서 같이 실행 (서버 쪽 OBS 연동이 됐을 경우)
    await axios.post(`/api/broadcast/video/upload`, {
      broadcast_id: broadcast.broadcast_id
    }, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    });

    // 4. 성공 처리
    if (res.data.status === 'success') {
      alert("방송 종료 및 다시보기가 등록되었습니다!");
      broadcast.stream_url = res.data.stream_url;
    } else {
      alert("방송 종료 실패: " + res.data.message);
    }

  } catch (e) {
    console.error("❌ 방송 종료 실패:", e);
    alert("서버 오류로 방송 종료에 실패했습니다.");
  }
};

// 방송 상태 업데이트 API 호출
const updateBroadcastStatus = async (payload) => {
  try {
    await axios.put('/api/broadcast/status', payload, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }})
    alert('방송 상태 업데이트 완료!')
  } catch (err) {
    alert('업데이트 실패: ' + err.message)
  }
}

// 방송 페이지 나가기 (팝업 여부에 따라 동작 분기)
const exitPage = () => {
  if (window.opener) {
    window.close(); // 팝업이라면 창 닫기
  } else {
    this.$router.push("/"); // 팝업이 아닌 일반 페이지일 경우 홈으로 이동
  }
}

// 날짜를 MySQL datetime 포맷으로 변환
function formatDateToMySQL(date) {
  const d = new Date(date)
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${min}:${ss}`
}

// 스트림 키 보여주기
const toggleStreamKey = () => {
  showStreamKey.value = !showStreamKey.value
}

// 스트림 키 복사 함수 (HTTPS + fallback 대응)
const copyStreamKey = async () => {
  const text = stream_key.value  // 복사할 텍스트 값

  // 1. 최신 Clipboard API 사용 (HTTPS 또는 localhost 환경에서만 가능)
  if (navigator.clipboard && window.isSecureContext) {
    try {
      await navigator.clipboard.writeText(text)  // 텍스트 복사 시도
      alert('✅ 스트림 키 복사 완료!')
      return  // 성공 시 fallback으로 넘어가지 않음
    } catch (err) {
      console.warn('📋 Clipboard API 복사 실패:', err)
      // 실패 시 fallback으로 넘어감
    }
  }

  // 2. Fallback 방식 (구형 브라우저, HTTP, IP주소 접속 등)
  try {
    const textarea = document.createElement('textarea')  // textarea 엘리먼트 생성
    textarea.value = text  // 복사할 텍스트 삽입
    textarea.style.position = 'fixed'  // 화면 스크롤 방지
    textarea.style.opacity = '0'       // 안 보이게 숨김
    document.body.appendChild(textarea)  // DOM에 삽입

    textarea.select()  // 텍스트 선택

    const successful = document.execCommand('copy')  // execCommand 복사 시도
    document.body.removeChild(textarea)  // 복사 후 textarea 제거

    if (successful) {
      alert('✅ 스트림 키 복사 완료! (Fallback 방식)')
    } else {
      throw new Error('execCommand 실패')
    }
  } catch (err) {
    console.error('❌ 복사 실패:', err)
    alert('❌ 복사 기능을 사용할 수 없는 환경입니다.\n직접 복사해 주세요.')
  }
}

// 페이지 진입 시 방송 ID 세팅 후 데이터 호출
onMounted(() => {
  broadcast.broadcast_id = parseInt(route.params.broadcast_id)
  console.log("route.params.broadcast_id:", route.params.broadcast_id)
  getBroadCasts()
})
</script>

<style scoped src="@/assets/broadcast/broadcastStart.css"></style>