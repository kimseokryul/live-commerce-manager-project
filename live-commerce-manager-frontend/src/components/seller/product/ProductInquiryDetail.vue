<template>
  <div class="inquiry-detail">
    <!-- 제목 -->
    <div class="inquiry-detail-title-above">[{{ inquiry.productName }}] 문의 상세보기</div>
    <!-- 목록 버튼 -->
    <div class="inquiry-detail-header">
      <button class="btn-main list-btn-top" @click="goToInquiryList">
        <span class="icon-arrow">&#8592;</span> 목록
      </button>
    </div>

    <!-- 문의 정보 -->
    <div class="inquiry-info-row">
      <span class="inquiry-id">{{ inquiry.qnaId }}</span>
      <span class="inquiry-title">{{ inquiry.title }}</span>
      <span class="inquiry-author">{{ inquiry.userId }}</span>
      <span class="inquiry-date">{{ formatDate(inquiry.createdDate) }}</span>
    </div>

    <!-- 문의 내용 -->
    <div class="inquiry-content-card">
      <div class="inquiry-content-title">문의 내용</div>
      <div class="inquiry-content-text">{{ inquiry.content }}</div>
    </div>

    <!-- 답변 영역 -->
    <div class="inquiry-reply-card">
      <div class="reply-title">답변</div>
      <template v-if="answers.length > 0">
        <div
          class="reply-item"
          v-for="answer in answers"
          :key="answer.answerId"
        >
          <div class="reply-meta">
            <span class="reply-author">{{ answer.userId }}</span>
            <span class="reply-date">{{ formatDate(answer.createdDate) }}</span>
          </div>
          <div class="reply-content">{{ answer.content }}</div>
          <div class="reply-actions">
            <button class="btn-main btn-edit" @click="startEdit(answer)">수정</button>
            <button class="btn-main btn-delete" @click="deleteAnswer(answer.answerId)">삭제</button>
          </div>
          <div v-if="editAnswerId === answer.answerId" class="reply-form">
            <textarea
              v-model="editAnswerText"
              rows="4"
              class="reply-textarea"
              :maxlength="maxLength"
            ></textarea>
            <div class="reply-footer">
              <span class="char-count">{{ editAnswerText.length }} / {{ maxLength }}</span>
              <button class="btn-main" @click="saveEditAnswer(answer.answerId)">저장</button>
              <button class="btn-main btn-cancel" @click="cancelEdit">취소</button>
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="no-reply">아직 등록된 답변이 없습니다.</div>
      </template>

      <!-- 답변 등록 폼 (항상 노출) -->
      <div class="reply-form">
        <textarea
          v-model="newAnswerText"
          placeholder="답변을 입력하세요 (최소 5자)"
          rows="4"
          class="reply-textarea"
          :maxlength="maxLength"
          :disabled="loading"
        ></textarea>
        <div class="reply-footer">
          <span class="char-count">{{ newAnswerText.length }} / {{ maxLength }}</span>
          <button class="btn-main" :disabled="!canSubmit || loading" @click="submitAnswer">
            <span v-if="loading" class="spinner"></span>
            답변 등록
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

// URL 파라미터
const productId = Number(route.params.productId);
const qnaId = route.params.qnaId;

// 데이터 상태
const inquiry = ref({
  qnaId: '',
  productId: '',
  productName: '',
  userId: '',
  title: '',
  content: '',
  createdDate: '',
});
const answers = ref([]);

const newAnswerText = ref('');
const editAnswerId = ref(null);
const editAnswerText = ref('');
const loading = ref(false);

const maxLength = 300;
const minLength = 5;

// JWT 토큰 가져오기
function getAuthHeader() {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');
  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

const canSubmit = computed(() =>
  newAnswerText.value.trim().length >= minLength &&
  newAnswerText.value.length <= maxLength
);

function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
  return date.toISOString().slice(0, 10);
}

// 문의 상세 + 답변 리스트
async function fetchInquiryDetail() {
  try {
    const { data } = await axios.get(
      `/api/products/${productId}/inquiries/${qnaId}`,
      getAuthHeader()
    );

    inquiry.value = {
      qnaId: data.qnaId ?? data.QNA_ID,
      productId: data.productId ?? data.PRODUCT_ID,
      productName: data.productName ?? data.PRODUCT_NAME,
      userId: data.userId ?? data.USER_ID,
      title: data.title ?? data.TITLE,
      content: data.content ?? data.CONTENT,
      createdDate: data.createdDate ?? data.CREATED_DATE,
    };

    answers.value = (data.answers ?? []).map(a => ({
      answerId: a.answerId ?? a.ANSWER_ID,
      qnaId: a.qnaId ?? a.QNA_ID,
      userId: a.userId ?? a.USER_ID,
      content: a.content ?? a.CONTENT,
      createdDate: a.createdDate ?? a.CREATED_DATE,
    }));
  } catch (e) {
    alert('문의 상세 정보를 불러오지 못했습니다.');
  }
}

// 답변 등록
async function submitAnswer() {
  if (!canSubmit.value) return;
  loading.value = true;
  try {
    await axios.post(
      `/api/products/${productId}/inquiries/${qnaId}/answers`,
      { content: newAnswerText.value },
      getAuthHeader()
    );
    newAnswerText.value = '';
    await fetchInquiryDetail();
  } catch (e) {
    alert('답변 등록에 실패했습니다.');
  }
  loading.value = false;
}

// 답변 수정
function startEdit(answer) {
  editAnswerId.value = answer.answerId;
  editAnswerText.value = answer.content;
}

