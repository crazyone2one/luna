<script setup lang="ts">
import {ref, watch} from 'vue'
import {useAppStore} from '/@/store'
import {fetchProjectList} from '/@/api/system/org-project.ts'
import type {IProjectItem} from '/@/types/project.ts'

const props = defineProps<{
  class?: string;
  useDefaultArrowIcon?: boolean;
}>();

const appStore = useAppStore();
const options = ref<IProjectItem[]>([])
const handleUpdateValue = (value: string) => {
  console.log(value)
}
const init = async () => {
  if (appStore.currentOrgId) {
    options.value = []
    options.value = await fetchProjectList(appStore.getCurrentOrgId);
  } else {
    options.value = [];
  }
}
watch(() => appStore.currentOrgId, () => {
  // init()
}, {immediate: true})

</script>

<template>
  <n-select
      :value="appStore.currentProjectId"
      :class="props.class || 'mr-[8px] w-[200px]'"
      filterable
      :options="options"
      label-field="name"
      value-field="id"
      @update:value="handleUpdateValue"
  >
    <template #action>
      <n-button class="mb-[4px] h-[28px] w-full justify-start pl-[7px] pr-0" text>新建项目</n-button>
    </template>
  </n-select>
</template>

<style scoped>

</style>