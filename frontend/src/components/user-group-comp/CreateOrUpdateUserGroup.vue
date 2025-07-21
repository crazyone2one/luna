<script setup lang="ts">
import {inject, reactive, ref, watchEffect} from 'vue'
import type {UserGroupItem} from '/@/types/user-group.ts'

const systemType = inject<String>('systemType');
const props = defineProps<{
  id?: string;
  list: UserGroupItem[];
  visible?: boolean;
  defaultName?: string;
  // 权限范围
  authScope?: string;
}>();
const currentVisible = ref(props.visible);
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void;
  (e: 'submit', currentId: string): void;
}>();
const form = reactive({
  name: '',
});
const handleCancel = () => {
  form.name = '';
  emit('cancel', false);
};
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel()
  }
}
watchEffect(() => {
  currentVisible.value = props.visible;
  form.name = props.defaultName || '';
});
</script>

<template>
  <n-popover
      :show="currentVisible"
      placement="bottom"
      trigger="click"
      class="w-[350px]"
      :content-class="props.id ? 'move-left' : ''"
      @clickoutside="handleOutsideClick"
  >
    <template #trigger>
      <slot></slot>
    </template>
    <div>
      <n-form>
        <div class="mb-[8px] text-[14px] font-medium text-[var(--color-text-1)]">
          {{ props.id ? '重命名' : '创建用户组' }}
        </div>
        <n-form-item>
          <n-input v-model:value="form.name" :maxlength="255" placeholder="请输入用户组名称" clearable/>
          <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium text-[var(--color-text-4)]">
            该用户组将在整个系统范围内可用
          </span>
        </n-form-item>
      </n-form>
      <div class="flex flex-row flex-nowrap justify-end gap-2">
        <n-button size="tiny" @click="handleCancel">取消</n-button>
        <n-button size="tiny">{{ props.id ? '确认' : '创建' }}</n-button>
      </div>
    </div>
  </n-popover>
</template>

<style scoped>
.move-left {
  position: relative;
  right: 22px;
}
</style>