<script setup lang="ts">
import BaseViewer from '/@/components/BaseViewer.vue'
import BaseProvider from '/@/components/BaseProvider.vue'
</script>

<template>
  <base-provider>
    <base-viewer/>
  </base-provider>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
