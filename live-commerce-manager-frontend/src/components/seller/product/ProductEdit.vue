<template>
  <div class="product-registration">
    <h2>상품 수정</h2>
    <form @submit.prevent="submitForm">
      <!-- 1. 기본 정보 -->
      <AccordionSection title="기본 정보" :open="openBasic" @toggle="openBasic = !openBasic">
        <div class="form-group category-row">
          <label class="category-label">카테고리 <span class="required">*</span></label>
          <div class="category-selects">
            <select v-model.number="selectedLargeCategoryId" required>
              <option disabled value="">대분류 선택</option>
              <option v-for="cat in largeCategories" :key="cat.categoryId" :value="cat.categoryId">
                {{ cat.categoryName }}
              </option>
            </select>
            <select v-model.number="selectedSmallCategoryId" :disabled="!Number(selectedLargeCategoryId)">
              <option disabled value="">
                {{ !selectedLargeCategoryId ? '먼저 대분류를 선택하세요' : '소분류 선택' }}
              </option>
              <option v-for="cat in smallCategories" :key="cat.categoryId" :value="cat.categoryId">
                {{ cat.categoryName }}
              </option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label>상품명 <span class="required">*</span></label>
          <input type="text" v-model="form.name" required maxlength="100" />
          <div class="form-hint">상품명을 정확하게 입력해 주세요. (최대 100자)</div>
        </div>
      </AccordionSection>

      <!-- 2. 판매 정보 -->
      <AccordionSection title="판매 정보" :open="openSale" @toggle="openSale = !openSale">
        <div class="form-group">
          <label>상품 가격 <span class="required">*</span></label>
          <input type="number" v-model.number="form.price" min="0" required />
          <div class="form-hint">소비자에게 노출되는 기본 가격입니다.</div>
        </div>
        <div class="form-group">
          <label>상품 판매가 <span class="required">*</span></label>
          <input type="number" v-model.number="form.salePrice" min="0" required />
          <div class="form-hint">할인 등 실제 판매가를 입력하세요.</div>
        </div>
        <div class="form-group">
          <label>상품 재고 <span class="required">*</span></label>
          <input type="number" v-model.number="form.stock" min="0" required />
          <div class="form-hint">전체 재고 수량을 입력하세요.</div>
        </div>
        <div class="form-group">
          <label>상품 상태 <span class="required">*</span></label>
          <select v-model="form.status" required>
            <option value="판매중">판매중</option>
            <option value="품절">품절</option>
            <option value="판매중지">판매중지</option>
          </select>
        </div>
      </AccordionSection>

      <!-- 3. 상품 옵션 -->
      <AccordionSection title="상품 옵션 (선택)" :open="openOption" @toggle="openOption = !openOption">
        <div class="form-hint option-hint">
          옵션은 등록하지 않아도 됩니다. 옵션이 있을 경우만 입력하세요.<br />
          옵션별 판매가는 <b>상품 판매가</b> 기준 "0" 또는 "+, - 금액"으로 입력하세요.<br />
          예) 상품 판매가 9900원, 옵션가 0 → 9900원<br />
          옵션가 +1000 → 10,900원, 옵션가 -500 → 9,400원
        </div>
        <div v-for="(option, idx) in form.options" :key="idx" class="option-row">
          <div class="form-group option-group">
            <label>옵션명 <span class="required">*</span></label>
            <input type="text" v-model="option.name" required maxlength="50" />
          </div>
          <div class="form-group option-group">
            <label>판매가 차액 <span class="required">*</span></label>
            <input type="number" v-model.number="option.salePriceDiff" required />
          </div>
          <div class="form-group option-group">
            <label>재고 <span class="required">*</span></label>
            <input type="number" v-model.number="option.stock" min="0" required />
          </div>
          <div class="form-group option-group">
            <label>상태 <span class="required">*</span></label>
            <select v-model="option.status" required>
              <option value="판매중">판매중</option>
              <option value="품절">품절</option>
              <option value="판매중지">판매중지</option>
            </select>
          </div>
          <button type="button" class="btn-option-del" @click="removeOption(idx)" :disabled="isSubmitting">삭제</button>
        </div>
        <button type="button" class="btn-option-add" @click="addOption" :disabled="isSubmitting">+ 옵션 추가</button>
        <div v-if="form.options.length === 0" class="form-hint" style="color:#2563eb;">
          옵션을 등록하지 않아도 상품 수정이 가능합니다.
        </div>
      </AccordionSection>

      <!-- 4. 이미지 및 상세 설명 -->
      <AccordionSection title="이미지 및 상세 설명" :open="openImage" @toggle="openImage = !openImage">
        <div class="form-group">
          <label>대표 이미지 <span class="required">*</span></label>
          <input type="file" @change="onMainImageChange" accept="image/*" :disabled="isSubmitting" />
          <div v-if="form.mainImageUrl || form.mainImage" class="image-preview-wrapper">
            <img :src="form.mainImageUrl || getImageUrl(form.mainImage)" alt="대표 이미지 미리보기" class="image-preview" />
            <button type="button" class="btn-image-remove" @click="removeMainImage">이미지 삭제</button>
          </div>
          <div class="form-hint">
            최대 5MB, jpg/png/gif만 등록 가능. (권장: 정사각형, 800x800px 이상)
          </div>
          <div class="form-warning" v-if="imageError">{{ imageError }}</div>
        </div>
        <div class="form-group">
          <label>상세 설명 <span class="required">*</span></label>
          <ToastEditor ref="toastEditorRef" v-model="form.description" :disabled="isSubmitting" />
          <div class="form-hint">상품의 특징, 구성, 사용법 등을 상세히 입력해 주세요.</div>
        </div>
        <div class="form-group">
          <label>간단 설명 <span class="required">*</span></label>
          <input type="text" v-model="form.shortDescription" required maxlength="200" />
          <div class="form-hint">상품의 주요 특징을 한 줄로 요약해 주세요. (최대 200자)</div>
        </div>
        <div class="form-warning" style="margin-top:14px;">
          ※ 상품 필수정보(원산지, 유통기한, 보관방법 등)는 상세설명에 반드시 기재해야 합니다.
        </div>
      </AccordionSection>

      <div class="form-warning form-bottom-warning" v-if="formError">
        {{ formError }}
      </div>
      <div class="form-success" v-if="formSuccess">
        {{ formSuccess }}
      </div>
      <div class="spinner-wrapper" v-if="isSubmitting">
        <div class="spinner"></div>
      </div>
      <button type="submit" class="btn submit-btn" :disabled="isSubmitting">
        <span v-if="isSubmitting" class="spinner-btn"></span>
        수정하기
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToastEditor from '../../ToastEditor.vue'
import AccordionSection from '@/components/common/AccordionSection.vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const productId = Number(route.params.productId)

