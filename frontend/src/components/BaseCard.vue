<script setup lang="ts">
const show = defineModel<boolean>('show', {type: Boolean, default: false})
</script>

<template>
  <n-spin :show="show" :size="28">
    <n-card embedded :bordered="false">
      <template #header>
        <slot name="header"></slot>
      </template>
      <template #header-extra>
        <slot name="header-extra"></slot>
      </template>
      <slot></slot>
      <template #footer>
        <slot name="footer"></slot>
      </template>
      <template #action>
        <slot name="action"></slot>
      </template>
    </n-card>
    <template #description>
      你不知道你有多幸运
    </template>
  </n-spin>
</template>

<style scoped>

</style>