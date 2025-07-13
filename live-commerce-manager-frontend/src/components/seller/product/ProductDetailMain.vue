<template>
  <div v-if="loading" class="loading">로딩중...</div>
  <div v-else-if="error" class="error">{{ error }}</div>
  <div v-else class="main-row">
    <!-- 대표 이미지 -->
    <div class="product-image-area">
      <img :src="getImageUrl(product.image)" alt="대표이미지" />
    </div>

    <!-- 상품 정보 테이블 -->
    <div class="product-meta-table">
      <div class="table-wrapper">
        <table>
          <tbody>
            <tr><th>상품번호</th><td>{{ product.id }}</td></tr>
            <tr><th>상품명</th><td>{{ product.name }}</td></tr>
            <tr>
              <th>카테고리</th>
              <td>
                {{ [product.mainCategoryName, product.midCategoryName, product.subCategoryName].filter(Boolean).join(' > ') }}
              </td>
            </tr>
            <tr>
              <th>상품가격</th>
              <td>{{ product.price != null ? product.price.toLocaleString() + '원' : '-' }}</td>
            </tr>
            <tr>
              <th>판매가격</th>
              <td>
                <template v-if="editTarget === 'salePrice'">
                  <input v-model.number="editValue" type="number" class="input-edit" />
                  <button class="mini-btn" @click="saveEdit('salePrice')">저장</button>
                  <button class="mini-btn" @click="cancelEdit">취소</button>
                </template>
                <template v-else>
                  {{ product.salePrice != null ? product.salePrice.toLocaleString() + '원' : '-' }}
                  <button class="mini-btn" @click="editField('salePrice')">변경</button>
                </template>
              </td>
            </tr>
            <tr>
              <th>재고수량</th>
              <td>
                <template v-if="editTarget === 'stock'">
                  <input v-model.number="editValue" type="number" class="input-edit" />
                  <button class="mini-btn" @click="saveEdit('stock')">저장</button>
                  <button class="mini-btn" @click="cancelEdit">취소</button>
                </template>
                <template v-else>
                  {{ product.stock }}
                  <button class="mini-btn" @click="editField('stock')">변경</button>
                </template>
              </td>
            </tr>
            <tr><th>상품 조회수</th><td>{{ product.views }}</td></tr>
            <tr><th>판매량</th><td>{{ product.sold }}</td></tr>
            <tr><th>후기수</th><td>{{ product.reviews }}</td></tr>
            <tr>
              <th>판매 상태</th>
              <td>
                <template v-if="editTarget === 'status'">
                  <select v-model="editValue" class="input-edit">
                    <option value="ACTIVE">판매중</option>
                    <option value="INACTIVE">판매중지</option>
                    <option value="SOLD_OUT">품절</option>
                  </select>
                  <button class="mini-btn" @click="saveEdit('status')">저장</button>
                  <button class="mini-btn" @click="cancelEdit">취소</button>
                </template>
                <template v-else>
                  {{ statusMap[product.status] || product.status }}
                  <button class="mini-btn" @click="editField('status')">변경</button>
                </template>
              </td>
            </tr>
            <tr><th>등록날짜</th><td>{{ formatDate(product.createdAt) }}</td></tr>
            <tr><th>수정날짜</th><td>{{ formatDate(product.updatedAt) }}</td></tr>
          </tbody>
        </table>

        <div class="bottom-actions">
          <button class="btn edit-btn" @click="goToEdit">수정</button>
          <button class="btn list-btn" @click="goToList">목록</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// --- props 선언: String, Number 모두 허용
const props = defineProps({
  productId: {
    type: [String, Number],
    required: true
  }
});

// --- 항상 숫자로 변환해서 사용
const numericProductId = computed(() => Number(props.productId));

const router = useRouter();

const product = ref(null);
const loading = ref(true);
const error = ref('');
const editTarget = ref(null);
const editValue = ref('');

const statusMap = {
  ACTIVE: '판매중',
  INACTIVE: '판매중지',
  SOLD_OUT: '품절'
};

