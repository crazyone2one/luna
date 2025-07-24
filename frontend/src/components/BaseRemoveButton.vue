<script setup lang="ts">

import {ref, useAttrs} from 'vue'
import {type ButtonProps, type FormInst, type FormItemRule, type FormRules, NPopconfirm} from 'naive-ui'
import type {ConfirmValue} from '/@/components/BasePopover.vue'

interface FieldConfig {
  field?: string;
  rules?: FormRules;
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
  nameExistTipText?: string; // 添加重复提示文本
}

const props = withDefaults(
    defineProps<{
      title: string;
      subTitleTip: string;
      loading?: boolean;
      removeText?: string;
      okText?: string;
      disabled?: boolean;
      fieldConfig?: FieldConfig; // 表单配置项
      allNames?: string[]; // 添加或者重命名名称重复
      isDelete?: boolean; // 当前使用是否是移除
      nodeId?: string; // 节点 id
      cancelText?: string;
    }>(),
    {
      okText: '移除',
      disabled: false,
    }
);
const attrs = useAttrs();
const emits = defineEmits<{
  (e: 'confirm', formValue: ConfirmValue, cancel?: () => void): void;
  (e: 'cancel'): void;
}>();
const currentVisible = ref(false);
const formRef = ref<FormInst | null>(null)
const form = ref({
  field: props.fieldConfig?.field || '',
});
const validateName = (_rule: FormItemRule, value: string) => {
  if ((props.allNames || []).includes(value)) {
    if (props.fieldConfig && props.fieldConfig.nameExistTipText) {
      return new Error(props.fieldConfig.nameExistTipText);
    } else {
      return new Error('名称已存在');
    }
  }
}

const handleNegativeClick = () => {
  currentVisible.value = false;
  emits('cancel');
  form.value.field = ''
  formRef.value?.restoreValidation()
}
const handlePositiveClick = () => {
  if (!formRef.value) {
    emits('confirm', {...form.value, id: props.nodeId}, handleNegativeClick);
    return;
  }
  formRef.value?.validate(err => {
    if (!err) {
      emits('confirm', {...form.value, id: props.nodeId}, handleNegativeClick);
    }
  })
}
const buttonProps: ButtonProps = {
  size: 'tiny'
}
</script>

<template>
  <n-popconfirm trigger="click" v-bind="attrs"
                :positive-text="props.okText || '确认'"
                :negative-text="props.cancelText || '取消'"
                :negative-button-props=buttonProps
                :positive-button-props=buttonProps
                @negative-click="handleNegativeClick"
                @positive-click="handlePositiveClick"
  >
    <template #trigger>
      <slot>
        <n-button type="error" size="tiny" :disabled="props.disabled">{{ props.removeText||'移除' }}</n-button>
      </slot>
    </template>
    <div class="flex flex-row flex-nowrap items-center">
      {{ props.title || '' }}
    </div>
    <!-- 描述展示 -->
    <div v-if="props.subTitleTip" class="text-sm">
      {{ props.subTitleTip }}
    </div>
    <n-form v-else ref="formRef" :model="form" :rules="props.fieldConfig?.rules || [{ required: true, message: '名称不能为空' },
              { validator: validateName },]">
      <n-form-item>
        <n-input :type="props.fieldConfig?.isTextArea ? 'textarea':'text'" v-model:value="form.field"
                 :maxlength="props.fieldConfig?.isTextArea ?props.fieldConfig?.maxLength || 1000:255"
                 :placeholder="props.fieldConfig?.placeholder"
                 class="w-[245px]"/>
      </n-form-item>
    </n-form>
  </n-popconfirm>
</template>

<style scoped>

</style>