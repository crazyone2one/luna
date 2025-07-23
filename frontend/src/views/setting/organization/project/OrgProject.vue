<script setup lang="ts">

import BaseCard from '/@/components/BaseCard.vue'
import {computed, h, onMounted, ref, useTemplateRef} from 'vue'
import {type DataTableColumns, type DataTableRowKey, NButton, NSpace, NSwitch} from 'naive-ui'
import type {CreateOrUpdateSystemProjectParams, IProjectItem} from '/@/types/project.ts'
import {usePagination} from 'alova/client'
import {fetchOrgProjectPage} from '/@/api/system/org-project.ts'
import {useAppStore} from '/@/store'
import AddProjectModal from '/@/views/setting/organization/project/AddProjectModal.vue'
import {hasAnyPermission} from '/@/utils/permission.ts'

const addProjectModalRef = useTemplateRef<InstanceType<typeof AddProjectModal>>('addProjectModal')
const appStore = useAppStore();
const keyword = ref('');
const currentProjectId = ref('');
const addProjectVisible = ref(false);
const currentUpdateProject = ref<CreateOrUpdateSystemProjectParams>();
const currentOrgId = computed(() => appStore.currentOrgId);
const checkedRowKeys = ref<DataTableRowKey[]>([])
const columns = computed<DataTableColumns<IProjectItem>>(() => {
  return [
    {
      type: 'selection',
    },
    {
      title: 'ID',
      key: 'num',
      width: 150
    },
    {
      title: '名称',
      key: 'name',
    },
    {
      title: '成员',
      key: 'memberCount',
      render(record) {
        if (hasAnyPermission(['ORGANIZATION_PROJECT:READ+ADD_MEMBER', 'ORGANIZATION_PROJECT:READ'])) {
          return h(NButton, {
            text: true,
            type: 'primary',
            onClick: () => showUserDrawer(record)
          }, {default: () => record.memberCount})
        } else {
          return h('span', null, {default: () => record.memberCount})
        }
      }
    },
    {
      title: '状态',
      key: 'enable',
      render(row) {
        return h(NSwitch, {size: 'small', value: row.enable, onUpdateValue: (v) => handleChangeEnable(v, row)}, {})
      }
    },
    {
      title: '描述',
      key: 'description',
    },
    {
      title: '所属组织',
      key: 'organizationName',
    },
    {
      title: 'Action',
      key: 'actions',
      fixed: 'right',
      width: 250,
      render(row) {
        if (row.enable) {
          return h(NSpace, {}, {
            default: () => {
              return [
                h(NButton, {size: 'tiny', type: 'warning'}, {default: () => '编辑'}),
                h(NButton, {size: 'tiny', type: 'info', disabled: true}, {default: () => '添加成员'}),
                h(NButton, {size: 'tiny', type: 'tertiary', disabled: true}, {default: () => '进入项目'}),
              ]
            }
          })
        } else {
          return h(NButton, {size: 'tiny', type: 'error'}, {default: () => '删除'})
        }
      }
    }
  ]
})
const showUserDrawer = (record: IProjectItem) => {
  currentProjectId.value = record.id;
  console.log(record)
}
/**
 * 切换项目状态
 * @param isEnable
 * @param record
 */
const handleChangeEnable = (isEnable = true, record: IProjectItem) => {
  const title = isEnable ? '启用项目' : '关闭项目';
  const content = isEnable ? '开启后的项目展示在项目切换列表' : '结束后的项目不展示在项目切换列表';
  window.$dialog.error({
    title, content,
    negativeText: '取消',
    positiveText: isEnable ? '确认开启' : '确认关闭',
    onPositiveClick: () => {
      console.log(record.id)
      console.log(isEnable)
    }
  })
}

const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {data, send: loadList, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, organizationId: currentOrgId.value, keyword: keyword.value}
  return fetchOrgProjectPage(param)
}, {
  initialData: {
    total: 0,
    data: []
  },
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
const showAddProject = () => {
  addProjectVisible.value = true
  currentUpdateProject.value = undefined
}
const handleAddProjectModalCancel = (shouldSearch: boolean) => {
  currentUpdateProject.value = undefined;
  if (shouldSearch) {
    loadList();
  }
};
onMounted(() => {
  loadList()
})
</script>

<template>
  <base-card :show="loading">
    <template #header>
      <n-button v-permission="['ORGANIZATION_PROJECT:READ+ADD']" type="primary" @click="showAddProject"> 创建项目
      </n-button>
    </template>
    <template #header-extra>
      <n-input v-model:value="keyword" class="w-[240px]" clearable placeholder="通过 ID/名称搜索"/>
    </template>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: IProjectItem) => row.id"
        @update:checked-row-keys="handleCheck"
    />
  </base-card>
  <add-project-modal ref="addProjectModalRef" v-model:show-modal="addProjectVisible"
                     :current-project="currentUpdateProject"
                     @cancel="handleAddProjectModalCancel"/>
</template>

<style scoped>

</style>