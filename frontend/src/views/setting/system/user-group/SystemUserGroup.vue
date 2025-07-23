<script setup lang="ts">

import {hasAnyPermission} from '/@/utils/permission.ts'
import UserGroupLeft from '/@/components/user-group-comp/UserGroupLeft.vue'
import UserTable from '/@/components/user-group-comp/UserTable.vue'
import AuthTable from '/@/components/user-group-comp/AuthTable.vue'
import BaseCard from '/@/components/BaseCard.vue'
import {computed, nextTick, onMounted, provide, ref, useTemplateRef, watchEffect} from 'vue'
import {AuthScopeEnum} from '/@/enums/common.ts'
import type {CurrentUserGroupItem} from '/@/types/user-group.ts'
import {useRouter} from 'vue-router'

const router = useRouter();
const currentTable = ref('auth');
provide('systemType', AuthScopeEnum.SYSTEM);
const currentKeyword = ref('');
const userTableRef = useTemplateRef<InstanceType<typeof UserTable>>('userTable')
const authTableRef = useTemplateRef<InstanceType<typeof AuthTable>>('authTable')
const userGroupLeftRef = useTemplateRef<InstanceType<typeof UserGroupLeft>>('userGroupLeft')
const currentUserGroupItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  type: AuthScopeEnum.SYSTEM,
  internal: true,
});
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM);
const handleSelect = (item: CurrentUserGroupItem) => {
  currentUserGroupItem.value = item;
};
const tableSearch = () => {
  if (currentTable.value === 'user' && userTableRef.value) {
    userTableRef.value.fetchData();
  } else if (!userTableRef.value) {
    nextTick(() => {
      userTableRef.value?.fetchData();
    });
  }
}
const handleSearch = (value: string) => {
  currentKeyword.value = value;
  tableSearch()
}
watchEffect(() => {
  if (!couldShowUser.value) {
    currentTable.value = 'auth';
  } else {
    currentTable.value = 'auth';
  }
});
onMounted(() => {
  userGroupLeftRef.value?.initData(router.currentRoute.value.query.id as string, true)
})
</script>

<template>
  <base-card>
    <n-split direction="horizontal" :max="0.8" :min="0.2" :default-size="0.2">
      <template #1>
        <div class="mr-1">
          <user-group-left ref="userGroupLeftRef"
                           :add-permission="['SYSTEM_USER_ROLE:READ+ADD']"
                           :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                           :is-global-disable="true"
                           @handle-select="handleSelect"/>
        </div>
      </template>
      <template #2>
        <div>
          <div class="flex h-full flex-col overflow-hidden pt-[16px]">
            <div class="flex flex-row items-center justify-between px-[16px]">
              <n-radio-group v-if="couldShowUser" v-model:value="currentTable" name="systemUserGroup" class="mb-[16px]">
                <n-radio-button value="auth" class="p-[2px]">权限</n-radio-button>
                <n-radio-button value="user" class="p-[2px]">成员</n-radio-button>
              </n-radio-group>
              <div class="flex items-center">
                <n-input v-if="currentTable === 'user'" class="w-[240px]" placeholder="通过姓名/邮箱/手机搜索"
                         @clear="handleSearch('')"/>
              </div>
            </div>
          </div>
          <div class="flex-1 overflow-hidden">
            <user-table v-if="currentTable === 'user'" ref="userTableRef"
                        :current="currentUserGroupItem"
                        :keyword="currentKeyword"
                        :delete-permission="['SYSTEM_USER_ROLE:READ+DELETE']"
                        :read-permission="['SYSTEM_USER_ROLE:READ']"
                        :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"/>
            <auth-table v-if="currentTable === 'auth'" ref="authTableRef"
                        :current="currentUserGroupItem"
                        :save-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                        :disabled="!hasAnyPermission(['SYSTEM_USER_ROLE:READ+UPDATE'])"
            />
          </div>
        </div>
      </template>
    </n-split>
  </base-card>
</template>

<style scoped>

</style>