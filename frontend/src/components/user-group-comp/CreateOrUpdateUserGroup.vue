<script setup lang="ts">
import {inject, ref, watchEffect} from 'vue'
import type {
  BaseUserGroupParams,
  OrgUserGroupParams,
  SystemUserGroupParams,
  UserGroupItem
} from '/@/types/user-group.ts'
import type {FormInst, FormItemRule, FormRules} from 'naive-ui'
import {useForm} from 'alova/client'
import {AuthScopeEnum} from '/@/enums/common.ts'
import {updateOrAddOrgUserGroup, updateOrAddProjectUserGroup, updateOrAddUserGroup} from '/@/api/system/usergroup.ts'
import {useAppStore} from '/@/store'

const systemType = inject<String>('systemType');
const props = defineProps<{
  id?: string;
  list: UserGroupItem[];
  visible?: boolean;
  defaultName?: string;
  authScope?: string;
}>();
const formRef = ref<FormInst | null>(null)
const appStore = useAppStore();
const currentVisible = ref(props.visible);
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void;
  (e: 'submit', currentId: string): void;
}>();

const handleCancel = () => {
  form.value.name = '';
  emit('cancel', false);
};
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel()
  }
}
const rules: FormRules = {
  name: [{
    required: true, validator(_rule: FormItemRule, value: string) {
      if (value === undefined || value === '') {
        return new Error('用户组名称不能为空')
      } else {
        if (value === props.defaultName) {
          return true
        } else {
          const isExist = props.list.some((item) => item.name === value);
          if (isExist) {
            return new Error(`已有 ${value} ，请更改`)
          }
        }
        if (value.length > 255) {
          return new Error('名称不能超过 255 个字符')
        }
        return true
      }
    }
  }]
}
const {form, loading, send} = useForm((formData: BaseUserGroupParams) => {
      if (systemType === AuthScopeEnum.SYSTEM) {
        const systemParams: SystemUserGroupParams = {
          id: props.id,
          name: formData.name, // 使用formData而非form
          type: props.authScope
        };
        return updateOrAddUserGroup(systemParams)
      } else if (systemType === AuthScopeEnum.ORGANIZATION) {
        const orgParams: OrgUserGroupParams = {
          id: props.id,
          name: formData.name, // 使用formData而非form
          type: props.authScope,
          scopeId: appStore.currentOrgId,
        };
        return updateOrAddOrgUserGroup(orgParams)
      } else {
        const projectParams: SystemUserGroupParams = {
          name: formData.name
        };
        return updateOrAddProjectUserGroup(projectParams)
      }
    },
    {
      immediate: false,
      initialForm: {
        id: props.id,
        name: props.defaultName,
        scopeId: appStore.currentOrgId,
        type: props.authScope,
      } as BaseUserGroupParams
    })
const handleSubmit = (e: MouseEvent) => {
  e.preventDefault()
  formRef.value?.validate(err => {
    if (!err) {
      send().then(res => {
        if (res) {
          window.$message.success(props.id ? '更新用户组成功' : '添加用户组成功')
          emit('submit', res.id);
          handleCancel();
        }
      })
    }
  })
}
watchEffect(() => {
  currentVisible.value = props.visible;
  form.value.name = props.defaultName || '';
});
</script>

<template>
  <n-popover
      :show="currentVisible"
      placement="bottom"
      trigger="click"
      class="w-[350px]"
      :content-class="props.id ? 'move-left' : ''"
      @clickoutside="handleOutsideClick"
  >
    <template #trigger>
      <slot></slot>
    </template>
    <div>
      <n-form ref="formRef" :model="form" :rules="rules">
        <div class="mb-[8px] text-[14px] font-medium">
          {{ props.id ? '重命名' : '创建用户组' }}
        </div>
        <n-form-item path="name">
          <n-input v-model:value="form.name" :maxlength="255" placeholder="请输入用户组名称" clearable/>
          <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium">
            该用户组将在整个系统范围内可用
          </span>
        </n-form-item>
      </n-form>
    </div>
    <div class="flex flex-row flex-nowrap justify-end gap-2">
      <n-button size="tiny" @click="handleCancel">取消</n-button>
      <n-button type="primary" size="tiny" :disabled="form.name?.length===0"
                :loading="loading"
                @click="handleSubmit">
        {{ props.id ? '确认' : '创建' }}
      </n-button>
    </div>
  </n-popover>
</template>

<style scoped>
.move-left {
  position: relative;
  right: 22px;
}
</style>