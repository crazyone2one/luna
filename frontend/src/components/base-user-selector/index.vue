<script setup lang="ts">
import {onMounted, ref, watch} from 'vue'
import {initOptionsFunc, UserRequestTypeEnum} from '/@/utils/common.ts'
import type {SelectOption} from 'naive-ui'

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
const innerValue = ref<string[]>([]);
const currentValue = defineModel<(string | number)[]>('modelValue', {default: []});
const options = ref<Array<SelectOption>>([])
const loadList = async () => {
  const {keyword, ...rest} = props.loadOptionParams;
  const list = (await initOptionsFunc(props.type, {keyword, ...rest})) || [];
  console.log(list)
  if (list.length > 0) {
    options.value = []
    list.forEach(u => options.value.push({'label': u.name, 'value': u.id, 'disabled': u.checkRoleFlag as boolean}))
  }
}
watch(
    () => innerValue.value,
    (value) => {
      const values: (string | number)[] = [];
      value.forEach((item) => {
        values.push(item);
      });
      currentValue.value = values;
    }
);
onMounted(() => {
  loadList()
})
</script>

<template>
  <n-select
      v-model:value="innerValue"
      filterable multiple
      placeholder="请选择成员"
      :options="options"
      clearable
  />
</template>

<style scoped>

</style>