<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NButton, NSwitch} from 'naive-ui'
import {h, onMounted, ref, useTemplateRef} from 'vue'
import type {ITaskCenterTaskItem} from '/@/types/task.ts'
import {usePagination, useRequest} from 'alova/client'
import {fetchSchedulePage, fetchScheduleSwitch} from '/@/api/system/task.ts'
import BaseCard from '/@/components/BaseCard.vue'
import AddTaskModal from '/src/views/setting/components/AddTaskModal.vue'

const addTaskModelRef = useTemplateRef<InstanceType<typeof AddTaskModal>>('addTaskModel')
const showAddTaskModel = ref(false)
const columns: DataTableColumns<ITaskCenterTaskItem> = [
  {
    type: 'selection',
  },
  {
    title: 'num',
    key: 'num',
    render(row) {
      return h(NButton, {text: true, class: 'max-w-full justify-start px-0'}, {default: () => row.num})
    }
  },
  {
    title: 'status',
    key: 'enable',
    render(row) {
      return h(NSwitch, {size: 'small', value: row.enable, onUpdateValue: () => handleEnableChange(row)}, {})
    }
  },
  {
    title: 'resourceType',
    key: 'resourceType'
  },
  {
    title: 'runRule',
    key: 'value'
  },
  {
    title: 'Action',
    key: 'actions',
    fixed: 'right',
    render(row) {
      return [
        h(NButton, {type: 'error', size: 'small', class: '!mr-[12px]'}, {default: () => '删除'}),
        h(NButton, {type: 'primary', size: 'small', class: '!mr-0'}, {default: () => '详情'}),
      ]
    }
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const {data, send: loadList, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize}
  return fetchSchedulePage(param)
}, {
  initialData: {
    total: 0,
    data: []
  },
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
})
const {send: scheduleSwitch} = useRequest((id) => fetchScheduleSwitch(id), {immediate: false})
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const handleEnableChange = (record: ITaskCenterTaskItem) => {
  scheduleSwitch(record.id).then(() => {
    loadList()
  })
}
const addTask = () => {
  console.log( showAddTaskModel.value)
  showAddTaskModel.value = true
}
onMounted(() => {
  loadList()
})
</script>

<template>
  <base-card :show="loading">
    <template #header>
      <n-button type="primary" @click="addTask"> add</n-button>
    </template>
    <template #header-extra>
      <n-input class="w-[240px]" clearable placeholder="通过 ID/名称搜索"/>
    </template>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: ITaskCenterTaskItem) => row.id"
        @update:checked-row-keys="handleCheck"
    />
  </base-card>
  <add-task-modal ref="addTaskModelRef" v-model:show-modal="showAddTaskModel"/>
</template>

<style scoped>

</style>