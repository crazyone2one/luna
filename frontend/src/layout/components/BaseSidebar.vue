<script setup lang="ts">
import {h, ref, watchEffect} from 'vue'
import {type MenuOption} from 'naive-ui';
import {RouterLink, useRoute} from 'vue-router'
import BaseIcon from '/@/components/BaseIcon.vue'

const route = useRoute()
const collapsed = ref(false)
const activeMenu = ref<string>('')
const menuOptions: MenuOption[] = [
  {
    label: () => h(RouterLink, {to: {name: 'Home'}}, {default: () => 'Home'}),
    key: 'Home',
    icon: () => h(BaseIcon, {type: 'dashboard'},)
  },
  {
    label: '系统设置',
    key: 'setting',
    icon: () => h(BaseIcon, {type: 'settings'},),
    children: [
      {
        type: 'group',
        label: 'Organization',
        key: 'organization',
        children: [
          {
            label: () => h(RouterLink, {to: {name: 'settingOrganizationProject'}}, {default: () => '项目'}),
            key: 'settingOrganizationProject', path: '/settings/org-project',

          },
          {
            label: () => h(RouterLink, {to: {name: 'settingOrganizationUserGroup'}}, {default: () => '用户组'})
            , key: 'settingOrganizationUserGroup', path: '/settings/org-user-group'
          },
          {
            label: () => h(RouterLink, {to: {name: 'settingOrganizationTaskCenter'}}, {default: () => '任务中心'}),
            key: 'settingOrganizationTaskCenter', path: '/settings/taskCenter',
            icon: () => h(BaseIcon, {type: 'notifications'},),
          }
        ]
      },
      {
        type: 'group',
        label: 'System',
        key: 'system',
        children: [
          {
            label: () => h(RouterLink, {to: {name: 'settingSystemUser'}}, {default: () => '用户'}),
            key: 'settingSystemUser',
            icon: () => h(BaseIcon, {type: 'users'},)

          },
          {
            label: () => h(RouterLink, {to: {name: 'settingSystemUserGroup'}}, {default: () => '用户组'}),
            key: 'settingSystemUserGroup', path: '/settings/system-user-group'
          },
          {
            label: () => h(RouterLink, {to: {name: 'settingSystemTaskCenter'}}, {default: () => '任务中心'}),
            key: 'settingSystemTaskCenter', path: '/settings/taskCenter'
          },
          {
            label: () => h(RouterLink, {to: {name: 'settingSystemLog'}}, {default: () => '日志'}),
            key: 'settingSystemLog', path: '/settings/system/log'
          }
        ]
      }
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
      matchExpanded(menu.children)
    }
    if (routeMatched(menu)) {
      activeMenu.value = menu.key as string
      matched = true
    }
  })
  return matched
}
watchEffect(() => matchExpanded(menuOptions))
</script>

<template>
  <n-layout-sider :native-scrollbar="false" :collapsed-width="64"
                  :width="240" collapse-mode="width"
                  :collapsed="collapsed"
                  bordered
                  show-trigger="bar"
                  @collapse="collapsed = true"
                  @expand="collapsed = false">
    <router-link to="/" #="{ navigate, href }" custom>
      <!-- prettier-ignore -->
      <n-a class="logo" :href="href" @click="navigate">
        <svg v-if="collapsed" class="small" width="100" height="150" viewBox="0 0 100 150"
             xmlns="http://www.w3.org/2000/svg">
          <!-- 垂直部分 -->
          <rect x="10" y="10" width="30" height="120" fill="#9370DB"/>
          <!-- 水平部分 -->
          <rect x="10" y="100" width="80" height="30" fill="#9370DB"/>
        </svg>
        <svg v-else width="300" height="120" viewBox="0 0 300 120" xmlns="http://www.w3.org/2000/svg">
          <!-- 字母L - 蓝色 -->
          <rect x="10" y="20" width="25" height="80" fill="#1E90FF"/>
          <rect x="10" y="80" width="60" height="20" fill="#1E90FF"/>

          <!-- 字母U - 绿色 -->
          <rect x="80" y="20" width="25" height="70" fill="#32CD32"/>
          <rect x="130" y="20" width="25" height="70" fill="#32CD32"/>
          <rect x="80" y="90" width="75" height="20" fill="#32CD32"/>

          <!-- 字母N - 红色 -->
          <rect x="170" y="20" width="25" height="100" fill="#FF4500"/>
          <rect x="210" y="20" width="25" height="100" fill="#FF4500"/>
          <polygon points="195,20 210,20 195,120 170,120" fill="#FF4500"/>

          <!-- 字母A - 紫色 -->
          <polygon points="255,20 290,70 275,70 270,50 260,50 255,70 240,70" fill="#9370DB"/>
          <rect x="240" y="70" width="50" height="20" fill="#9370DB"/>
          <rect x="260" y="90" width="10" height="30" fill="#9370DB"/>
        </svg>
      </n-a>
    </router-link>
    <n-menu
        :value="activeMenu"
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        accordion
        @update:value="(k: string) => activeMenu = k"
    />
  </n-layout-sider>
</template>

<style scoped>
.logo {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  padding: 12px 20px;
  text-decoration: none;
}

.n-layout-sider--collapsed .logo {
  padding: 9px;
}

.logo svg {
  height: 32px;
}
</style>