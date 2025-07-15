<script setup lang="ts">
import BaseModal from '/@/components/BaseModal.vue'
import type {CreateOrUpdateSystemProjectParams, SystemOrgOption} from '/@/types/project.ts'
import {computed, ref, watchEffect} from 'vue'
import type {FormInst, FormRules} from 'naive-ui'
import BaseIcon from '/@/components/BaseIcon.vue'
import {useForm} from 'alova/client'
import {createOrUpdateProjectByOrg, getSystemOrgOption} from '/@/api/system/org-project.ts'
import {useAppStore} from '/@/store'
import type {UserSelectorOption} from '/@/types/user.ts'
import {initOptionsFunc, UserRequestTypeEnum} from '/@/utils/common.ts'

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const props = defineProps<{
  currentProject?: CreateOrUpdateSystemProjectParams;
}>();
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const isEdit = computed(() => !!(props.currentProject && props.currentProject.id));
const affiliatedOrgOption = ref<SystemOrgOption[]>([]);
const userOptions = ref<UserSelectorOption[]>([]);
const formRef = ref<FormInst | null>(null)
const appStore = useAppStore();
const currentOrgId = computed(() => appStore.currentOrgId);
const rules: FormRules = {
  name: [{
    required: true,
    trigger: ['blur', 'input'],
    message: '项目名称不能为空',
  }, {max: 255, message: '名称不能超过 255 个字符'}],
  userIds: {
    required: true,
    type: 'array',
    trigger: ['blur', 'input'],
    message: '项目管理员不能为空'
  },
}
const {form, reset, send} = useForm(form => createOrUpdateProjectByOrg(form), {
  immediate: false,
  initialForm: {
    // 项目名称
    name: '',
    num: '',
    // 项目描述
    description: '',
    // 启用或禁用
    enable: true,
    // 项目成员
    userIds: [],
    // 模块配置
    moduleSetting: [],
    // 所属组织
    organizationId: currentOrgId.value
  }
})
const handleSubmit = () => {
  console.log(form.value)
  send().then(() => {
    window.$message.success(isEdit.value ? '更新项目成功' : '创建项目成功')
    appStore.initProjectList()
    handleCancel(true);
  })
}
const handleCancel = (shouldSearch: boolean) => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
  emit('cancel', shouldSearch);
}
const initAffiliatedOrgOption = async () => {
  affiliatedOrgOption.value = await getSystemOrgOption();
}
const initUserOptions = async () => {
  const param = {organizationId: currentOrgId.value,}
  const res = await initOptionsFunc(UserRequestTypeEnum.ORGANIZATION_PROJECT_ADMIN, param);
  if (res) {
    userOptions.value = []
    res.map(u => u.name = u.email !== '' ? `${u.name}(${u.email})` : u.name)
    userOptions.value = res
  } else {
    userOptions.value = []
  }
}
watchEffect(() => {
  initAffiliatedOrgOption()
  initUserOptions()
})
</script>

<template>
  <base-modal v-model:show-modal="showModal" :is-edit="isEdit" @submit="handleSubmit" @cancel="handleCancel">
    <template #header>
      <span v-if="isEdit" class="flex">
        更新项目
        <div class="ml-[4px] flex text-[var(--color-text-4)]">
          (<div class="one-line-text max-w-[300px]">{{ props.currentProject?.name }}</div>)
        </div>
      </span>
      <span v-else>
        创建项目
      </span>
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
      <n-form-item label="项目名称" path="name">
        <n-input v-model:value="form.name" placeholder="请输入项目名称，不可与其他项目名称重复"/>
      </n-form-item>
      <n-form-item label="项目编号" path="num">
        <n-input v-model:value="form.num" placeholder="请输入项目编码，不可与其他项目编码重复"/>
      </n-form-item>
      <n-form-item label="所属组织" path="organizationId">
        <n-select v-model:value="form.organizationId" :options="affiliatedOrgOption" placeholder="请选择所属组织"
                  label-field="name" value-field="id"/>
      </n-form-item>
      <n-form-item label="项目管理员" path="userIds">
        <n-select v-model:value="form.userIds" :options="userOptions" multiple label-field="name" value-field="id"
                  placeholder="请选择项目管理员"/>
      </n-form-item>
      <n-form-item label="启用模块" path="module">
        <n-checkbox-group v-model:value="form.moduleSetting">
          <n-space item-style="display: flex;">
            <n-checkbox value="testPlan" label="测试计划"/>
            <n-checkbox value="caseManagement" label="测试用例"/>
          </n-space>
        </n-checkbox-group>
      </n-form-item>
      <n-form-item label="描述" path="description">
        <n-input v-model:value="form.description" type="textarea" placeholder="请对该项目进行描述"
                 :autosize="{
        minRows: 1
      }" clearable maxlength="1000"/>
      </n-form-item>
    </n-form>
    <template #actionLeft>
      <n-switch size="small" v-model:value="form.enable"/>
      <span>状态</span>
      <n-tooltip>
        <template #trigger>
          <base-icon type="fallback"/>
        </template>
        项目开启后，将展示在项目切换列表
      </n-tooltip>
    </template>
  </base-modal>
</template>

<style scoped>

</style>