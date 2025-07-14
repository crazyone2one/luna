<script setup lang="ts">
import {computed, useAttrs} from 'vue'

const props = withDefaults(
    defineProps<{
      tagList: Array<any>;
      showNum?: number;
      nameKey?: string;
      isStringTag?: boolean; // 是否是字符串数组的标签
      size?: 'small' | 'medium' | 'large';
      allowEdit?: boolean;
      showTable?: boolean;
      tagPosition?:
          | 'top'
          | 'tl'
          | 'tr'
          | 'bottom'
          | 'bl'
          | 'br'
          | 'left'
          | 'lt'
          | 'lb'
          | 'right'
          | 'rt'
          | 'rb'
          | undefined; // 提示位置防止窗口抖动
    }>(),
    {
      showNum: 2,
      nameKey: 'name',
      size: 'small',
      tagPosition: 'top',
    }
);
const emit = defineEmits<{
  (e: 'click'): void;
}>();
const filterTagList = computed(() => {
  return (props.tagList || []).filter((item: any) => item) || [];
});
const attrs = useAttrs();
const showTagList = computed(() => {
  // 在表格展示则全部展示，按照自适应去展示标签个数
  if (props.showTable) {
    return filterTagList.value;
  }
  return filterTagList.value.slice(0, props.showNum);
});
</script>

<template>
  <n-space v-if="showTagList.length" @click="emit('click')">
    <n-tag v-for="tag of showTagList" :key="tag.id" :size="size" v-bind="attrs">
      {{ props.isStringTag ? tag : tag[props.nameKey] }}
    </n-tag>
  </n-space>
  <!-- 避免在标签为空时，增大点击区域快速编辑 -->
  <div v-else :class="`tag-group-class ${props.allowEdit ? 'min-h-[24px] cursor-pointer' : ''}`" @click="emit('click')">
    -
  </div>
</template>

<style scoped>

</style>