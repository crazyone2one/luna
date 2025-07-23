<script setup lang="ts">
import {computed, inject, onMounted, ref} from 'vue';

import BaseIcon from '/@/components/BaseIcon.vue'
import CreateOrUpdateUserGroup from '/@/components/user-group-comp/CreateOrUpdateUserGroup.vue'
import {AuthScopeEnum} from '/@/enums/common.ts'
import type {CurrentUserGroupItem, UserGroupItem} from '/@/types/user-group.ts'
import {hasAnyPermission} from '/@/utils/permission.ts'
import type {DropdownOption} from 'naive-ui'
import BaseMoreAction from '/@/components/BaseMoreAction.vue'
import {fetchOrgUserGroupList, fetchProjectUserGroupList, fetchUserGroupList} from '/@/api/system/usergroup.ts'
import {useAppStore} from '/@/store'

const systemType = inject<String>('systemType');
const props = defineProps<{
  addPermission: string[];
  updatePermission: string[];
  isGlobalDisable: boolean;
}>();
const showSystem = computed(() => systemType === AuthScopeEnum.SYSTEM);
const showOrg = computed(() => systemType === AuthScopeEnum.SYSTEM || systemType === AuthScopeEnum.ORGANIZATION);
const showProject = computed(() => systemType === AuthScopeEnum.SYSTEM || systemType === AuthScopeEnum.PROJECT);
// 系统用户组Toggle
const systemToggle = ref(true);
// 组织用户组Toggle
const orgToggle = ref(true);
// 项目用户组Toggle
const projectToggle = ref(true);
// 用户组列表
const userGroupList = ref<UserGroupItem[]>([]);
// 系统用户组列表
const systemUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM);
});
// 组织用户组列表
const orgUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.ORGANIZATION);
});
// 项目用户组列表
const projectUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.PROJECT);
});
// 系统用户创建用户组visible
const systemUserGroupVisible = ref(false);
// 组织用户创建用户组visible
const orgUserGroupVisible = ref(false);
// 项目用户创建用户组visible
const projectUserGroupVisible = ref(false);
const emit = defineEmits<{
  (e: 'handleSelect', element: UserGroupItem): void;
  (e: 'addUserSuccess', id: string): void;
}>();
const isSystemShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'SYSTEM_USER_ROLE:READ+DELETE']);
});
const isOrdShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'ORGANIZATION_USER_ROLE:READ+DELETE']);
});
const isProjectShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'PROJECT_GROUP:READ+DELETE']);
});
const currentId = ref('');
const currentItem = ref<CurrentUserGroupItem>({id: '', name: '', internal: false, type: AuthScopeEnum.SYSTEM});
const systemMoreAction: DropdownOption[] = [
  {
    label: '重命名',
    key: 'rename',
    permission: props.updatePermission,
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: '删除',
    key: 'delete',
    permission: ['SYSTEM_USER_ROLE:READ+DELETE'],
  }
]
const orgMoreAction: DropdownOption[] = [
  {
    label: '重命名',
    key: 'rename',
    permission: props.updatePermission,
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: '删除',
    key: 'delete',
    permission: ['ORGANIZATION_USER_ROLE:READ+DELETE'],
  }
]
const projectMoreAction: DropdownOption[] = [
  {
    label: '重命名',
    key: 'rename',
    permission: props.updatePermission,
  },
  {
    key: 'header-divider',
    type: 'divider'
  },
  {
    label: '删除',
    key: 'delete',
    permission: ['PROJECT_GROUP:READ+DELETE'],
  }
]
const appStore = useAppStore()

