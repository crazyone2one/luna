<script setup lang="ts">
import {computed, h, inject, watchEffect} from 'vue'
import {useAppStore} from '/@/store'
import type {CurrentUserGroupItem} from '/@/types/user-group.ts'
import {usePagination} from 'alova/client'
import {AuthScopeEnum} from '/@/enums/common.ts'
import {fetchOrgUserByUserGroup, fetchUserByUserGroup} from '/@/api/system/usergroup.ts'
import BaseCard from '/@/components/BaseCard.vue'
import type {UserListItem} from '/@/types/user.ts'
import {type DataTableColumns} from 'naive-ui'
import BaseRemoveButton from '/@/components/BaseRemoveButton.vue'
import {hasAnyPermission} from '/@/utils/permission.ts'
import BaseConfirmUserSelector from '/@/components/base-user-selector/BaseConfirmUserSelector.vue'
import {UserRequestTypeEnum} from '/@/utils/common.ts'

const systemType = inject<string>('systemType');
const appStore = useAppStore();
const currentOrgId = computed(() => appStore.currentOrgId);
const props = defineProps<{
  keyword: string;
  current: CurrentUserGroupItem;
  deletePermission?: string[];
  readPermission?: string[];
  updatePermission?: string[];
}>();
const {send: fetchData, data, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, roleId: '', userRoleId: '', organizationId: ''}
  if (systemType === AuthScopeEnum.SYSTEM) {
    param.roleId = props.current.id
    return fetchUserByUserGroup(param)
  } else {
    param.userRoleId = props.current.id
    param.organizationId = currentOrgId.value
    return fetchOrgUserByUserGroup(param)
  }
}, {
  initialData: {
    total: 0,
    data: []
  },
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
})
const columns = computed<DataTableColumns<UserListItem>>(() => {
  return [
    {
      type: 'selection',
    },
    {
      title: '姓名',
      key: 'name',
    },
    {
      title: '邮箱',
      key: 'email',
    },
    {
      title: '手机',
      key: 'phone',
    },
    {
      title: '操作',
      key: 'actions',
      fixed: 'right',
      width: 100,
      render(row) {
        if (hasAnyPermission(props.updatePermission || [])) {
          return h(BaseRemoveButton,
              {
                title: `确认移除 ${row.name} 这个用户吗？`,
                subTitleTip: '移除后，将失去用户组权限',
                disabled: systemType === AuthScopeEnum.SYSTEM && row.name === 'admin',
                onConfirm: () => handleRemove(row)
              }, {})
        }
      }
    }
  ]
})
const userSelectorProps = computed(() => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    return {
      type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
      loadOptionParams: {
        roleId: props.current.id,
      },
      disabledKey: 'exclude',
    };
  }
  return {
    type: UserRequestTypeEnum.ORGANIZATION_USER_GROUP,
    loadOptionParams: {
      roleId: props.current.id,
      organizationId: currentOrgId.value,
    },
    disabledKey: 'checkRoleFlag',
  };
})
const handleRemove = (record: UserListItem) => {

  console.log(record)
}
watchEffect(() => {
  if (props.current.id && currentOrgId.value) {
    fetchData()
  }
})
defineExpose({
  fetchData,
});
</script>

<template>
  <base-card v-model:show="loading">
    <base-confirm-user-selector v-bind="userSelectorProps" />
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: UserListItem) => row.id"
    />
  </base-card>
</template>

<style scoped>

</style>