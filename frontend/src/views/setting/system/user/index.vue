<script setup lang="ts">

import BaseCard from '/@/components/BaseCard.vue'
import {computed, h, onBeforeMount, ref, useTemplateRef} from 'vue'
import {type DataTableColumns, type DataTableRowKey, type DropdownOption, NButton, NSelect, NSwitch} from 'naive-ui'
import type {SystemRole, UpdateUserInfoParams, UserListItem} from '/@/types/user.ts'
import {usePagination, useRequest} from 'alova/client'
import {deleteUserInfo, fetchUserPage, getSystemRoles, resetUserPassword, updateUserInfo} from '/@/api/system/user.ts'
import EditUser from '/@/views/setting/system/user/components/EditUser.vue'
import BaseTagGroup from '/@/components/BaseTagGroup.vue'
import {hasAllPermission, hasAnyPermission} from '/@/utils/permission.ts'
import BaseMoreAction from '/@/components/BaseMoreAction.vue'
import type {BatchActionQueryParams} from '/@/types/common.ts'

type UserModalMode = 'create' | 'edit';
const editUserRef = useTemplateRef<InstanceType<typeof EditUser>>('editUser')
const checkedRowKeys = ref<DataTableRowKey[]>([])
const hasOperationSysUserPermission = computed(() =>
    hasAnyPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER:READ+DELETE'])
);
const userGroupOptions = ref<SystemRole[]>([]);
const options = [
  {
    label: '重置密码',
    key: 'resetPassword'
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '删除',
    key: 'delete'
  },
]
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
      width: 300,
      render(row) {
        return h(BaseTagGroup, {type: 'primary', tagList: row.organizationList}, {})
      }
    },
    {
      title: '用户组',
      key: 'userRoleList',
      width: 300,
      render(row) {
        if (!row.selectUserGroupVisible) {
          return h(BaseTagGroup, {
            type: 'primary', tagList: row.userRoleList,
            onClick: () => handleTagClick(row)
          }, {})
        } else {
          return h(NSelect, {
            value: row.userRoleList.map(u => u.id),
            placeholder: '请选择用户组',
            options: userGroupOptions.value,
            labelField: 'name',
            valueField: 'id',
            class: 'w-full max-w-[300px]', clearable: true, multiple: true, size: 'small',
            loading: selectUserGroupLoading.value,
            onUpdateValue: (value) => handleUserGroupChange(value, row)
          }, {})
        }
      }
    },
    {
      title: '状态',
      key: 'enable',
      render(row) {
        return h(NSwitch, {value: row.enable, size: 'small'}, {})
      }
    },
    {
      title: hasOperationSysUserPermission.value ? '操作' : '',
      key: 'actions',
      fixed: 'right',
      width: hasOperationSysUserPermission.value ? 110 : 50,
      render(row) {
        if (!row.enable) {
          if (hasAnyPermission(['SYSTEM_USER:READ+DELETE'])) {
            return h(NButton, {size: 'tiny'}, {default: () => '删除'})
          }
        } else {
          const res = []
          if (hasAnyPermission(['SYSTEM_USER:READ+UPDATE'])) {
            res.push(h(NButton, {
              text: true,
              size: 'tiny',
              class: 'pr-1',
              onClick: () => showUserModal('edit', row)
            }, {default: () => '编辑'}))
          }
          if (hasAnyPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER:READ+DELETE'])) {
            res.push(h(BaseMoreAction, {options: options, onSelect: (item) => handleSelect(item, row)}, {}))
          }
          return res
        }
      }
    }
  ]
})
const handleSelect = (item: DropdownOption, record: UserListItem) => {
  switch (item.key) {
    case 'resetPassword':
      resetPassword(record);
      break;
    case 'delete':
      deleteUser(record);
      break;
    default:
      break;
  }
}
const resetPassword = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let selectIds = [record?.id || ''];
  let title = `是否将 ${record?.name} 的密码重置为初始密码？`
  if (isBatch) {
    title = `是否将选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户的密码重置为初始密码？`
    selectIds = checkedRowKeys.value as string[]
  }
  let content = '初始的密码为用户邮箱，下次登录时生效'
  if (record && record.name === 'admin') {
    content = '初始的密码为 Password@，下次登录时生效'
  }
  window.$dialog.warning({
    title: title, content: content,
    positiveText: '确认重置',
    negativeText: '取消',
    maskClosable: false,
    onPositiveClick: async () => {
      await resetUserPassword({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      });
      window.$message.success('重置成功')
    }
  })
}
const deleteUser = (record?: UserListItem, isBatch?: boolean, params?: BatchActionQueryParams) => {
  let selectIds = [record?.id || ''];
  let title = `确认删除 ${record?.name} 这个用户吗？`
  if (isBatch) {
    title = `确认删除已选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户吗？`
    selectIds = checkedRowKeys.value as string[]
  }
  window.$dialog.error({
    title: title, content: '仅删除用户信息，不处理该用户的系统数据',
    positiveText: '确认删除',
    negativeText: '取消',
    maskClosable: false,
    onPositiveClick: async () => {
      await deleteUserInfo({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      });
      window.$message.success('删除成功')
      await loadList()
    }
  })
}
const keyword = ref('');
const showAddModel = ref(false)
const userFormMode = ref<UserModalMode>('create');
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
const userFrom = ref<UpdateUserInfoParams>({
  id: '',
  name: '',
  email: '',
  phone: '',
  userRoleIdList: []
})
const showUserModal = (mode: UserModalMode, record?: UserListItem) => {
  showAddModel.value = true
  userFormMode.value = mode
  if (mode === 'edit' && record) {
    userFrom.value.id = record.id
    userFrom.value.name = record.name
    userFrom.value.email = record.email
    userFrom.value.phone = record.phone
    userFrom.value.userRoleIdList = record.userRoleList.map(r => r.id)
  }
}
const handleTagClick = (record: UserListItem) => {
  if (hasAllPermission(['SYSTEM_USER:READ+UPDATE', 'SYSTEM_USER_ROLE:READ'])) {
    record.selectUserGroupVisible = true;
  }
}
const {
  send: updateUser,
  loading: selectUserGroupLoading
} = useRequest((param) => updateUserInfo(param), {immediate: false})
const handleUserGroupChange = (value: Array<string>, record: UserListItem & Record<string, any>) => {
  const params = {
    id: record.id,
    name: record.name,
    email: record.email,
    phone: record.phone,
    userRoleIdList: value,
  };
  updateUser(params).then(() => {
    record.selectUserGroupVisible = false
    window.$message.success('更新成功')
  })

}
const init = async () => {
  userGroupOptions.value = await getSystemRoles();

}
onBeforeMount(() => {
  init()
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
  <edit-user ref="editUserRef" v-model:show-modal="showAddModel"
             :user-group-options="userGroupOptions"
             v-model:user-form-mode="userFormMode"
             v-model:update-user-info-params="userFrom"
             @reload="loadList"/>
</template>

<style scoped>

</style>