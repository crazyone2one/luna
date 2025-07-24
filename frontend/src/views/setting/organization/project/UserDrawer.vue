<script setup lang="ts">
import BaseDrawer from '/@/components/BaseDrawer.vue'
import {hasAnyPermission} from '/@/utils/permission.ts'
import {computed, h, ref, watch} from 'vue'
import {type DataTableColumns, NButton} from 'naive-ui'
import type {UserListItem} from '/@/types/user.ts'
import BaseTagGroup from '/@/components/BaseTagGroup.vue'
import {usePagination, useRequest} from 'alova/client'
import {deleteProjectMemberByOrg, postProjectMemberByProjectId} from '/@/api/system/org-project.ts'
import BaseCard from '/@/components/BaseCard.vue'
import {getProjectUserGroup} from '/@/api/project'
import type {ISelectOption} from '/@/types/common.ts'
import BaseRemoveButton from '/@/components/BaseRemoveButton.vue'
import AddUserModal from '/@/views/setting/organization/components/AddUserModal.vue'

export interface projectDrawerProps {
  organizationId?: string;
  projectId?: string;
  currentName?: string;
}

const props = defineProps<projectDrawerProps>();
const userGroupOptions = ref<Array<ISelectOption>>([])
const visible = defineModel('visible', {type: Boolean, default: false})
const userVisible = ref(false);
const emit = defineEmits<{
  (e: 'cancel'): void;
  (e: 'requestFetchData'): void;
}>();
const handleCancel = () => {
  emit('cancel')
}
const hasOperationPermission = computed(() => hasAnyPermission(['ORGANIZATION_PROJECT:READ+DELETE_MEMBER']));
const columns = computed<DataTableColumns<UserListItem>>(() => {
  return [
    {
      title: '姓名',
      key: 'name',
      width: 200,
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
      title: '邮箱',
      key: 'email', width: 180,
    },
    {
      title: '手机',
      key: 'phone',
      width: 140
    },

    {
      title: hasOperationPermission.value ? '操作' : '',
      key: 'actions',
      fixed: 'right',
      width: hasOperationPermission.value ? 60 : 20,
      render(row) {
        if (hasAnyPermission(['ORGANIZATION_PROJECT:READ+DELETE_MEMBER'])) {
          return h(BaseRemoveButton,
              {
                title: `确认移除 ${row.name} 这个用户吗？`,
                subTitleTip: '移除后，将失去项目权限',
                onConfirm: () => handleRemove(row)
              },
              {})
        }
      }
    }
  ]
})
const keyword = ref('');
const {data, send: fetchData, loading} = usePagination((page, pageSize) => {
  const param = {page, pageSize, keyword: keyword.value, projectId: props.projectId}
  return postProjectMemberByProjectId(param)
}, {
  initialData: {
    total: 0,
    data: []
  },
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
})
const {send: fetchUserGroupOption} = useRequest((projectId) => getProjectUserGroup(projectId), {immediate: false})
const getUserGroupOptions = () => {
  if (props.projectId) {
    fetchUserGroupOption(props.projectId).then(res => {
      userGroupOptions.value = []
      userGroupOptions.value = res
    })
  }
}
const handleRemove = async (record: UserListItem) => {
  try {
    if (props.projectId) {
      await deleteProjectMemberByOrg(props.projectId, record.id)
      window.$message.success('移除成功')
      await fetchData()
      emit('requestFetchData')
    }
  } catch (e) {
    console.error(e)
  }
}
const handleAddMember = () => userVisible.value = true;
const handleAddMemberSubmit = () => {
  userVisible.value = false
  fetchData()
  emit('requestFetchData');
}
watch([() => props.projectId, () => visible.value], () => {
  if (visible.value) {
    fetchData();
    getUserGroupOptions();
  }
});
</script>

<template>
  <base-drawer v-model:active="visible" title="成员列表" @cancel="handleCancel">
    <template #headerLeft>
      <div class="ml-[8px] flex flex-1 overflow-hidden text-[var(--color-text-4)]">
        {{ props.currentName }}
      </div>
    </template>
    <div>
      <div class="flex flex-row justify-between">
        <n-button v-if="hasAnyPermission(['ORGANIZATION_PROJECT:READ+ADD_MEMBER'])"
                  class="mr-3" @click="handleAddMember">
          添加成员
        </n-button>
        <n-input :value="keyword" placeholder="通过名称/邮箱/手机搜索" class="w-[230px]"/>
      </div>
      <base-card v-model:show="loading" class="mt-[16px]">
        <n-data-table
            :columns="columns"
            :data="data"
            :row-key="(row: UserListItem) => row.id"
        />
      </base-card>
    </div>
  </base-drawer>
  <add-user-modal :visible="userVisible"
                  :organization-id="props.organizationId"
                  :project-id="props.projectId"
                  :user-group-options="userGroupOptions"
                  is-organization
                  @submit="handleAddMemberSubmit"/>
</template>

<style scoped>

</style>