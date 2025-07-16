<script setup lang="ts">
import BaseModal from '/@/components/BaseModal.vue'
import {type FormInst} from 'naive-ui'
import {ref} from 'vue'
import {useForm} from 'alova/client'
import {fetchCreateSchedule} from '/@/api/system/task.ts'
import {useAppStore} from '/@/store'
import BaseIcon from '/@/components/BaseIcon.vue'
import BaseCronSelect from '/@/components/BaseCronSelect.vue'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const formRef = ref<FormInst | null>(null)
const appStore = useAppStore()
const rules = {
  value: {
    required: true,
    trigger: ['input'],
    message: '请输入 cron表达式'
  },
  name: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 name'
  },
  job: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 job'
  },
}
const emit = defineEmits<{ (e: 'refresh'): void; }>();

const {form, reset, send} = useForm(form => fetchCreateSchedule(form), {
  immediate: false,
  initialForm: {
    name: '',
    job: '',
    value: '',
    resourceId: appStore.currentProjectId,
    enable: true
  }
})
const handleSubmit = () => {
  send().then(() => {
    window.$message.success('任务添加成功')
    handleCancel()
    emit('refresh')
  })
  // console.log(form.value)
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
      <span>add task</span>
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
      <n-form-item label="taskName" path="name">
        <n-input v-model:value="form.name" placeholder="录入任务名称"/>
      </n-form-item>
      <n-form-item label="job" path="job">
        <n-input v-model:value="form.job" clearable placeholder="录入任务对应的类名"/>
        <n-tooltip trigger="hover">
          <template #trigger>
            <base-icon type="fallback"/>
          </template>
          录入类似cn.master.luna.job.DemoJob
        </n-tooltip>
      </n-form-item>
      <n-form-item label="cron表达式" path="value">
        <base-cron-select v-model:model-value="form.value" size="medium"/>
      </n-form-item>
    </n-form>
    <template #actionLeft>
      <div class="mr-3">
        <n-switch size="small" v-model:value="form.enable"/>
        <span>状态</span>
      </div>
    </template>
  </base-modal>
</template>

<style scoped>

</style>