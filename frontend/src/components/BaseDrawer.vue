<script setup lang="ts">
import {NDrawer, NDrawerContent} from 'naive-ui';

interface DrawerProps {
  title?: string | undefined;
  titleTag?: string;
  saveContinueText?: string;
  cancelText?: string;
  okText?: string;
  showContinue?: boolean;
  okPermission?: string[]; // 确认按钮权限
}
const emit = defineEmits(['update:visible', 'confirm', 'cancel', 'continue', 'close']);
const props = withDefaults(defineProps<DrawerProps>(), {})
const active = defineModel('active', {type: Boolean, default: false})
const handleClose = () => {
  active.value = false
  emit('cancel')
}
</script>

<template>
  <n-drawer v-model:show="active" :width="800" @after-leave="handleClose">
    <n-drawer-content closable>
      <template #header>
        <div class="flex items-center justify-between gap-[4px]">
          <slot name="title">
            <div class="flex flex-1 items-center justify-between overflow-hidden">
              <div class="flex flex-1 items-center overflow-hidden">
                <span class="one-line-text max-w-[300px]"> {{ props.title }}</span>
                <slot name="headerLeft"></slot>
                <n-tag v-if="titleTag" class="ml-[8px] mr-auto">
                  {{ props.titleTag }}
                </n-tag>
              </div>
            </div>
          </slot>
          <div class="drawer-right-operation-button">
          </div>
        </div>
      </template>
      <div class="drawer-body">
        <slot>
        </slot>
      </div>
      <template #footer>
        <slot name="footer">
          <div class="flex items-center justify-between">
            <slot name="footerLeft"></slot>
            <div class="ml-auto flex gap-[12px]">
              <n-button>{{ props.cancelText || '取消' }}</n-button>
              <n-button v-if="showContinue" v-permission="props.okPermission || []">
                {{ props.saveContinueText || '保存并继续添加' }}
              </n-button>
              <n-button v-permission="props.okPermission || []">{{ props.okText || '确认' }}</n-button>
            </div>
          </div>
        </slot>

      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>