<script setup lang="ts">

import BaseModal from '/@/components/BaseModal.vue'
import {onBeforeMount, ref} from 'vue'
import type {FormInst, FormRules} from 'naive-ui'
import {useForm} from 'alova/client'
import {fetchCreateUser, getSystemRoles} from '/@/api/system/user.ts'
import type {SystemRole} from '/@/types/user.ts'

// type UserModalMode = 'create' | 'edit';
const emit = defineEmits(['reload'])
// const userFormMode = ref<UserModalMode>('create');
const title = ref('创建用户')
const userGroupOptions = ref<SystemRole[]>([]);
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
  name: [
    {required: true, trigger: ['blur', 'input'], message: '姓名不能为空'},
    {min: 2, max: 50, message: '名称不能超过 50 个字符'}
  ],
  email: {required: true, trigger: ['blur', 'input'], message: '邮箱不能为空'},
}
const {form, reset, send} = useForm(form => fetchCreateUser(form), {
  immediate: false,
  initialForm: {
    name: '',
    email: '',
    userRoleIdList: [] as string[],
    phone: ''
  }
})
const handleSubmit = () => {
  formRef.value?.validate(e => {
    if (!e) {
      send().then(() => {
        showModal.value = false
        window.$message.success('创建成功')
        emit('reload')
      })
    }
  })
}
const handleCancel = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
  form.value.userRoleIdList = userGroupOptions.value.filter(e => e.selected).map(o => o.id)
}
const init = async () => {
  userGroupOptions.value = await getSystemRoles();
  if (userGroupOptions.value.length) {
    form.value.userRoleIdList = userGroupOptions.value.filter((e: SystemRole) => e.selected).map(o => o.id);
  }
}
onBeforeMount(() => {
  init()
})
</script>

<template>
  <base-modal v-model:show-modal="showModal" @submit="handleSubmit" @cancel="handleCancel">
    <template #header>
      <span>{{ title }}</span>
    </template>
    <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
        size="small"
    >
      <n-form-item label="姓名" path="name">
        <n-input v-model:value="form.name" placeholder="请输入用户姓名"/>
      </n-form-item>
      <n-form-item label="邮箱" path="email">
        <n-input v-model:value="form.email" placeholder="请输入邮箱地址"/>
      </n-form-item>
      <n-form-item label="电话号码" path="phone">
        <n-input v-model:value="form.phone" placeholder="请输入电话号码"/>
      </n-form-item>
      <!--      <n-form-item label="密码" path="password">-->
      <!--        <n-input v-model:value="form.password" placeholder="请输入密码"/>-->
      <!--      </n-form-item>-->
      <n-form-item label="用户组" path="userGroup">
        <n-select v-model:value="form.userRoleIdList" filterable multiple
                  :options="userGroupOptions" label-field="name" value-field="id" placeholder="请选择用户组"/>
      </n-form-item>
    </n-form>
  </base-modal>
</template>

<style scoped>

</style>