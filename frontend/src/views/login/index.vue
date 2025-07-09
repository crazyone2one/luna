<script setup lang="ts">
import {type FormInst, NH1, NH2} from 'naive-ui'
import {computed, ref} from 'vue'
import {fetchLogin} from '/@/api/auth'
import {useForm} from 'alova/client'
import {storage} from '/@/utils'
import {useRouter} from 'vue-router'

const title = import.meta.env.VITE_APP_TITLE
const rules = {
  username: {
    required: true,
    message: 'Username is required.',
    trigger: 'blur'
  },
  password: {
    required: true,
    message: 'Password is required.',
    trigger: 'blur'
  }
}

const router = useRouter()
const formRef = ref<FormInst | null>(null)
const {form, loading, send} = useForm((formData) => fetchLogin(formData),
    {
      initialForm: {
        username: '',
        password: ''
      },
      immediate: false
    })
const handleLogin = (e: Event) => {
  e.preventDefault()
  formRef.value?.validate(err => {
    if (!err) {
      send().then(async (res) => {
        const token = {
          access: res.access_token,
          refresh: '',
          expires: Date.now() + 3600000
        }
        storage.set('token', token)
        const route = router.currentRoute.value
        console.log(route)
        const redirect = route.query.redirect?.toString()
        console.log(redirect)
        await router.replace(redirect ?? route.redirectedFrom?.fullPath ?? '/')
      })
    }
  })
}
const disabled = computed<boolean>(() => form.value.username === '' || form.value.password === '')
</script>

<template>
  <n-h1 style="--font-size: 60px; --font-weight: 100">{{ title }}</n-h1>
  <n-card size="large" style="--padding-bottom: 30px">
    <n-h2 style="--font-weight: 400">Sign-in</n-h2>
    <n-form ref="formRef" size="large" :rules="rules" :model="form">
      <n-form-item path="username">
        <n-input v-model:value="form.username" placeholder="请输入用户名"/>
      </n-form-item>
      <n-form-item path="password">
        <n-input v-model:value="form.password" placeholder="请输入密码"/>
      </n-form-item>
    </n-form>
    <n-button type="primary" size="large" block :disabled="disabled" :loading="loading" @click="handleLogin">登录
    </n-button>
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