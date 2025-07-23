<script setup lang="ts">
import {computed, h, inject, ref, type VNode, watchEffect} from 'vue'
import type {AuthTableItem, CurrentUserGroupItem, UserGroupAuthSetting} from '/@/types/user-group.ts'
import {type DataTableColumns, NButton, NCheckbox, NCheckboxGroup} from 'naive-ui'
import {AuthScopeEnum} from '/@/enums/common.ts'
import {fetchAuthByUserGroup, fetchGlobalUSetting, fetchOrgUSetting} from '/@/api/system/usergroup.ts'

const systemType = inject<string>('systemType');
const props = withDefaults(
    defineProps<{
      current: CurrentUserGroupItem;
      savePermission?: string[];
      showBottom?: boolean;
      disabled?: boolean;
      scroll?: {
        x?: number | string;
        y?: number | string;
        minWidth?: number | string;
        maxHeight?: number | string;
      };
    }>(),
    {
      showBottom: true,
      disabled: false,
    }
);
// 是否可以保存
const canSave = ref(false);
// 表格的总全选
const allChecked = ref(false);
const allIndeterminate = ref(false);
const tableData = ref<AuthTableItem[]>();
const systemAdminDisabled = computed(() => {
  const adminArr = ['admin', 'org_admin', 'project_admin'];
  const {id} = props.current;
  if (adminArr.includes(id)) {
    // 系统管理员,组织管理员，项目管理员都不可编辑
    return true;
  }

  return props.disabled;
});
const handleAllAuthChangeByCheckbox = () => {
  if (!tableData.value) return;
  allChecked.value = !allChecked.value;
  allIndeterminate.value = false;
  const tmpArr = tableData.value;
  tmpArr.forEach((item) => {
    item.enable = allChecked.value;
    item.indeterminate = false;
    item.perChecked = allChecked.value ? item.permissions?.map((ele) => ele.id) : [];
  });
  if (!canSave.value) canSave.value = true;
}
const columns = computed<DataTableColumns<AuthTableItem>>(() => {
  return [
    // {
    //   type: 'selection',
    // },
    {
      title: '功能',
      key: 'ability',
      width: 100
    },
    {
      title: '操作对象',
      key: 'operationObject',
      width: 150
    },
    {
      title: () => {
        return h('div', {class: 'flex w-full flex-row justify-between'}, {
          default: () => {
            const re = [h('div', null, {default: () => '权限'})]
            if (tableData.value && tableData.value?.length > 0) {
              re.push(
                  h(NCheckbox,
                      {
                        checked: allChecked.value,
                        disabled: systemAdminDisabled.value || props.disabled,
                        class: 'mr-[7px]',
                        indeterminate: allIndeterminate.value,
                        onUpdateChecked: () => handleAllAuthChangeByCheckbox()
                      },
                      {})
              )
            }
            return re
          }
        })
      },
      key: 'memberCount',
      render(row, rowIndex) {
        return h('div', {class: 'flex flex-row items-center justify-between'}, {
          default: () => {
            return [
              h(NCheckboxGroup, {
                value: row.perChecked,
                onUpdateValue: (v, e) => handleUpdateValue(v, rowIndex, row, e)
              }, {
                default: () => {
                  const res: VNode[] = []
                  row.permissions?.forEach(item => {
                    res.push(
                        h(NCheckbox, {value: item.id, label: item.name}, {})
                    )
                  })
                  return res
                }
              }),
              h(NCheckbox, {
                class: 'mr-[7px]', checked: row.enable, indeterminate: row.indeterminate,
                disabled: systemAdminDisabled.value || props.disabled,
                onUpdateChecked: (value: boolean) => handleRowAuthChange(value, rowIndex)
              }, {})
            ]
          }
        })
      }
    }
  ]
})
const handleUpdateValue = (_value: (string | number)[], rowIndex: number,
                           record: AuthTableItem,
                           e: {
                             actionType: 'check' | 'uncheck';
                             value: string | number;
                           }) => {
  setAutoRead(record, e.value as string);
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const length = tmpArr[rowIndex].permissions?.length || 0;
  if (record.perChecked?.length === length) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].indeterminate = false;
  } else if (record.perChecked?.length === 0) {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = false;
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = true;
  }
  handleAllChange();
}
const setAutoRead = (record: AuthTableItem, currentValue: string) => {
  if (!record.perChecked?.includes(currentValue)) {
    // 如果当前没有选中则执行自动添加查询权限逻辑
    // 添加权限值
    record.perChecked?.push(currentValue);
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    const lastEditStr = currentValue.split('+')[1]; // 编辑类权限通过+号拼接
    const existRead = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'READ'
    );
    const existCreate = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'ADD'
    );
    if (!existRead && postStr !== 'READ') {
      record.perChecked?.push(`${preStr}:READ`);
    }
    if (!existCreate && lastEditStr === 'IMPORT') {
      // 勾选导入时自动勾选新增和查询
      record.perChecked?.push(`${preStr}:ADD`);
      record.perChecked?.push(`${preStr}:READ+UPDATE`);
    }
  } else {
    // 删除权限值
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    if (postStr === 'READ') {
      // 当前是查询 那 移除所有相关的
      record.perChecked = record.perChecked.filter((item: string) => !item.includes(preStr));
    } else {
      record.perChecked.splice(record.perChecked.indexOf(currentValue), 1);
    }
  }
}
const handleRowAuthChange = (value: boolean | (string | number | boolean)[], rowIndex: number) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  tmpArr[rowIndex].indeterminate = false;
  if (value) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].perChecked = tmpArr[rowIndex].permissions?.map((item) => item.id);
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].perChecked = [];
  }
  tableData.value = [...tmpArr];
  handleAllChange();
  if (!canSave.value) canSave.value = true;
}
const transformData = (data: UserGroupAuthSetting[]) => {
  const result: AuthTableItem[] = [];
  data.forEach((item) => {
    result.push(...makeData(item));
  });
  return result;
}
const makeData = (item: UserGroupAuthSetting) => {
  const result: AuthTableItem[] = [];
  item.children?.forEach((child, index) => {
    if (!child.license) {
      const perChecked =
          child?.permissions?.reduce((acc: string[], cur) => {
            if (cur.enable) {
              acc.push(cur.id);
            }
            return acc;
          }, []) || [];
      const perCheckedLength = perChecked.length;
      let indeterminate = false;
      if (child?.permissions) {
        indeterminate = perCheckedLength > 0 && perCheckedLength < child?.permissions?.length;
      }
      result.push({
        id: child?.id,
        license: child?.license,
        enable: child?.enable,
        permissions: child?.permissions,
        indeterminate,
        perChecked,
        ability: index === 0 ? item.name : undefined,
        operationObject: child.name,
        rowSpan: index === 0 ? item.children?.length || 1 : undefined,
      });
    }
  });
  return result;
}
const initData = async (id: string) => {
  tableData.value = []; // 重置数据，可以使表格滚动条重新计算
  let res: UserGroupAuthSetting[] = [];
  if (systemType === AuthScopeEnum.SYSTEM) {
    res = await fetchGlobalUSetting(id);
  } else if (systemType === AuthScopeEnum.ORGANIZATION) {
    res = await fetchOrgUSetting(id);
  } else {
    res = await fetchAuthByUserGroup(id);
  }
  tableData.value = transformData(res);
  handleAllChange(true);
}
const handleAllChange = (isInit = false) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const {length: allLength} = tmpArr;
  const {length} = tmpArr.filter((item) => item.enable);
  if (length === allLength) {
    allChecked.value = true;
    allIndeterminate.value = false;
  } else if (length === 0) {
    allChecked.value = false;
    allIndeterminate.value = false;
  } else {
    allChecked.value = false;
    allIndeterminate.value = true;
  }
  if (!isInit && !canSave.value) canSave.value = true;
}
const handleReset = () => {
  if (props.current.id) {
    initData(props.current.id);
  }
}
watchEffect(() => {
  if (props.current.id) {
    initData(props.current.id)
  }
})
defineExpose({
  canSave,
  // handleSave,
  handleReset,
});
</script>

<template>
  <div class="flex h-full flex-col gap-[16px] overflow-hidden">
    <div class="group-auth-table">
      <n-data-table
          :columns="columns"
          :data="tableData"
          :row-key="(row: AuthTableItem) => row.id"
      />
    </div>
    <div v-if="props.showBottom && props.current.id !== 'admin' && !systemAdminDisabled"
         v-permission="props.savePermission || []"
         class="footer">
      <n-button :disabled="!canSave" @click="handleReset">撤销修改</n-button>
      <n-button v-permission="props.savePermission || []" type="primary" :disabled="!canSave">保存</n-button>
    </div>
  </div>
</template>

<style scoped>
.group-auth-table {
  @apply flex-1 overflow-hidden;

  padding: 0 16px 16px;
}

.footer {
  display: flex;
  justify-content: flex-end;
  box-shadow: 0 -1px 4px rgb(2 2 2 / 10%);
  gap: 16px;
}
</style>