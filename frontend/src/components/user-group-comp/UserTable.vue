<script setup lang="ts">
import {computed, h, inject, ref, watchEffect} from 'vue'
import {useAppStore} from '/@/store'
import type {CurrentUserGroupItem} from '/@/types/user-group.ts'
import {usePagination} from 'alova/client'
import {AuthScopeEnum} from '/@/enums/common.ts'
import {
  addOrgUserToUserGroup,
  addUserToUserGroup,
  deleteOrgUserFromUserGroup,
  deleteUserFromUserGroup,
  fetchOrgUserByUserGroup,
  fetchUserByUserGroup
} from '/@/api/system/usergroup.ts'
import BaseCard from '/@/components/BaseCard.vue'
import type {UserListItem} from '/@/types/user.ts'
import {type DataTableColumns} from 'naive-ui'
import BaseRemoveButton from '/@/components/BaseRemoveButton.vue'
import {hasAnyPermission} from '/@/utils/permission.ts'
import BaseConfirmUserSelector from '/@/components/base-user-selector/BaseConfirmUserSelector.vue'
import {UserRequestTypeEnum} from '/@/utils/common.ts'

const systemType = inject<string>('systemType');
const appStore = useAppStore();
const okLoading = ref(false)
const currentOrgId = computed(() => appStore.currentOrgId);
const props = defineProps<{
  keyword: string;
  current: CurrentUserGroupItem;
  deletePermission?: string[];
  readPermission?: string[];
  updatePermission?: string[];
}>();
const {send: loadList, data, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, roleId: '', userRoleId: '', organizationId: '', 'keyword': props.keyword}
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
      keyword: props.keyword,
      disabledKey: 'exclude',
    };
  }
  return {
    type: UserRequestTypeEnum.ORGANIZATION_USER_GROUP,
    loadOptionParams: {
      roleId: props.current.id,
      organizationId: currentOrgId.value,
    },
    keyword: props.keyword,
    disabledKey: 'checkRoleFlag',
  };
})

const handleAddMember = async (userIds: string[], callback: (v: boolean) => void) => {
  try {
    okLoading.value = true
    if (systemType === AuthScopeEnum.SYSTEM) {
      await addUserToUserGroup({roleId: props.current.id, userIds})
    }
    if (systemType === AuthScopeEnum.ORGANIZATION) {
      await addOrgUserToUserGroup({
        userRoleId: props.current.id,
        userIds,
        organizationId: currentOrgId.value,
      })
    }
    window.$message.success('添加成功')
    await fetchData();
    callback(true);
  } catch (e) {
    console.log(e);
    callback(false);
  } finally {
    okLoading.value = false
  }
}
const handlePermission = (permission: string[], cb: () => void) => {
  if (!hasAnyPermission(permission)) {
    return false;
  }
  cb();
};
const handleRemove = async (record: UserListItem) => {
  handlePermission(props.updatePermission || [], async () => {
    if (systemType === AuthScopeEnum.SYSTEM) {
      await deleteUserFromUserGroup(record.id);
    } else if (systemType === AuthScopeEnum.ORGANIZATION) {
      await deleteOrgUserFromUserGroup({
        organizationId: currentOrgId.value,
        userRoleId: props.current.id,
        userIds: [record.id],
      });
    }
    await fetchData();
  })
}
const fetchData = async () => {
  handlePermission(props.readPermission || [], async () => loadList())
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
    <base-confirm-user-selector v-bind="userSelectorProps" :ok-loading="okLoading" @confirm="handleAddMember"/>
    <div class="mt-2">
      <n-data-table
          :columns="columns"
          :data="data"
          :row-key="(row: UserListItem) => row.id"
      />
    </div>
  </base-card>
</template>

<style scoped>

</style>