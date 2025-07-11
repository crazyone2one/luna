<script setup lang="ts">
import {NModal} from 'naive-ui';

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const emit = defineEmits(['cancel', 'submit'])
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>
        <slot name="header"></slot>
      </div>
    </template>
    <div>
      <slot></slot>
    </div>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row gap-[12px]">
          <n-button type="tertiary" size="small" @click="emit('cancel')">算了</n-button>
          <n-button type="primary" size="small" @click="emit('submit')">确认</n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>