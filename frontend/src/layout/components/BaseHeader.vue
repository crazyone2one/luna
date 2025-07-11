<script setup lang="ts">
import {type DropdownOption} from 'naive-ui';
import BaseIcon from '/@/components/BaseIcon.vue'
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '/@/store'
import {clearToken} from '/@/utils/storage.ts';


const router = useRouter()
const userStore = useUserStore()
const options = computed(() => {
  return [
    {key: 'me', label: `Hey, ${userStore.userInfo.name}!`},
    {key: 'divider', type: 'divider'},
    {
      label: '用户资料',
      key: 'profile',
      // icon: renderIcon(UserIcon)
    },
    {key: 'settings', label: 'Settings'},
    {key: 'divider', type: 'divider'},
    {
      label: '退出登录',
      key: 'logout',
      // icon: renderIcon(LogoutIcon)
    }
  ]
})
const handleOptionsSelect = async (key: string, option: DropdownOption) => {
  if ((key) === 'logout') {
    clearToken()
    userStore.resetInfo()
    await router.push({name: 'Login'})
  } else if ((key as string) === 'me') {
    window.$message.success(`Welcome back, ${userStore.userInfo.name}!`)
  }
}
</script>

<template>
  <n-layout-header bordered>
    <n-button text>
      <base-icon type="refresh" size="20" :depth="2"/>
    </n-button>
    <n-space :size="20" align="center" style="line-height: 1">
      <n-tooltip>
        <template #trigger>
          <n-a href="https://github.com/crazyone2one/luna" target="_blank">
            <base-icon type="github" size="22" :depth="2"/>
          </n-a>
        </template>
        View on GitHub
      </n-tooltip>
      <n-dropdown :options="options" placement="bottom-end" show-arrow @select="handleOptionsSelect">
        <n-avatar size="small" round src="https://07akioni.oss-cn-beijing.aliyuncs.com/demo1.JPG"/>
      </n-dropdown>
    </n-space>
  </n-layout-header>
</template>

<style scoped>
.n-layout-header {
  position: sticky;
  top: 0;
  z-index: 1;
  display: flex;
  align-items: center;
  padding: 9px 18px;
}

.n-button {
  margin-right: 15px;
}

.n-space {
  margin-left: auto;
}
</style>