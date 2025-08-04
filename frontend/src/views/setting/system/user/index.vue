<script setup lang="ts">

import BaseCard from '/@/components/BaseCard.vue'
import {computed, h, onBeforeMount, ref, useTemplateRef} from 'vue'
import {
  type DataTableColumns,
  type DataTableRowKey,
  type DropdownOption,
  NButton,
  NModal,
  NSelect,
  NSwitch,
  type UploadFileInfo
} from 'naive-ui'
import type {SystemRole, UpdateUserInfoParams, UserListItem} from '/@/types/user.ts'
import {usePagination, useRequest} from 'alova/client'
import {
  deleteUserInfo,
  fetchUserPage,
  getSystemRoles,
  getUserTemplate,
  importUserInfo,
  resetUserPassword,
  updateUserInfo
} from '/@/api/system/user.ts'
import EditUser from '/@/views/setting/system/user/components/EditUser.vue'
import BaseTagGroup from '/@/components/BaseTagGroup.vue'
import {hasAllPermission, hasAnyPermission} from '/@/utils/permission.ts'
import BaseMoreAction from '/@/components/BaseMoreAction.vue'
import type {BatchActionQueryParams} from '/@/types/common.ts'
import {ArchiveOutline as ArchiveIcon} from '@vicons/ionicons5'

type UserModalMode = 'create' | 'edit';
const importVisible = ref(false);
const importSuccessCount = ref(0);
const importFailCount = ref(0);
const userImportFile = ref<UploadFileInfo[]>([]);
const importResultVisible = ref(false);
const importResult = ref<'success' | 'allFail' | 'fail'>('allFail');
const importResultTitle = ref('导入用户');
const importErrorMessageDrawerVisible = ref(false);
const importErrorMessages = ref<Record<string, any>>({});
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
const {send: downloadTemplate} = useRequest(() => getUserTemplate(), {immediate: false})