// --- 헬퍼 ---
function mapProductDtoToViewModel(dto) {
  return {
    id: dto.productId,
    image: dto.mainImage,
    name: dto.name,
    mainCategoryName: dto.mainCategoryName,
    midCategoryName: dto.midCategoryName,
    subCategoryName: dto.subCategoryName,
    category: dto.categoryId,
    price: dto.price,
    salePrice: dto.salePrice,
    stock: dto.stock,
    views: dto.viewCount,
    sold: dto.productSalesCount,
    reviews: dto.productReviewCount,
    status: dto.productStatus,
    createdAt: dto.createdDate,
    updatedAt: dto.updatedDate,
  }
}
function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
  return date.toISOString().slice(0, 10);
}
function goToEdit() {
  router.push({ name: 'ProductEdit', params: { productId: product.value.id } });
}

// --- API ---
async function fetchProduct() {
  loading.value = true;
  error.value = '';
  // productId 유효성 검사
  if (!numericProductId.value || isNaN(numericProductId.value)) {
    error.value = '유효하지 않은 상품 ID입니다.';
    loading.value = false;
    return;
  }
  try {
    const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');


    const res = await axios.get(`/api/products/${numericProductId.value}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    product.value = mapProductDtoToViewModel(res.data);
  } catch (err) {
    error.value = '상품 정보를 불러오지 못했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
}
async function saveEdit(field) {
  try {
    const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');

    await axios.patch(`/api/products/${numericProductId.value}`, 
      { [field]: editValue.value },
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );

    await fetchProduct(); // 수정 후 다시 조회
    editTarget.value = null;
    editValue.value = '';
  } catch (err) {
    alert('수정에 실패했습니다.');
    console.error(err);
  }
}

// --- 이벤트 ---
function getImageUrl(src) {
  if (!src) return '/default-image.png';
  // 이미 http로 시작하면 그대로, 아니면 백엔드 주소 붙이기
  return src.startsWith('http') ? src : `http://localhost:8080${src}`;
}
function editField(field) {
  editTarget.value = field;
  editValue.value = product.value[field];
}
function cancelEdit() {
  editTarget.value = null;
  editValue.value = '';
}
function goToList() {
  router.push({ name: 'ProductList' });
}

// --- 마운트/감시 ---
onMounted(fetchProduct);
// productId가 바뀌면 다시 조회
watch(() => numericProductId.value, fetchProduct);
</script>

<style scoped>
.main-row {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 140px;
  margin: 40px 0 20px 0;
  width: 100%;
}
.product-image-area {
  flex: 0 0 540px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.product-image-area img {
  width: 650px;
  height: 650px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e0e0e0;
  background: #fafbfc;
}
.product-meta-table {
  flex: 1 1 0;
  max-width: 700px;
  margin-left: 40px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}
.table-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.product-meta-table table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1.08rem;
  border: 1.5px solid #c7d1e0;
}
.product-meta-table th,
.product-meta-table td {
  padding: 14px 18px;
  border: 1px solid #c7d1e0;
  background: #fff;
}
.product-meta-table th {
  background: #f7faff;
  width: 120px;
  font-weight: 600;
}
.input-edit {
  width: 100px;
  padding: 4px 8px;
  font-size: 1rem;
  border: 1px solid #c7d1e0;
  border-radius: 5px;
}
.bottom-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 16px;
}
.btn {
  padding: 8px 22px;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.18s;
}
.list-btn {
  background: #eceff7;
  color: #333;
}
.list-btn:hover { background: #dde2ee; }
.edit-btn {
  background: #3e7eff;
  color: #fff;
}
.edit-btn:hover { background: #295fc5; }
.mini-btn {
  margin-left: 8px;
  padding: 2px 10px;
  font-size: 0.95rem;
  border-radius: 5px;
  border: 1px solid #d0d6e1;
  background: #f7faff;
  cursor: pointer;
  transition: background 0.15s;
}
.mini-btn:hover { background: #e4eafd; }
.loading, .error {
  text-align: center;
  font-size: 1.2rem;
  margin-top: 80px;
}
.loading { color: #3e7eff; }
.error { color: #e74c3c; }
</style>
