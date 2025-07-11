<script setup lang="ts">
import BaseModal from '/@/components/BaseModal.vue'
import {type FormInst} from 'naive-ui'
import {ref} from 'vue'
import {useForm} from 'alova/client'
import {fetchCreateSchedule} from '/@/api/system/task.ts'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const formRef = ref<FormInst | null>(null)

const rules = {
  inputValue: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 inputValue'
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
  value: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 cron value'
  },
}

const {form, reset} = useForm(form => fetchCreateSchedule(form), {
  immediate: false,
  initialForm: {
    name: '',
    job: '',
    value: '',
  }
})
const handleSubmit = () => {
  console.log(form.value)
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
        <n-input v-model:value="form.name"/>
      </n-form-item>
      <n-form-item label="job" path="job">
        <n-input v-model:value="form.job"/>
      </n-form-item>
      <n-form-item label="cron" path="cron">
        <n-input v-model:value="form.value"/>
      </n-form-item>
    </n-form>
  </base-modal>
</template>

<style scoped>

</style>