<script setup lang="ts">
import {computed, ref, useAttrs, watch} from 'vue'
import {type FormInst, type FormItemRule, type FormRules, NPopconfirm} from 'naive-ui';


interface FieldConfig {
  field?: string;
  rules?: FormRules;
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
  nameExistTipText?: string; // 添加重复提示文本
}

export interface ConfirmValue {
  field: string;
  id?: string;
}

const attrs = useAttrs();
const props = withDefaults(
    defineProps<{
      title: string; // 文本提示标题
      subTitleTip?: string; // 子内容提示
      isDelete?: boolean; // 当前使用是否是移除
      loading?: boolean;
      okText?: string; // 确定按钮文本
      cancelText?: string;
      visible?: boolean; // 是否打开
      popupContainer?: string;
      fieldConfig?: FieldConfig; // 表单配置项
      allNames?: string[]; // 添加或者重命名名称重复
      nodeId?: string; // 节点 id
    }>(),
    {
      type: 'warning',
      isDelete: true, // 默认移除pop
      okText: '移除',
    }
);
const currentVisible = ref(props.visible || false);
const formRef = ref<FormInst | null>(null)
// 表单
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
const titleClass = computed(() => {
  return props.isDelete
      ? 'ml-2 font-medium text-[var(--color-text-1)] text-[14px]'
      : 'mb-[8px] font-medium text-[var(--color-text-1)] text-[14px] leading-[22px]';
});
const emits = defineEmits<{
  (e: 'confirm', formValue: ConfirmValue, cancel?: () => void): void;
  (e: 'cancel'): void;
  (e: 'update:visible', visible: boolean): void;
}>();
const handleNegativeClick = () => {
  currentVisible.value = false;
  emits('cancel');
  form.value.field = ''
  formRef.value?.restoreValidation()
}
watch(
    () => props.fieldConfig?.field,
    (val) => {
      form.value.field = val || '';
    }
);

watch(
    () => props.visible,
    (val) => {
      currentVisible.value = val;
    }
);

watch(
    () => currentVisible.value,
    (val) => {
      if (!val) {
        emits('cancel');
      }
      emits('update:visible', val);
    }
);
</script>

<template>
  <n-popconfirm trigger="click" v-bind="attrs"
                :positive-text="props.okText || '确认'"
                :negative-text="props.cancelText || '取消'"
                @negative-click="handleNegativeClick"
  >
    <template #trigger>
      <slot></slot>
    </template>
    <div class="flex flex-row flex-nowrap items-center">
<!--      <slot name="icon">-->
<!--        <base-icon v-if="props.isDelete" class="mr-[2px] text-xl text-[rgb(var(&#45;&#45;danger-6))]"/>-->
<!--      </slot>-->
      <div :class="[titleClass]">
        {{ props.title || '' }}
      </div>
    </div>
    <!-- 描述展示 -->
    <div v-if="props.subTitleTip" class="ml-8 mt-2 text-sm text-[var(--color-text-2)]">
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