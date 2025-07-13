<template>
  <div ref="editorRoot"></div>
</template>

<script setup>
import '@toast-ui/editor/dist/toastui-editor.css'
import { ref, onMounted, watch, defineProps, defineEmits, defineExpose, onBeforeUnmount } from 'vue'
import Editor from '@toast-ui/editor'

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue'])

const editorRoot = ref(null)
let editorInstance = null

function emitUpdate() {
  if (editorInstance && typeof editorInstance.getHtml === 'function') {
    emit('update:modelValue', editorInstance.getHtml())
  }
}

// 에디터 인스턴스 해제(메모리 누수 방지)
onBeforeUnmount(() => {
  if (editorInstance && typeof editorInstance.destroy === 'function') {
    editorInstance.destroy()
    editorInstance = null
  }
})

onMounted(() => {
  try {
    editorInstance = new Editor({
      el: editorRoot.value,
      height: '500px',
      initialEditType: 'wysiwyg',
      previewStyle: 'vertical',
      initialValue: props.modelValue || '',
      toolbarItems: [
        'heading', 'bold', 'italic', 'strike',
        'hr', 'quote',
        'ul', 'ol', 'task', 'indent', 'outdent',
        'table', 'image', 'link',
        'code', 'codeblock'
      ]
    })
    if (props.disabled) {
      if (typeof editorInstance.disable === 'function') {
        editorInstance.disable()
      }
    }
    editorInstance.on('change', emitUpdate)
  } catch (e) {
    console.error('ToastEditor 에디터 생성 중 오류:', e)
  }
})

watch(() => props.modelValue, (v) => {
  try {
    if (editorInstance && typeof editorInstance.getHtml === 'function' && typeof editorInstance.setHtml === 'function') {
      if (v !== editorInstance.getHtml()) {
        editorInstance.setHtml(v || '')
      }
    }
  } catch (e) {
    console.error('ToastEditor modelValue watcher 오류:', e)
  }
})

watch(() => props.disabled, (v) => {
  try {
    if (editorInstance) {
      if (v && typeof editorInstance.disable === 'function') {
        editorInstance.disable()
      } else if (!v && typeof editorInstance.enable === 'function') {
        editorInstance.enable()
      }
    }
  } catch (e) {
    console.error('ToastEditor disabled watcher 오류:', e)
  }
})

defineExpose({
  getHtml() {
    return editorInstance && typeof editorInstance.getHtml === 'function'
      ? editorInstance.getHtml()
      : ''
  }
})
</script>