const openBasic = ref(false)
const openSale = ref(false)
const openOption = ref(false)
const openImage = ref(false)

const isSubmitting = ref(false)
const formError = ref('')
const formSuccess = ref('')
const imageError = ref('')
const MAX_IMAGE_SIZE = 5 * 1024 * 1024

const categories = ref([])
const selectedLargeCategoryId = ref('')
const selectedSmallCategoryId = ref('')

const form = reactive({
  name: '',
  shortDescription: '',
  mainImageFile: null,
  mainImageUrl: '',
  mainImage: '',
  price: 0,
  salePrice: 0,
  stock: 0,
  status: '판매중',
  description: '',
  options: []
})

const toastEditorRef = ref(null)

function getImageUrl(src) {
  if (!src) return '/default-image.png'
  return src.startsWith('http') ? src : `http://localhost:8080${src}`
}

onMounted(async () => {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')

  try {
    const res = await axios.get('/api/categories/tree', {
      headers: { Authorization: `Bearer ${token}` }
    })
    categories.value = res.data
    // ✅ 카테고리 받아온 뒤 fetchProduct 실행
    await fetchProduct()
    
  } catch (e) {
    formError.value = '카테고리 정보를 불러오지 못했습니다.'
  }
})

async function fetchProduct() {
    formError.value = ''
  try {
    const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')
    const { data } = await axios.get(`/api/products/${productId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    const category = resolveCategoryPath(data.categoryId)
    console.log('🔍 categoryId:', data.categoryId)
console.log('🔍 resolved path:', category)
    selectedLargeCategoryId.value = category.largeCategoryId || ''
    selectedSmallCategoryId.value = category.smallCategoryId || ''

    form.name = data.name
    form.shortDescription = data.productShortDescription
    form.mainImage = data.mainImage
    form.mainImageFile = null
    form.mainImageUrl = ''
    form.price = data.price
    form.salePrice = data.salePrice
    form.stock = data.stock
    form.status = data.productStatus
    form.description = data.productDescription
    form.options = Array.isArray(data.options)
      ? data.options.map(opt => ({
          name: opt.optionName,
          salePriceDiff: Number(opt.salePrice) - Number(data.salePrice),
          stock: opt.stock,
          status: opt.status
        }))
      : []
  } catch (e) {
    formError.value = '상품 정보를 불러오지 못했습니다.'
  }
}

function resolveCategoryPath(categoryId) {
  const result = { largeCategoryId: '', smallCategoryId: '' }
  if (!categories.value || !Array.isArray(categories.value)) return result

  for (const large of categories.value) {
    for (const small of large.children || []) {
      if (String(small.categoryId) === String(categoryId)) {
        result.largeCategoryId = String(large.categoryId)
        result.smallCategoryId = String(small.categoryId)
        return result
      }
    }
  }
  return result
}

const largeCategories = computed(() => categories.value)
const smallCategories = computed(() => {
  const large = categories.value.find(c => c.categoryId === Number(selectedLargeCategoryId.value))
  return large && large.children ? large.children : []
})

function addOption() {
  form.options.push({ name: '', salePriceDiff: 0, stock: 0, status: '판매중' })
}
function removeOption(idx) {
  form.options.splice(idx, 1)
}

function onMainImageChange(event) {
  imageError.value = ''
  const file = event.target.files && event.target.files[0]
  if (file) {
    if (file.size > MAX_IMAGE_SIZE) {
      imageError.value = '이미지 용량은 5MB 이하만 등록 가능합니다.'
      form.mainImageFile = null
      form.mainImageUrl = ''
      return
    }
    if (!file.type.match(/^image\/(jpeg|png|gif)$/)) {
      imageError.value = 'jpg, png, gif 파일만 등록 가능합니다.'
      form.mainImageFile = null
      form.mainImageUrl = ''
      return
    }
    form.mainImageFile = file
    form.mainImageUrl = URL.createObjectURL(file)
  }
}

function validateForm() {
  if (!selectedLargeCategoryId.value || !selectedSmallCategoryId.value) {
    formError.value = '카테고리를 모두 선택해 주세요.'
    return false
  }
  if (!form.name.trim()) {
    formError.value = '상품명을 입력해 주세요.'
    return false
  }
  if (form.price < 0 || form.salePrice < 0 || form.stock < 0) {
    formError.value = '가격 및 재고는 0 이상으로 입력해 주세요.'
    return false
  }
  if (!form.status) {
    formError.value = '상품 상태를 선택해 주세요.'
    return false
  }
  if (!form.mainImageFile && !form.mainImage) {
    formError.value = '대표 이미지를 등록해 주세요.'
    return false
  }
  if (toastEditorRef.value && toastEditorRef.value.getHtml) {
    form.description = toastEditorRef.value.getHtml()
  }
  const descText = (form.description || '').replace(/<[^>]*>/g, '').trim()
  if (!descText) {
    formError.value = '상세 설명을 입력해 주세요.'
    return false
  }
  if (!form.shortDescription.trim()) {
    formError.value = '간단 설명을 입력해 주세요.'
    return false
  }
  for (const [i, opt] of form.options.entries()) {
    if (!opt.name.trim() || isNaN(opt.salePriceDiff) || opt.stock < 0 || !opt.status) {
      formError.value = `옵션 ${i + 1} 정보를 모두 입력해 주세요.`
      return false
    }
  }
  return true
}

function removeMainImage() {
  form.mainImageFile = null
  form.mainImageUrl = ''
  form.mainImage = ''
  const input = document.querySelector('input[type="file"]')
  if (input) input.value = ''
}

let isUnmounted = false
onUnmounted(() => { isUnmounted = true })

async function submitForm() {
  formError.value = ''
  formSuccess.value = ''
  if (!validateForm()) return

  isSubmitting.value = true

  try {
    const formData = new FormData()

    // 1. JSON 형태의 product 객체 생성
    const product = {
      categoryId: selectedSmallCategoryId.value || selectedLargeCategoryId.value,
      name: form.name,
      price: form.price,
      salePrice: form.salePrice,
      stock: form.stock,
      productStatus: form.status,
      productShortDescription: form.shortDescription,
      productDescription: form.description,
      options: form.options?.map(opt => ({
        optionName: opt.name,
        salePrice: Number(form.salePrice) + Number(opt.salePriceDiff),
        stock: opt.stock,
        status: opt.status
      })) || []
    }

    // 2. product JSON을 Blob으로 묶어서 append
    formData.append(
      'product',
      new Blob([JSON.stringify(product)], { type: 'application/json' })
    )

    // 3. 대표 이미지 있을 경우 추가
    if (form.mainImageFile) {
      formData.append('mainImage', form.mainImageFile)
    }

    // 4. JWT 토큰
    const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')

    // 5. 요청 전송
    await axios.post(`/api/products/${productId}/edit`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })

    // 6. 성공 처리
    if (!isUnmounted) {
      formSuccess.value = '상품이 성공적으로 수정되었습니다!'
      setTimeout(() => {
        if (!isUnmounted && router?.push) {
          formSuccess.value = ''
          router.push('/product')
        }
      }, 1400)
    }
  } catch (e) {
    if (!isUnmounted) {
      formError.value = e.response?.data?.message || '상품 수정에 실패했습니다.'
    }
  } finally {
    if (!isUnmounted) {
      isSubmitting.value = false
    }
  }
}

</script>

<style>
/* paste.txt의 스타일을 그대로 복사해서 사용하세요 */
</style>