const initData = async (id?: string, isSelect = true) => {
  let res: UserGroupItem[] = [];
  if (systemType === AuthScopeEnum.SYSTEM && hasAnyPermission(['SYSTEM_USER_ROLE:READ'])) {
    res = await fetchUserGroupList();
  } else if (systemType === AuthScopeEnum.ORGANIZATION && hasAnyPermission(['ORGANIZATION_USER_ROLE:READ'])) {
    res = await fetchOrgUserGroupList(appStore.currentOrgId);
  } else if (systemType === AuthScopeEnum.PROJECT && hasAnyPermission(['PROJECT_GROUP:READ'])) {
    res = await fetchProjectUserGroupList(appStore.currentProjectId);
  }
  if (res.length > 0) {
    userGroupList.value = res
    if (isSelect) {
      // leftCollapse 切换时不重复数据请求
      if (id) {
        const item = res.find((i) => i.id === id);
        if (item) {
          handleListItemClick(item);
        } else {
          window.$message.warning('资源已被删除')
          handleListItemClick(res[0]);
        }
      } else {
        handleListItemClick(res[0]);
      }
    }
  }
}
const handleListItemClick = (element: UserGroupItem) => {
  const {id, name, type, internal} = element;
  currentItem.value = {id, name, type, internal};
  currentId.value = id;
  emit('handleSelect', element);
}
const handleCreateUG = (scoped: String) => {
  if (scoped === AuthScopeEnum.SYSTEM) {
    systemUserGroupVisible.value = true;
  } else if (scoped === AuthScopeEnum.ORGANIZATION) {
    orgUserGroupVisible.value = true;
  } else if (scoped === AuthScopeEnum.PROJECT) {
    projectUserGroupVisible.value = true;
  }
};
const keyword = ref('')
const handleSearch = () => {
  console.log(keyword.value)
}
const handleCreateUserGroup = (id: string) => {
  initData(id)
}
defineExpose({
  initData,
});
onMounted(() => {
  initData()
})
</script>

