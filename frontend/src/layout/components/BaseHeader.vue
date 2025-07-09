<script setup lang="ts">

import BaseIcon from '/@/components/BaseIcon.vue'
</script>

<template>
  <n-layout-header bordered>
    <n-button text >
      <base-icon type="refresh" size="20" :depth="2"/>
    </n-button>
    <n-space :size="20" align="center" style="line-height: 1">
      <n-tooltip>
        <template #trigger>
          <n-a href="https://github.com/crazyone2one/luna" target="_blank">
            <base-icon type="github" size="22" :depth="2" />
          </n-a>
        </template>
        View on GitHub
      </n-tooltip>
    </n-space>
  </n-layout-header>
</template>

<style scoped>
.n-layout-header {
  position: sticky;
  top: 0;
  z-index: 1;
  display: flex;
  align-items: center;
  padding: 9px 18px;
}

.n-button {
  margin-right: 15px;
}

.n-space {
  margin-left: auto;
}
</style>