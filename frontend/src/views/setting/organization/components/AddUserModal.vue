<script setup lang="ts">

import BaseModal from '/@/components/BaseModal.vue'
import {ref, watch} from 'vue'
import type {ISelectOption} from '/@/types/common.ts'
import type {UserListItem} from '/@/types/user.ts'
import type {DataTableColumns} from 'naive-ui'
import {usePagination, useRequest} from 'alova/client'
import {
  addProjectMemberByOrg,
  addUserToOrgOrProject,
  getOrganizationMemberListPage,
  getSystemMemberListPage
} from '/@/api/system/org-project.ts'
import {getProjectUserGroup} from '/@/api/project'
import {getGlobalUserGroup} from '/@/api/system/usergroup.ts'

const userGroupIds = ref<string[]>([]);
const currentVisible = defineModel<boolean>('visible', {
  required: true,
});
const props = defineProps<{
  isOrganization?: boolean; // 组织下的
  userGroupOptions?: ISelectOption[];
  organizationId?: string;
  projectId?: string;
}>();
const emit = defineEmits<{
  (e: 'submit'): void;
}>();
const keyword = ref<string>('');
const currentUserGroupOptions = ref<ISelectOption[]>([])
const checkedRowKeys = ref<string[]>([])
const columns: DataTableColumns<UserListItem> = [
  {
    type: 'selection',
    disabled: (row: UserListItem) => row.memberFlag as boolean
  },
  {
    title: '姓名',
    key: 'name',
    width: 200,
  },
  {
    title: '邮箱',
    key: 'email',
    width: 250,
  },
]
const handleCheck = (rowKeys: string[]) => {
  checkedRowKeys.value = rowKeys
}
const {data, send: loadList, loading} = usePagination((page, pageSize) => {
  const param = {
    page, pageSize, keyword: keyword.value, projectId: props.projectId,
    sourceId: props.projectId ?? props.organizationId,
    organizationId: props.organizationId
  }
  if (props.isOrganization) {
    return getOrganizationMemberListPage(param)
  } else {
    return getSystemMemberListPage(param)
  }
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
const getUserGroupOptions = async () => {
  if (props.organizationId && !props.isOrganization) {
    // 系统-组织与项目-组织-成员用户组下拉
    currentUserGroupOptions.value = await getGlobalUserGroup(props.organizationId);
  } else if (props.projectId) {
    // 系统-组织与项目-项目-成员用户组下拉 和 组织-项目-成员用户组下拉
    currentUserGroupOptions.value = await getProjectUserGroup(props.projectId);
  }
}
const handleCancel = () => {
  currentVisible.value = false;
  keyword.value = '';
  userGroupIds.value = [];
}
const {loading: addUserLoading, send: addUser} = useRequest(() => {
  if (!props.isOrganization) {
    return addUserToOrgOrProject({
      userRoleIds: userGroupIds.value,
      userIds: checkedRowKeys.value,
      projectId: props.projectId,
      organizationId: props.organizationId,
    })
  } else {
    return addProjectMemberByOrg({
      userRoleIds: userGroupIds.value,
      userIds: checkedRowKeys.value,
      projectId: props.projectId,
    })
  }
}, {immediate: false})
const handleAddMember = () => {
  addUser().then(() => {
    window.$message.success('添加成功')
    emit('submit');
    handleCancel();
  })
}
watch(() => currentVisible.value, (newValue) => {
  if (newValue) {
    loadList()
    if (!props.userGroupOptions) {
      getUserGroupOptions();
    } else {
      currentUserGroupOptions.value = props.userGroupOptions;
    }
    if (props.projectId) {
      userGroupIds.value = ['project_member'];
    } else if (props.organizationId) {
      userGroupIds.value = ['org_member'];
    }
  }
})
</script>

<template>
  <base-modal v-model:show-modal="currentVisible"
              title="添加成员" :show-footer="false">
    <div class="mb-[16px] flex justify-end">
      <n-input v-model:value="keyword" placeholder="通过名称/邮箱搜索" clearable class="w-[240px]"/>
    </div>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: UserListItem) => row.id"
        :loading="loading"
        @update:checked-row-keys="handleCheck"
    />
    <template #footer>
      <div class="flex justify-between">
        <div class="flex items-center gap-[8px]">
          <div class="text-nowrap">用户组</div>
          <n-select v-model:value="userGroupIds" :options="userGroupOptions"
                    label-field="name"
                    value-field="id"
                    multiple class="!w-[240px] text-start"
                    placeholder="请为以上成员选择用户组"/>
        </div>
        <div class="flex gap-[12px] ml-2">
          <n-button :loading="addUserLoading" @click="handleCancel">取消</n-button>
          <n-button type="primary" :loading="addUserLoading"
                    :disabled="!userGroupIds.length || !checkedRowKeys.length"
                    @click="handleAddMember">
            添加
          </n-button>
        </div>
      </div>
    </template>
  </base-modal>
</template>

<style scoped>

</style>