async function saveEditAnswer(answerId) {
  if (editAnswerText.value.trim().length < minLength) return;
  try {
    await axios.put(
      `/api/products/${productId}/inquiries/${qnaId}/answers/${answerId}`,
      { content: editAnswerText.value },
      getAuthHeader()
    );
    editAnswerId.value = null;
    editAnswerText.value = '';
    await fetchInquiryDetail();
  } catch (e) {
    alert('답변 수정에 실패했습니다.');
  }
}

// 답변 수정 취소
function cancelEdit() {
  editAnswerId.value = null;
  editAnswerText.value = '';
}

// 답변 삭제
async function deleteAnswer(answerId) {
  if (!confirm('답변을 삭제하시겠습니까?')) return;
  try {
    await axios.delete(
      `/api/products/${productId}/inquiries/${qnaId}/answers/${answerId}`,
      getAuthHeader()
    );
    await fetchInquiryDetail();
  } catch (e) {
    alert('답변 삭제에 실패했습니다.');
  }
}

function goToInquiryList() {
  router.push({ name: 'ProductInquiryList', params: { productId } });
}

onMounted(fetchInquiryDetail);
</script>


<style scoped>
.inquiry-detail {
  max-width: 100%;
  margin: 0;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 32px 16px 24px 16px;
  position: relative;
}
.inquiry-detail-title-above {
  text-align: center;
  font-size: 1.35rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 6px;
}
.inquiry-detail-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 10px;
  min-height: 48px;
}
.list-btn-top {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 7px;
  font-size: 1.09rem;
  font-weight: 700;
  padding: 0.54rem 1.4rem;
  cursor: pointer;
  transition: background 0.2s;
}
.list-btn-top:hover {
  background: #1746a2;
}
.icon-arrow {
  font-size: 1.15rem;
  margin-right: 2px;
}
.inquiry-info-row {
  display: flex;
  gap: 48px;
  align-items: center;
  font-size: 1.2rem;
  font-weight: 600;
  color: #222;
  margin-bottom: 28px;
  padding: 18px 10px 10px 18px;
  background: #f8fafc;
  border-radius: 8px;
}
.inquiry-id::before {
  content: "문의번호 : #";
  color: #2563eb;
  margin-right: 4px;
}
.inquiry-title::before {
  content: "문의제목 : ";
  color: #222;
  margin-right: 4px;
}
.inquiry-author::before {
  content: "작성자 : ";
  color: #555;
  margin-right: 4px;
}
.inquiry-date::before {
  content: "등록일자 : ";
  color: #888;
  margin-right: 4px;
}
.inquiry-content-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px 16px 18px 16px;
  margin-bottom: 28px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.inquiry-content-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2563eb;
  margin-bottom: 8px;
}
.inquiry-content-text {
  font-size: 1.2rem;
  color: #222;
  line-height: 1.7;
}
.inquiry-reply-card {
  background: #f7faff;
  border-radius: 8px;
  padding: 18px 16px 14px 16px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(37,99,235,0.05);
  border-left: 5px solid #2563eb;
  position: relative;
}
.reply-title {
  font-size: 1.04rem;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 10px;
}
.reply-item {
  margin-bottom: 18px;
  background: #e6f0ff;
  border-radius: 6px;
  padding: 10px 12px 8px 12px;
  box-shadow: 0 1px 4px rgba(37,99,235,0.04);
}
.reply-meta {
  font-size: 0.97rem;
  color: #888;
  margin-bottom: 6px;
  display: flex;
  gap: 16px;
}
.reply-content {
  font-size: 1.13rem;
  color: #1746a2;
  margin-bottom: 10px;
  word-break: break-all;
}
.reply-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 4px;
}
.no-reply {
  font-size: 1.08rem;
  color: #888;
  margin-bottom: 10px;
}
.reply-form {
  margin-top: 0;
}
.reply-textarea {
  width: 100%;
  border: 1px solid #c7d1e0;
  border-radius: 7px;
  font-size: 1.07rem;
  padding: 10px;
  margin-bottom: 10px;
  resize: vertical;
}
.reply-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.char-count {
  font-size: 0.97rem;
  color: #777;
}
.btn-main {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 7px;
  font-size: 1.09rem;
  font-weight: 700;
  padding: 0.54rem 1.4rem;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 8px;
}
.btn-main:disabled {
  background: #b6c6e0;
  cursor: not-allowed;
}
.btn-main:hover:not(:disabled) {
  background: #1746a2;
}
.btn-edit {
  margin-left: 0;
  background: #fff;
  color: #2563eb;
  border: 1px solid #2563eb;
}
.btn-edit:hover {
  background: #e6f0ff;
}
.btn-delete {
  background: #fff;
  color: #e53e3e;
  border: 1px solid #e53e3e;
  margin-left: 10px;
}
.btn-delete:hover {
  background: #ffeaea;
}
.btn-cancel {
  background: #fff;
  color: #888;
  border: 1px solid #bbb;
  margin-left: 10px;
}
.spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid #fff;
  border-radius: 50%;
  border-top: 2px solid #2563eb;
  animation: spin 0.8s linear infinite;
  margin-right: 6px;
  vertical-align: middle;
}
@keyframes spin {
  0% { transform: rotate(0deg);}
  100% { transform: rotate(360deg);}
}
@media (max-width: 640px) {
  .inquiry-detail { padding: 10px 2vw; }
  .inquiry-info-row { gap: 16px; font-size: 1rem; }
}
</style>
