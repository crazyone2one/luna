<script setup lang="ts">

import BaseHeader from '/@/layout/components/BaseHeader.vue'
import BaseSidebar from '/@/layout/components/BaseSidebar.vue'
</script>

<template>
  <n-layout embedded has-sider style="height: 100vh">
    <base-sidebar/>
    <n-layout>
      <base-header/>
      <n-layout-content content-style="padding: 20px 20px; min-height: 85vh" :native-scrollbar="false">
        <router-view/>
      </n-layout-content>
      <n-layout-footer>
        <n-p>
          <n-text type="error">♥</n-text>
          <n-a href="https://github.com/crazyone2one/luna">luna</n-a>
          with
          <n-a href="https://github.com/crazyone2one/luna/blob/main/LICENSE">MIT</n-a>
          License.
          &copy; {{ new Date().getFullYear() }} Luna’s. All rights reserved.
        </n-p>
      </n-layout-footer>
    </n-layout>
  </n-layout>
</template>

<style scoped>
.n-layout-footer {
  opacity: 0.6;
  text-align: center;
}

.n-a {
  text-decoration: none;
}
</style>