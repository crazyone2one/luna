<script setup lang="ts">

import BaseCard from '/@/components/BaseCard.vue'
import {computed, h, onMounted, ref, useTemplateRef} from 'vue'
import {type DataTableColumns, type DataTableRowKey, NButton, NSwitch} from 'naive-ui'
import type {UserListItem} from '/@/types/user.ts'
import {usePagination} from 'alova/client'
import {fetchUserPage} from '/@/api/system/user.ts'
import EditUser from '/@/views/setting/system/user/components/EditUser.vue'
import BaseTagGroup from '/@/components/BaseTagGroup.vue'

type UserModalMode = 'create' | 'edit';
const editUserRef = useTemplateRef<InstanceType<typeof EditUser>>('editUser')
const checkedRowKeys = ref<DataTableRowKey[]>([])
const columns = computed<DataTableColumns<UserListItem>>(() => {
  return [
    {
      type: 'selection',
    },
    {
      title: '用户名',
      key: 'name',
    },
    {
      title: '邮箱',
      key: 'email',
    },
    {
      title: '手机',
      key: 'phone',
      width: 140
    },
    {
      title: '组织',
      key: 'organizationList',
      width: 300
    },
    {
      title: '用户组',
      key: 'userRoleList',
      width: 300,
      render(row) {
        return h(BaseTagGroup, {type: 'primary', tagList: row.userRoleList}, {})
      }
    },
    {
      title: '状态',
      key: 'enable',
      render(row) {
        return h(NSwitch, {value: row.enable}, {})
      }
    },
    {
      title: 'Action',
      key: 'actions',
      fixed: 'right',
      width: 250,
      render(row) {
        if (!row.enable) {
          return h(NButton, {size:'tiny'}, {default: () => '删除'})
        } else {
          return h(NButton, {size:'tiny'}, {default: () => '编辑'})
        }
      }
    }
  ]
})
const keyword = ref('');

const showAddModel = ref(false)
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {data, send: loadList, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, keyword: keyword.value}
  return fetchUserPage(param)
}, {
  initialData: {
    total: 0,
    data: []
  },
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
})
const showUserModal = (_mode: UserModalMode, _record?: UserListItem) => {
  showAddModel.value = true
}
onMounted(() => {
  loadList()
})
</script>

<template>
  <base-card :show="loading">
    <template #header>
      <n-button type="primary" @click="showUserModal('create')"> 创建用户</n-button>
    </template>
    <template #header-extra>
      <n-input class="w-[240px]" clearable placeholder="通过 ID/名称搜索"/>
    </template>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: UserListItem) => row.id"
        @update:checked-row-keys="handleCheck"
    />
  </base-card>
  <edit-user ref="editUserRef" v-model:show-modal="showAddModel" @reload="loadList"/>
</template>

<style scoped>

</style>