<template>
  <video ref="videoRef" controls autoplay muted width="640" height="360"></video>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Hls from 'hls.js'

const videoRef = ref(null)

onMounted(() => {
  if (Hls.isSupported()) {
    const hls = new Hls({
      liveSyncDuration: 1,
      liveMaxLatencyDuration: 2,
      enableWorker: true,
      lowLatencyMode: true
    })
    hls.loadSource('http://localhost:8090/live/test_720p2628kbs/index.m3u8')
    hls.attachMedia(videoRef.value)
  } else if (videoRef.value.canPlayType('application/vnd.apple.mpegurl')) {
    videoRef.value.src = 'http://localhost:8090/live/test_720p2628kbs/index.m3u8'
  }
})
</script>