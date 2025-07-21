<script setup lang="ts">
import {computed} from 'vue'
import {
  AddOutline as add,
  AlertCircleOutline as fallback,
  BuildOutline as tools,
  CaretDown,
  CaretUp,
  ChatbubbleEllipsesOutline as comments,
  ColorPaletteOutline as themes,
  CreateOutline as posts,
  EllipsisHorizontal as more,
  FlaskOutline as flask,
  HardwareChipOutline as plugins,
  HelpCircleOutline as help,
  ImagesOutline as media,
  LogoGithub as github,
  NewspaperOutline as pages,
  NotificationsOutline as notifications,
  PeopleOutline as users,
  RefreshOutline as refresh,
  SettingsOutline as settings,
  SpeedometerOutline as dashboard
} from '@vicons/ionicons5'
import {NIcon} from 'naive-ui'

const icons = {
  refresh,
  dashboard,
  posts,
  pages,
  media,
  users,
  comments,
  themes,
  plugins,
  tools,
  settings,
  flask,
  add,
  help,
  notifications,
  github,
  fallback, CaretUp, CaretDown, more
}

type IconTypes = keyof typeof icons
const props = defineProps({
  ...NIcon.props,
  type: {
    type: String,
    default: 'fallback'
  },

})
const icon = icons[props.type as IconTypes]
const formatLocalIcon = computed(() => {
  if (!props.localIcon) return ''
  if (!props.localIcon.startsWith('i-')) {
    return `i-${props.localIcon}`
  }
  return props.localIcon
})
</script>

<template>
  <div v-if="props.localIcon" :class="formatLocalIcon" :="$attrs"></div>
  <n-icon v-else v-bind="props">
    <icon/>
  </n-icon>
</template>

<style scoped>

</style>