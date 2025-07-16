<script setup lang="ts">
import {watch} from 'vue'
import {useAppStore, useUserStore} from '/@/store'
import {useRouter} from 'vue-router'
import BaseIcon from '/@/components/BaseIcon.vue'
import {switchProject} from '/@/api/system/org-project.ts'
import {useRequest} from 'alova/client'

const props = defineProps<{
  class?: string;
  useDefaultArrowIcon?: boolean;
}>();
const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();
const {send: handleSwitchProject} = useRequest((param) => switchProject(param), {immediate: false})
const handleUpdateValue = (value: string) => {
  handleSwitchProject({
    projectId: value,
    userId: userStore.id || '',
  }).then(res => {
    userStore.setInfo(res.userInfo)
    appStore.setCurrentOrgId(res.userInfo.lastOrganizationId)
    appStore.setCurrentProjectId(res.userInfo.lastProjectId)
  });
  router.replace({
    name: router.getRoutes()[0].name,
    query: {
      orgId: appStore.currentOrgId,
      pId: value,
    },
  })
}
watch(() => appStore.currentOrgId, () => {
  appStore.initProjectList()
}, {immediate: true})

</script>

<template>
  <n-select
      :value="appStore.currentProjectId"
      :class="props.class || 'mr-[8px] w-[200px]'"
      filterable
      :options="appStore.projectList"
      label-field="name"
      value-field="id"
      @update:value="handleUpdateValue"
  >
    <template #action>
      <n-button class="mb-[4px] h-[28px] w-full justify-start pl-[7px] pr-0" text disabled>
        <template #icon>
          <base-icon type="add"/>
        </template>
        新建项目
      </n-button>
    </template>
  </n-select>
</template>

<style scoped>

</style>