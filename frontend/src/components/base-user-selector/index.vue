<script setup lang="ts">
import {onMounted, ref} from 'vue'
import type {IUserSelectorOption} from '/@/components/base-user-selector/types.ts'
import {initOptionsFunc, UserRequestTypeEnum} from '/@/utils/common.ts'

const props = withDefaults(
    defineProps<{
      disabled?: boolean; // 是否禁用
      disabledKey?: string; // 禁用的key
      valueKey?: string; // value的key
      placeholder?: string;
      firstLabelKey?: string; // 首要的的字段key
      secondLabelKey?: string; // 次要的字段key
      loadOptionParams: Record<string, any>; // 加载选项的参数
      type?: string; // 加载选项的类型
      atLeastOne?: boolean; // 是否至少选择一个
    }>(),
    {
      disabled: false,
      disabledKey: 'disabled',
      valueKey: 'id',
      firstLabelKey: 'name',
      secondLabelKey: 'email',
      type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
      atLeastOne: false,
    }
);
const innerValue = ref<IUserSelectorOption[]>([]);
const loadList = async () => {
  const {keyword, ...rest} = props.loadOptionParams;
  const list = (await initOptionsFunc(props.type, {keyword, ...rest})) || [];
  console.log(list)
}
onMounted(() => {
  console.log(props.type)
  loadList()
})
</script>

<template>
  <n-select
      v-model:value="innerValue"
      filterable multiple
      placeholder="请选择成员"
      :options="[]"
      clearable
  />
</template>

<style scoped>

</style>