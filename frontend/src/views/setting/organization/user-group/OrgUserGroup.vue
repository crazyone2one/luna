<script setup lang="ts">

import BaseCard from '/@/components/BaseCard.vue'
import {computed, onMounted, provide, ref, useTemplateRef, watchEffect} from 'vue'
import {AuthScopeEnum} from '/@/enums/common.ts'
import type {CurrentUserGroupItem} from '/@/types/user-group.ts'
import UserTable from '/@/components/user-group-comp/UserTable.vue'
import AuthTable from '/@/components/user-group-comp/AuthTable.vue'
import UserGroupLeft from '/@/components/user-group-comp/UserGroupLeft.vue'
import {useRouter} from 'vue-router'
import {hasAnyPermission} from '/@/utils/permission.ts'

const router = useRouter();
provide('systemType', AuthScopeEnum.ORGANIZATION);
const currentTable = ref('user');
const currentUserGroupItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  type: AuthScopeEnum.ORGANIZATION,
  internal: true,
});
const userTableRef = useTemplateRef<InstanceType<typeof UserTable>>('userTable')
const authTableRef = useTemplateRef<InstanceType<typeof AuthTable>>('authTable')
const userGroupLeftRef = useTemplateRef<InstanceType<typeof UserGroupLeft>>('userGroupLeft')
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.ORGANIZATION);
const couldShowAuth = computed(() => currentUserGroupItem.value.id !== 'admin');
const currentKeyword = ref('');
// const tableSearch = () => {
//   if (currentTable.value === 'user' && userTableRef.value) {
//     userTableRef.value.fetchData();
//   } else if (!userTableRef.value) {
//     nextTick(() => {
//       userTableRef.value?.fetchData();
//     });
//   }
// }
const handleSelect = (item: CurrentUserGroupItem) => {
  currentUserGroupItem.value = item;
};
watchEffect(() => {
  if (!couldShowAuth.value) {
    currentTable.value = 'user';
  } else if (!couldShowUser.value) {
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
                           :add-permission="['ORGANIZATION_USER_ROLE:READ+ADD']"
                           :update-permission="['ORGANIZATION_USER_ROLE:READ+UPDATE']"
                           :is-global-disable="true"
                           @handle-select="handleSelect"/>
        </div>
      </template>
      <template #2>
        <div>
          <div class="flex flex-row items-center justify-between p-[16px]">
            <n-radio-group v-if="couldShowUser && couldShowAuth" v-model:value="currentTable" name="radiobuttongroup1">
              <n-radio-button v-if="couldShowAuth" value="auth" class="p-[2px]">权限</n-radio-button>
              <n-radio-button v-if="couldShowUser" value="user" class="p-[2px]">成员</n-radio-button>
            </n-radio-group>
            <div class="flex items-center">
              <n-input v-if="currentTable === 'user'" class="w-[240px]" placeholder="通过姓名/邮箱/手机搜索"/>
            </div>
          </div>
          <div>
            <user-table v-if="currentTable === 'user' && couldShowUser" ref="userTableRef"
                        :current="currentUserGroupItem"
                        :keyword="currentKeyword"
                        :delete-permission="['ORGANIZATION_USER_ROLE:READ+DELETE']"
                        :read-permission="['ORGANIZATION_USER_ROLE:READ']"
                        :update-permission="['ORGANIZATION_USER_ROLE:READ+UPDATE']"/>
            <auth-table v-if="currentTable === 'auth' && couldShowAuth" ref="authTableRef"
                        :current="currentUserGroupItem"
                        :save-permission="['ORGANIZATION_USER_ROLE:READ+UPDATE']"
                        :disabled="!hasAnyPermission(['ORGANIZATION_USER_ROLE:READ+UPDATE']) || currentUserGroupItem.scopeId === 'global'"
            />
          </div>
        </div>
      </template>
    </n-split>
  </base-card>
</template>

<style scoped>

</style>