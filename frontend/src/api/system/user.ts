import type {BatchActionQueryParams, ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import {alovaInstance} from '/@/api'
import type {addOrUpdateUserForm, SystemRole, UserListItem} from '/@/types/user.ts'

export const fetchUserPage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>('/system/user/page', data)
}
export const fetchCreateUser = (data: addOrUpdateUserForm) => {
    return alovaInstance.Post('/system/user/save', data)
}

export const getSystemRoles = () => {
    return alovaInstance.Get<SystemRole[]>('/system/user/get/global/system/role')
}
/**
 * 删除用户
 * @param data
 */
export const deleteUserInfo = (data: BatchActionQueryParams) => {
    return alovaInstance.Post('/system/user/delete', data)
}
/**
 * 重置用户密码
 * @param data
 */
export const resetUserPassword = (data: BatchActionQueryParams) => {
    return alovaInstance.Post('/system/user/reset/password', data)
}