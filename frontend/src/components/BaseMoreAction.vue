<script setup lang="ts">
import {type DropdownOption} from 'naive-ui';
import BaseIcon from '/@/components/BaseIcon.vue'

const props = defineProps<{
  options: DropdownOption[];
  trigger?: 'hover' | 'click' | 'focus' | 'manual'
}>();
const emit = defineEmits(['select', 'close', 'open']);
const handleSelect = (value: string) => {
  const item = props.options.find((e: DropdownOption) => e.key === value);
  emit('select', item);
}
</script>

<template>
  <n-dropdown :trigger="props.trigger || 'hover'" :options="options" @select="handleSelect">
    <base-icon type="more"/>
  </n-dropdown>
</template>

<style scoped>

</style>