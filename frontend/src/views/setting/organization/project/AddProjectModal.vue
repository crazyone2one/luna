<script setup lang="ts">
import BaseModal from '/@/components/BaseModal.vue'
import type {CreateOrUpdateSystemProjectParams} from '/@/types/project.ts'
import {computed, ref} from 'vue'
import type {FormInst, FormRules} from 'naive-ui'
import BaseIcon from '/@/components/BaseIcon.vue'
import {useForm} from 'alova/client'
import {createOrUpdateProjectByOrg} from '/@/api/system/org-project.ts'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const props = defineProps<{
  currentProject?: CreateOrUpdateSystemProjectParams;
}>();
const isEdit = computed(() => !!(props.currentProject && props.currentProject.id));
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
  name: [{
    required: true,
    trigger: ['blur', 'input'],
    message: '项目名称不能为空',
  },{max:255,message:'名称不能超过 255 个字符'}],
  userIds: {
    required: true,
    trigger: ['blur', 'input'],
    message: '项目管理员不能为空'
  },
}
const {form, reset} = useForm(form => createOrUpdateProjectByOrg(form), {
  immediate: false,
  initialForm: {
    // 项目名称
    name: '',
    // 项目描述
    description: '',
    // 启用或禁用
    enable: true,
    // 项目成员
    userIds: [],
    // 模块配置
    moduleIds: [],
    // 所属组织
    organizationId: ''
  }
})
const handleSubmit = () => {

}
const handleCancel = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
}
</script>

<template>
  <base-modal v-model:show-modal="showModal" @submit="handleSubmit" @cancel="handleCancel">
    <template #header>
      <span v-if="isEdit" class="flex">
        更新项目
        <div class="ml-[4px] flex text-[var(--color-text-4)]">
          (<div class="one-line-text max-w-[300px]">{{ props.currentProject?.name }}</div>)
        </div>
      </span>
      <span v-else>
        创建项目
      </span>
    </template>
    <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
        size="small"
    >
      <n-form-item label="项目名称" path="name">
        <n-input v-model:value="form.name" placeholder="请输入项目名称，不可与其他项目名称重复"/>
      </n-form-item>
      <n-form-item label="所属组织" path="organizationId">
        <n-input v-model:value="form.organizationId" placeholder="请选择所属组织"/>
      </n-form-item>
      <n-form-item label="项目管理员" path="userIds">
        <n-select v-model:value="form.userIds" placeholder="请选择项目管理员"/>
      </n-form-item>
      <n-form-item label="启用模块" path="module">
        <n-select v-model:value="form.userIds" placeholder="请选择项目管理员"/>
      </n-form-item>
    </n-form>
    <template #actionLeft>
      <n-switch size="small"/>
      <span>状态</span>
      <n-tooltip>
        <template #trigger>
          <base-icon type="fallback"/>
        </template>
        项目开启后，将展示在项目切换列表
      </n-tooltip>
    </template>
  </base-modal>
</template>

<style scoped>

</style>