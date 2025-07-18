<script setup lang="ts">
withDefaults(defineProps<{ size?: 'tiny' | 'small' | 'medium' | 'large', placeholder?: string }>(), {
  size: 'medium',
  placeholder: '请选择'
})
const type = defineModel<string>('modelValue', {
  required: true,
});
const options = [
  {label: '环境瓦斯', value: '0001'},
  {label: '风速', value: '0002', disabled: true},
  {label: '环境温度', value: '0003', disabled: true},
  {label: '一氧化碳', value: '0004'},
  {label: '风压', value: '0005', disabled: true},
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
      v-model:value="type"
      filterable
      :placeholder='placeholder'
      :options="options"
      :size="size"
      @update:value="emit('change', $event)"
      :fallback-option="(val:string)=>({label: `${val}`,value: val})"
  />
</template>

<style scoped>

</style>