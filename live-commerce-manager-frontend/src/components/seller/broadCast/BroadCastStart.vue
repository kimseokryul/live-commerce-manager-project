<template>
  <div class="broadcast-page">
    <!-- 전체를 좌우 섹션으로 나눔 -->
    <div class="main-content">
      <!-- 왼쪽: 방송 영상, 정보, 시작/중지/일시정지 버튼 -->
      <div class="left-section">
        <div class="video-section">
          <!-- <video id="broadcastVideo" autoplay muted controls></video> -->
          <video ref="videoRef" controls autoplay muted ></video>
        </div>

        <div class="info-section">
          <h2>{{ broadcast.title }}</h2>
          <p>방송자: {{ broadcast.broadcaster_id }}</p>
          <p>{{ broadcast.description }}</p>
        </div>

        <div class="control-buttons">
          <button @click="startBroadcast">방송 시작</button>
          <button @click="stopBroadcast">방송 중지</button>
        </div>

        <div class="form-group">
          <label>OBS 서버 주소 설정 (파일>설정>방송 : 서버 주소에 해당 주소를 붙여넣어 주세요)</label>
          <input type="text" :value="rtmp_url" readonly placeholder="자동 생성 예정" />
        </div>

        <div class="form-group">
          <label>OBS 스트림 키 (파일>설정>방송 : 스트림 키에 해당 주소를 붙여넣어 주세요)</label>
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

      <!-- 오른쪽: 시청자, 상품, 채팅, 송출/종료/나가기 버튼 -->
      <div class="right-section">

        <div class="viewer-info">
          <div class="viewer-count-badge">
            <svg xmlns="http://www.w3.org/2000/svg" class="viewer-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7zm0 12a5 5 0 110-10 5 5 0 010 10z"/>
              <circle cx="12" cy="12" r="2.5" fill="#fff"/>
            </svg>
            <span class="viewer-count">{{ broadcast.current_viewers }}명 시청 중</span>
          </div>
        </div>

        <div class="product-list">
          <div class="product-header" @click="toggleProductList">
            <h3>상품 목록</h3>
            <button class="toggle-button">{{ showProducts ? '접기 ▲' : '펼치기 ▼' }}</button>
          </div>

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

        

        <!-- ✅ 전체 chat-content를 통째로 접었다 폈다 -->
        <div :class="['chat-box', { collapsed: isCollapsed }]" v-if="broadcast.broadcast_id">
          <SellerChat :broadcastId="String(broadcast.broadcast_id)" />
        </div>

        <!-- 오른쪽 버튼 -->
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
import { onMounted, reactive, ref } from 'vue'
import OBSWebSocket from 'obs-websocket-js'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Hls from 'hls.js'
import SellerChat from '@/components/chat/SellerChat.vue';

// const obs = new OBSWebSocket()

const broadcast = reactive({
  broadcast_id: '',
  broadcaster_id: '',
  title: '',
  description: '',
  current_viewers: '',
  like_count: '',
  scheduled_start_time: '',
	scheduled_end_time: '',
  productList: [],
  viewerList: [],
})
const isCollapsed = ref(false);
const stream_key = ref('')
const rtmp_url = ref('')
const stream_url = ref('')

const showProducts = ref(true)
const showStreamKey = ref(false)
const streamKeyInput = ref(null)


const router = useRouter()
const route = useRoute()

const token = ref(localStorage.getItem('jwt') || sessionStorage.getItem('jwt'))

const videoRef = ref(null)

const toggleProductList = () => {
  showProducts.value = !showProducts.value
}