<template>
  <div class="flex flex-col px-[16px] pb-[16px]">
    <div class="sticky top-0 z-[999] pb-[8px] pt-[16px]">
      <n-input v-model:value="keyword" clearable placeholder="请输入用户组名称" @keyup.enter.native="handleSearch"/>
    </div>
    <div v-if="showSystem" v-permission="['SYSTEM_USER_ROLE:READ']" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-gray">
          <base-icon v-if="systemToggle" class="cursor-pointer" type="CaretDown" @click="systemToggle = false"/>
          <base-icon v-else class="cursor-pointer" type="CaretUp" @click="systemToggle = true"/>
          <div class="text-[14px]">
            系统用户组
          </div>
        </div>
        <create-or-update-user-group :list="systemUserGroupList" :visible="systemUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.SYSTEM">
          <n-tooltip>
            <template #trigger>
              <base-icon class="cursor-pointer text-[rgb(64, 128, 255)] hover:text-[rgb(106,161,255)]"
                         type="add"
                         @click="handleCreateUG(AuthScopeEnum.SYSTEM)"/>
            </template>
            <span>创建系统用户组</span>
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <transition>
        <div v-if="systemToggle">
          <div v-for="element in systemUserGroupList" :key="element.id" class="list-item"
               @click="handleListItemClick(element)">
            <create-or-update-user-group :list="systemUserGroupList">
              <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                <div class="list-item-name one-line-text"
                     :class="{ '!text-blue-500': element.id === currentId }">
                  {{ element.name }}
                </div>
                <div v-if="element.type === systemType ||
                    (isSystemShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      systemMoreAction.length > 0)"
                     class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                     :class="{ '!opacity-100': element.id === currentId }"
                >
                  <div v-if="element.type === systemType" class="icon-button">
                    <base-icon type="add"/>
                  </div>
                  <base-more-action v-if="
                      isSystemShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      systemMoreAction.length > 0
                    " :options="systemMoreAction"/>
                </div>
              </div>
            </create-or-update-user-group>
          </div>
          <n-divider class="my-[0px] mt-[6px]"/>
        </div>
      </transition>
    </div>
    <div v-if="showOrg" v-permission="['ORGANIZATION_USER_ROLE:READ']" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1">
          <base-icon v-if="orgToggle" class="cursor-pointer" type="CaretDown" size="12" @click="orgToggle = false"/>
          <base-icon v-else class="cursor-pointer" type="CaretUp" size="12" @click="orgToggle = true"/>
          <div class="text-[14px]">
            组织用户组
          </div>
        </div>
        <create-or-update-user-group :list="orgUserGroupList"
                                     :visible="orgUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.ORGANIZATION"
                                     @cancel="orgUserGroupVisible = false"
                                     @submit="handleCreateUserGroup">
          <n-tooltip>
            <template #trigger>
              <base-icon class="cursor-pointer text-[rgb(64, 128, 255)] hover:text-[rgb(106,161,255)]"
                         type="add" size="20"
                         @click="orgUserGroupVisible=true"/>
            </template>
            <span>创建组织用户组</span>
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="orgToggle">
          <div v-for="element in orgUserGroupList" :key="element.id" class="list-item"
               @click="handleListItemClick(element)">
            <create-or-update-user-group :list="orgUserGroupList"
            >
              <div class="flex w-full grow flex-row items-center justify-between">
                <div class="flex w-[calc(100%-56px)] items-center gap-[4px]">
                  <div class="list-item-name one-line-text"
                       :class="{ '!text-blue-500': element.id === currentId }">
                    {{ element.name }}
                  </div>
                  <div v-if="systemType === AuthScopeEnum.ORGANIZATION"
                       class="one-line-text ml-1 text-gray-500">
                    {{
                      `(${element.internal ? '系统内置' : element.scopeId === 'global' ? '系统自定义' : '自定义'})`
                    }}
                  </div>
                </div>
                <div v-if="element.type === systemType ||
                    (isOrdShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      orgMoreAction.length > 0)"
                     class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                     :class="{ '!opacity-100': element.id === currentId }"
                >
                  <div v-if="element.type === systemType" class="icon-button">
                    <base-icon type="add" size="16"/>
                  </div>
                  <base-more-action v-if="
                      isOrdShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      orgMoreAction.length > 0
                    " :options="orgMoreAction"/>
                </div>
              </div>
            </create-or-update-user-group>
          </div>
          <n-divider v-if="showSystem" class="my-[0px] mt-[6px]"/>
        </div>
      </Transition>
    </div>
    <div v-if="showProject" v-permission="['PROJECT_GROUP:READ']" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-[var(--color-text-4)]">
          <base-icon v-if="projectToggle" class="cursor-pointer" type="CaretDown" @click="projectToggle = false"/>
          <base-icon v-else class="cursor-pointer" type="CaretUp" @click="projectToggle = true"/>
          <div class="text-[14px]">
            项目用户组
          </div>
        </div>
        <create-or-update-user-group :list="projectUserGroupList"
                                     :visible="projectUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.PROJECT">
          <n-tooltip>
            <template #trigger>
              <base-icon class="cursor-pointer" type="add" @click="projectUserGroupVisible=true"/>
            </template>
            <span>创建项目用户组</span>
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="projectToggle">
          <div
              v-for="element in projectUserGroupList"
              :key="element.id"
              class="list-item"
              :class="{ '!bg-[rgb(var(--primary-1))]': element.id === currentId }"
              @click="handleListItemClick(element)"
          >
            <create-or-update-user-group :list="projectUserGroupList">
              <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                <div :class="`list-item-name one-line-text   ${
                                          systemType === AuthScopeEnum.ORGANIZATION ? 'max-w-[calc(100%-86px)]' : 'w-full'
                                        } ${element.id === currentId ? 'text-blue-500' : ''}`">
                  {{ element.name }}
                </div>
                <div
                    v-if="
                    element.type === systemType ||
                    (isProjectShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      projectMoreAction.length > 0)
                  "
                    class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                    :class="{ '!opacity-100': element.id === currentId }"
                >
                  <div v-if="element.type === systemType" class="icon-button">
                    <base-icon type="add"/>
                  </div>
                  <base-more-action v-if="
                      isProjectShowAll &&
                      !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      projectMoreAction.length > 0
                    "
                                    :options="projectMoreAction">

                  </base-more-action>
                </div>
              </div>
            </create-or-update-user-group>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.icon-button {
  display: flex;
  box-sizing: border-box;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

.list-item {
  padding: 7px 4px 7px 20px;
  height: 38px;
  @apply flex cursor-pointer items-center hover:bg-blue-500;

  &:hover .list-item-action {
    opacity: 1;
  }
}
</style>