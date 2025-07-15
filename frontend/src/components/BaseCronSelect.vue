<script setup lang="ts">
withDefaults(defineProps<{ size: 'tiny' | 'small' | 'medium' | 'large' }>(), {
  size: 'medium'
})
const cron = defineModel<string>('modelValue', {
  required: true,
});
const options = [
  {label: '(每小时)', value: '0 0 0/1 * * ?'},
  {label: '(每 6 小时)', value: '0 0 0/6 * * ?'},
  {label: '(每 12 小时)', value: '0 0 0/12 * * ?'},
  {label: '(每天)', value: '0 0 0 * * ?'},
]
const emit = defineEmits<{
  (
      e: 'change',
      value: string
  ): void;
}>();
</script>

<template>
  <n-select
      v-model:value="cron"
      tag filterable
      placeholder='可直接输入表达式'
      :options="options"
      :size="size"
      @update:value="emit('change', $event)"
      :fallback-option="(val:string)=>({label: `${val}`,value: val})"
  />
</template>

<style scoped>

</style>