const getBroadCasts = async () => {
  if (!broadcast.broadcast_id) {
    console.warn("⛔ broadcast_id가 없습니다:", broadcast.broadcast_id)
    return
  }
  try{
    const response = await axios.get(`/api/broadcast/${broadcast.broadcast_id}`)
    
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

const updateBroadcastStatus = async (payload) => {
  try {
    await axios.put('/api/broadcast/status', payload)
    alert('방송 상태 업데이트 완료!')
  } catch (err) {
    alert('업데이트 실패: ' + err.message)
  }
}

const exitPage = () => {
  if (window.opener) {
    window.close(); // 팝업이라면 창 닫기
  } else {
    this.$router.push("/"); // 팝업이 아닌 일반 페이지일 경우 홈으로 이동
  }
}

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

// 스트림 키 복사
const copyStreamKey = async () => {
  try {
    await navigator.clipboard.writeText(stream_key.value)
    alert('스트림 키 복사 완료!')
  } catch (err) {
    alert('복사 실패')
  }
}


onMounted(() => {
  broadcast.broadcast_id = parseInt(route.params.broadcast_id)
  console.log("route.params.broadcast_id:", route.params.broadcast_id)
  getBroadCasts()
})
</script>

<style scoped>
.broadcast-page {
  padding: 24px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  background-color: #f5f6fa;
  min-height: 100vh;
}

.main-content {
  display: flex;
  gap: 24px;
}

/* 좌측: 방송 영상, 정보, 제어 버튼 */
.left-section {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-section video {
  width: 100%;
  height: 550px;
  background: #000;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.info-section {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.info-section h2 {
  margin-bottom: 10px;
  font-size: 20px;
  color: #2c3e50;
}

.info-section p {
  margin-bottom: 6px;
  color: #555;
  font-size: 14px;
}

.control-buttons {
  display: flex;
  gap: 12px;
}

.control-buttons button {
  flex: 1;
  padding: 12px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.control-buttons button:hover {
  background-color: #2980b9;
}

/* 우측: 시청자, 상품, 채팅, 제어 */
.right-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  justify-content: flex-start;
}

.viewer-info {
  padding: 16px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.viewer-count-badge {
  display: flex;
  align-items: center;
  background-color: #ecf0f1;
  padding: 8px 14px;
  border-radius: 30px;
  font-weight: bold;
  font-size: 14px;
  color: #2c3e50;
  box-shadow: inset 0 0 4px rgba(0,0,0,0.05);
}

.viewer-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  color: #2980b9;
}

/*.viewer-info,*/
/* .product-list, */
/* .chat-box {
  background-color: #ffffff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  color: #333;
} */

.product-list {
  background-color: #ffffff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.toggle-button {
  background: none;
  border: none;
  color: #3498db;
  font-size: 14px;
  cursor: pointer;
  font-weight: bold;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.product-table th,
.product-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}

.product-table th {
  background-color: #f0f3f5;
  color: #2c3e50;
}

.chat-box {
  transition: height 0.3s ease;
  overflow: hidden;
}

/* 접힌 상태에서 부모에서 class 내려주기 (예: collapsed 상태 class) */
.chat-box.collapsed {
  height: 0;
  padding: 0;
  border: none;
}

.toggle-button {
  background: none;
  border: none;
  color: #3498db;
  font-size: 14px;
  cursor: pointer;
  font-weight: bold;
}

.chat-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 방송 제어 버튼 */
.right-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.right-buttons .horizontal-buttons {
  display: flex;
  gap: 10px;
}

.right-buttons button {
  flex: 1;
  padding: 12px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

/* 송출/종료 버튼 */
.right-buttons .horizontal-buttons button:first-child {
  background-color: #f39c12;
  color: white;
}

.right-buttons .horizontal-buttons button:first-child:hover {
  background-color: #d68910;
}

.right-buttons .horizontal-buttons button:last-child {
  background-color: #e74c3c;
  color: white;
}

.right-buttons .horizontal-buttons button:last-child:hover {
  background-color: #c0392b;
}

/* 나가기 버튼 */
.exit-btn {
  background-color: #95a5a6;
  color: white;
}

.exit-btn:hover {
  background-color: #7f8c8d;
}

/* 공통 폼 그룹 */
.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #374151;
  font-size: 15px;
}

/* 수평 정렬 그룹 */
.form-group.horizontal {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>