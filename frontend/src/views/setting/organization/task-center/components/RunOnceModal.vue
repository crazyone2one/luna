<script setup lang="ts">

import BaseModal from '/@/components/BaseModal.vue'
import type {ITaskCenterTaskItem} from '/@/types/task.ts'
import {useForm} from 'alova/client'
import {fetchRunSchedule} from '/@/api/system/task.ts'
import BaseSensorTypeSelect from '/@/components/BaseSensorTypeSelect.vue'
import {watch} from 'vue'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const currentTask = defineModel<ITaskCenterTaskItem>('currentTask', {default: {}})
const {form, reset, send} = useForm(form => fetchRunSchedule(form),
    {
      immediate: false,
      initialForm: {
        scheduleId: '',
        sensorType: ''
      }
    }
)
const handleSubmit = () => {
  console.log(form.value)
  send()
}
const handleCancel = () => {
  reset()
  showModal.value = false
}

watch(() => currentTask.value, () => {
      form.value.scheduleId = currentTask.value.id
    },
    {deep: true, immediate: true})
</script>

<template>
  <base-modal v-model:show-modal="showModal" @submit="handleSubmit" @cancel="handleCancel">
    <template #header>
      <span>run task</span>
    </template>
    <n-form
        ref="formRef"
        :model="form"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
        size="small"
    >
      <n-form-item label="测点类型">
        <base-sensor-type-select v-model:model-value="form.sensorType"/>
      </n-form-item>
    </n-form>
  </base-modal>
</template>

<style scoped>

</style>