<script setup lang="ts">
import BaseModal from '/@/components/BaseModal.vue'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
// const currentTask = defineModel<ITaskCenterTaskItem>('currentTask', {default: {}})
</script>

<template>
  <base-modal v-model:show-modal="showModal">
    <template #header>
      <span>测试数据异常文件</span>
    </template>
  </base-modal>
</template>

<style scoped>

</style>