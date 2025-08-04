import type {
    AlovaXHRResponse,
    BatchActionQueryParams,
    ICommonPage,
    ICommonResponse,
    ImportResult,
    ITableQueryParams
} from '/@/types/common.ts'
import {alovaInstance, xhrInst} from '/@/api'
import type {addOrUpdateUserForm, SystemRole, UpdateUserInfoParams, UserListItem} from '/@/types/user.ts'

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
/**
 * 更新用户信息
 * @param data
 */
export const updateUserInfo = (data: UpdateUserInfoParams) => {
    return alovaInstance.Post('/system/user/update', data)
}

/**
 * 下载导入模板
 */
export const getUserTemplate = () => {
    return xhrInst.Get<AlovaXHRResponse>('/system/user/templates/download',
        {responseType: 'blob'}
    )
}

/**
 * 导入user
 * @param data
 */
export function importUserInfo(data: File) {
    const formData = new FormData();
    formData.append('file', data);
    return xhrInst.Post<AlovaXHRResponse<ICommonResponse<ImportResult>>>('/system/user/import', formData,
        {responseType: 'json'}
    );
}