const downLoadUserTemplate = () => {
  downloadTemplate().then(res => {
    const anchor = document.createElement('a');
    anchor.href = URL.createObjectURL(res.data);
    anchor.download = 'user_import.xlsx';
    anchor.click();
    URL.revokeObjectURL(anchor.href);
  })
}
const showImportModal = () => {
  importVisible.value = true;
  importFailCount.value = 0;
  importSuccessCount.value = 0;
  importResult.value = 'allFail';
}
const cancelImport = () => {
  importVisible.value = false;
  importResultVisible.value = false;
  userImportFile.value = [];
}
const {loading: uploadLoading, send: sendUpload} = useRequest((file) => importUserInfo(file), {immediate: false})
const importUser = () => {
  sendUpload(userImportFile.value[0].file).then(res => {
    const {data} = res;
    const failCount = data.importCount - data.successCount;
    if (failCount === data.importCount) {
      importResult.value = 'allFail';
    } else if (failCount > 0) {
      importResult.value = 'fail';
    } else {
      importResult.value = 'success';
    }
    importSuccessCount.value = data.successCount;
    importFailCount.value = failCount;
    importErrorMessages.value = data.errorMessages;
    showImportResult();
  })
}
const showImportResult = () => {
  importVisible.value = false;
  switch (importResult.value) {
    case 'success':
      importResultTitle.value = '导入用户';
      loadList();
      break;
    case 'allFail':
      importResultTitle.value = '导入用户';
      break;
    case 'fail':
      importResultTitle.value = '导入用户';
      loadList();
      break;
    default:
      break;
  }
  importResultVisible.value = true;
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
      <n-button type="primary" size="small" @click="showUserModal('create')"> 创建用户</n-button>
      <n-button v-permission.all="['SYSTEM_USER:READ+IMPORT']" type="info" secondary size="small" class="mr-3"
                @click="showImportModal"> 导入用户
      </n-button>
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
  <n-modal v-model:show="importVisible" preset="dialog" title="导入用户" :mask-closable="false">
    <div>
      <n-alert type="info" class="mb-[16px]">
        用户组仅支持添加系统存在的用户组
        <n-button text size="small" @click="downLoadUserTemplate">下载模板</n-button>
      </n-alert>
      <n-upload
          v-model:file-list="userImportFile"
          accept="excel"
          directory-dnd
      >
        <n-upload-dragger>
          <div style="margin-bottom: 12px">
            <n-icon size="48" :depth="3">
              <ArchiveIcon/>
            </n-icon>
          </div>
          <n-text style="font-size: 16px">
            点击或者拖动文件到该区域来上传
          </n-text>
          <n-p depth="3" style="margin: 8px 0 0 0">
            请不要上传敏感数据，比如你的银行卡号和密码，信用卡号有效期和安全码
          </n-p>
        </n-upload-dragger>
      </n-upload>
    </div>
    <template #action>
      <div class="flex flex-row gap-[12px]">
        <n-button type="tertiary" size="small" :disabled="uploadLoading" @click="cancelImport">取消</n-button>
        <n-button type="primary" size="small" :loading="uploadLoading"
                  :disabled="userImportFile.length===0" @click="importUser">导入
        </n-button>
      </div>
    </template>
  </n-modal>
  <n-modal v-model:show="importResultVisible" preset="dialog" :title="importResultTitle" :mask-closable="false">
    <div v-if="importResult === 'success'" class="flex flex-col items-center justify-center">
      <div class="mb-[8px] mt-[16px] text-[16px] font-medium leading-[24px]">
        导入成功
      </div>
      <div class="sub-text">
        {{ `成功导入 ${importSuccessCount} 个用户` }}
      </div>
    </div>
    <template v-else>
      <n-alert type="error">
        <div class="flex items-center">
          {{ `成功导入用户 ${importSuccessCount} 个，导入失败` }}
          <div class="mx-[4px] text-red-6">{{ importFailCount }}</div>
          个
          <n-popover v-if="Object.keys(importErrorMessages).length > 0" trigger="hover"
                     content-class="w-[400px] p-0"
                     placement="bottom">
            <template #trigger>
              <n-button text>错误详情</n-button>
            </template>
            <div class="px-[16px] pt-[16px] text-[14px]">
              <div class="flex items-center font-medium">
                <div class="text-[var(--color-text-1)]">部分用户信息导入失败</div>
                <div class="ml-[4px] text-red-4">({{ importFailCount }})</div>
              </div>
              <div class="max-h-[250px] mt-[8px] overflow-y-auto">
                <div
                    v-for="key of Object.keys(importErrorMessages)"
                    :key="key"
                    class="mb-[16px] flex items-center"
                >
                  第
                  <div class="mx-[4px] font-medium">{{ key }}</div>
                  行：
                  {{ importErrorMessages[key] }}
                </div>
              </div>
            </div>
            <div v-if="Object.keys(importErrorMessages).length > 8" class="import-error-message-footer">
              <n-button text @click="importErrorMessageDrawerVisible = true">
                查看更多
              </n-button>
            </div>
          </n-popover>
        </div>
      </n-alert>
    </template>
  </n-modal>
  <n-drawer v-model:show="importErrorMessageDrawerVisible" :width="502">
    <n-drawer-content closable>
      <template #header>
        <div class="flex items-center font-medium">
          <div>部分用户信息导入失败</div>
          <div class="ml-[4px]">({{ importFailCount }})</div>
        </div>
      </template>
      <div class="max-h-[250px] !max-h-full overflow-y-auto">
        <div
            v-for="key of Object.keys(importErrorMessages)"
            :key="key"
            class="mb-[16px] flex items-center text-[var(--color-text-2)]"
        >
          <div class="mr-[8px] h-[6px] w-[6px] rounded-full"></div>
          第
          <div class="mx-[4px] font-medium">{{ key }}</div>
          个：
          {{ importErrorMessages[key] }}
        </div>
      </div>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>
.sub-text {
  font-size: 12px;
  line-height: 16px;
}
</style>