import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const participantCount = ref(0)

  const setParticipantCount = (count) => {
    participantCount.value = count
  }

  return {
    participantCount,
    setParticipantCount
  }
})
