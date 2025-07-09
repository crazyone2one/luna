<script setup lang="ts">
import {NH1, NH2} from 'naive-ui'

const title = import.meta.env.VITE_APP_TITLE
</script>

<template>
  <n-h1 style="--font-size: 60px; --font-weight: 100">{{ title }}</n-h1>
  <n-card size="large" style="--padding-bottom: 30px">
    <n-h2 style="--font-weight: 400">Sign-in</n-h2>
    <n-form ref="formRef" size="large">
      <n-form-item>
        <n-input placeholder="请输入用户名"/>
      </n-form-item>
      <n-form-item>
        <n-input placeholder="请输入密码"/>
      </n-form-item>
    </n-form>
    <n-button type="primary" size="large" block>登录</n-button>
    <br/>
  </n-card>
</template>

<style scoped>
.n-h1 {
  margin: 20vh auto 20px;
  text-align: center;
  letter-spacing: 5px;
  opacity: 0.8;
}

.n-card {
  margin: 0 auto;
  max-width: 380px;
  box-shadow: var(--box-shadow);
}
</style>