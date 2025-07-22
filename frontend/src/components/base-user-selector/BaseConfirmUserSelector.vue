<script setup lang="ts">

import BaseIcon from '/src/components/BaseIcon.vue'
import {ref} from 'vue'
import BaseUserSelector from '/@/components/base-user-selector/index.vue'

const props = withDefaults(
    defineProps<{
      loadOptionParams: Record<string, any>; // 加载选项的参数
      okLoading?: boolean;
    }>(), {okLoading: false,}
);
const emit = defineEmits<{
  (e: 'confirm', value: string[], callback: (v: boolean) => void): void;
}>();
const switchStatus = ref(false);
const memberList = ref<string[]>([]);
const showUserSelector = () => {
  switchStatus.value = true
}
const handleCancel = () => {
  switchStatus.value = false;
  memberList.value = [];
}
const handleConfirm = () => {
  emit('confirm', memberList.value, (v) => {
    switchStatus.value = !v;
    if (v) {
      memberList.value = [];
    }
  });
};
</script>

<template>
  <div v-if="!switchStatus" class="flex cursor-pointer flex-row gap-[8px] text-green"
       @click="showUserSelector">
    <div>
      <base-icon type="add" class="mr-[4px]"/>
    </div>
    <div>快速添加成员</div>
  </div>
  <div v-else>
    <div class="flex flex-row items-center">
      <base-user-selector v-bind="$attrs" :load-option-params="props.loadOptionParams"
                          v-model:model-value="memberList"
                          class="!w-[260px]"/>
      <n-button size="tiny" :disabled="!memberList.length" class="ml-[12px]"
                :loading="props.okLoading"
                @click="handleConfirm">确认</n-button>
      <n-button size="tiny" class="ml-[12px] !border-[var(--color-text-input-border)] !text-[var(--color-text-1)]"
                @click="handleCancel">
        取消
      </n-button>
    </div>
  </div>
</template>

<style scoped>

</style>