<script setup lang="ts">
import {h, ref, watchEffect} from 'vue'
import {type MenuOption} from 'naive-ui';
import {RouterLink, useRoute} from 'vue-router'
import BaseIcon from '/@/components/BaseIcon.vue'

const route = useRoute()
const collapsed = ref(false)
const activeMenu = ref<string>('')
const expandedKeys = ref<string[]>([])
const menuOptions: MenuOption[] = [
  {
    label: () => h(RouterLink, {to: {name: 'Home'}}, {default: () => 'Home'}),
    key: 'home',
    icon: () => h(BaseIcon, {type: 'dashboard'},)
  },
  {
    label: () => h(RouterLink, {to: {name: 'UserList'}}, {default: () => '用户管理'}),
    key: 'user',
    icon: () => h(BaseIcon, {type: 'users'},)

  },
  {
    label: '系统设置',
    key: 'settings',
    children: [
      {label: '基础设置', key: 'settings-base', path: '/settings/base'},
      {label: '安全设置', key: 'settings-security', path: '/settings/security'}
    ]
  }
]
const routeMatched = (menu: MenuOption): boolean => {
  return route.name === menu.key && (menu.params == null || JSON.stringify(route.params) === JSON.stringify(menu.params))
}
const matchExpanded = (menus: MenuOption[]): boolean => {
  let matched = false
  menus.forEach(menu => {
    if (menu.children != null) {
      matchExpanded(menu.children) && expandedKeys.value.push(menu.key as string)
    }
    if (routeMatched(menu)) {
      activeMenu.value = menu.key as string
      matched = true
    }
  })
  return matched
}
watchEffect(() =>  matchExpanded(menuOptions))
</script>

<template>
  <n-layout-sider :native-scrollbar="false" :collapsed-width="64"
                  :width="240" collapse-mode="width" :collapsed="collapsed" @collapse="collapsed = true"
                  @expand="collapsed = false">
    <div class="logo" :class="{ collapsed: collapsed }">
      <img v-if="!collapsed" src="/@/assets/vue.svg" alt="Logo"/>
      <span v-if="!collapsed">Vue3 Admin</span>
      <span v-else>A</span>
    </div>
    <n-menu
        :value="activeMenu"
        :default-expanded-keys="expandedKeys"
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        @update:value="
        (k: string) => {
          activeMenu = k
        }
      "
    />
  </n-layout-sider>
</template>

<style scoped>

</style>