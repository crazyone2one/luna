<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NButton, NSwitch} from 'naive-ui'
import {h, onMounted, ref, useTemplateRef} from 'vue'
import type {ITaskCenterTaskItem} from '/@/types/task.ts'
import {usePagination, useRequest} from 'alova/client'
import {fetchSchedulePage, fetchScheduleSwitch, organizationEditCron} from '/@/api/system/task.ts'
import BaseCard from '/@/components/BaseCard.vue'
import AddTaskModal from '/src/views/setting/organization/task-center/components/AddTaskModal.vue'
import {useAppStore} from '/@/store'
import dayjs from 'dayjs'
import {hasAnyPermission} from '/@/utils/permission.ts'
import BaseCronSelect from '/@/components/BaseCronSelect.vue'

const addTaskModelRef = useTemplateRef<InstanceType<typeof AddTaskModal>>('addTaskModel')
const showAddTaskModel = ref(false)
const type = ref('org')
const appStore = useAppStore()
const getCurrentPermission = (action: 'DELETE' | 'EDIT') => {
  return {
    system: {
      DELETE: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    org: {
      DELETE: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    project: {
      DELETE: 'PROJECT_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'PROJECT_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
  }[type.value]?.[action];
}
const columns: DataTableColumns<ITaskCenterTaskItem> = [
  {
    type: 'selection',
  },
  {
    title: '任务 ID',
    key: 'num',
    width: 100,
    render(row) {
      return h(NButton, {text: true, class: 'max-w-full justify-start px-0'}, {default: () => row.num})
    }
  },
  {
    title: '任务名称',
    key: 'name',
    width: 200
  },
  {
    title: '状态',
    key: 'enable',
    width: 60,
    render(row) {
      return h(NSwitch, {size: 'small', value: row.enable, onUpdateValue: () => handleEnableChange(row)}, {})
    }
  },
  {
    title: '运行规则',
    key: 'value',
    width: 200,
    render(row) {
      if (hasAnyPermission([getCurrentPermission('EDIT') as string])) {
        return h(BaseCronSelect, {
          modelValue: row.value,
          size: 'tiny',
          onChange: (val) => handleRunRuleChange(val, row)
        }, {})
      } else {
        return h('span', null, {default: () => row.value})
      }
    }
  },
  {
    title: '上次完成时间',
    key: 'lastTime',
    width: 170,
    render(item) {
      return h('span', null, {
        default: () => item.lastTime ? dayjs(item.lastTime).format('YYYY-MM-DD HH:mm:ss') : '-'
      })
    }
  },
  {
    title: '下次执行时间',
    key: 'nextTime',
    width: 170,
    render(item) {
      return h('span', null, {
        default: () => item.nextTime ? dayjs(item.nextTime).format('YYYY-MM-DD HH:mm:ss') : '-'
      })
    }
  },
  {
    title: '操作人',
    key: 'createUser',
    width: 150,
  },
  {
    title: '操作时间',
    key: 'createTime',
    width: 170,
    render(item) {
      return h('span', null, {
        default: () => item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'
      })
    }
  },
  {
    title: 'Action',
    key: 'actions',
    fixed: 'right',
    width: 110,
    render(row) {
      return [
        h(NButton, {type: 'error', size: 'tiny', class: '!mr-[12px]'}, {default: () => '删除'}),
        h(NButton, {type: 'primary', size: 'tiny', class: '!mr-0'}, {default: () => '详情'}),
      ]
    }
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const {data, send: loadList, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, orgId: appStore.currentOrgId}
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
const handleRunRuleChange = async (val: string, record: ITaskCenterTaskItem) => {
  await organizationEditCron(val, record.id);
  window.$message.success('更新成功')
}
const addTask = () => {
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
  <add-task-modal ref="addTaskModelRef" v-model:show-modal="showAddTaskModel" @refresh="loadList"/>
</template>

<style scoped>

</style>