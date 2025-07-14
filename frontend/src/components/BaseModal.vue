<script setup lang="ts">
import {NModal} from 'naive-ui';

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const emit = defineEmits(['cancel', 'submit'])
defineProps<{
  isEdit?: boolean;
}>();
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
        <div class="flex flex-row items-center gap-[4px]">
          <slot name="actionLeft"></slot>
        </div>
        <div class="flex flex-row gap-[12px]">
          <n-button type="tertiary" size="small" @click="emit('cancel')">取消</n-button>
          <n-button type="primary" size="small" @click="emit('submit')">{{ isEdit ? '确认' : '创建' }}</n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>