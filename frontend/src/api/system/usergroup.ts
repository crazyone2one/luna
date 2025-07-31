import {alovaInstance} from '/@/api'
import type {
    OrgUserGroupParams,
    SaveGlobalUSettingData,
    SystemUserGroupParams,
    UserGroupAuthSetting,
    UserGroupItem
} from '/@/types/user-group.ts'
import type {ICommonPage, ISelectOption, ITableQueryParams} from '/@/types/common.ts'
import type {UserListItem} from '/@/types/user.ts'
import type {IUserSelectorOption} from '/@/components/base-user-selector/types.ts'

/**
 * 系统-获取用户组列表
 */
export const fetchUserGroupList = () => {
    return alovaInstance.Get<UserGroupItem[]>('/user/role/global/list', {cacheFor: 600})
}
/**
 * 组织-获取用户组列表
 * @param organizationId
 */
export const fetchOrgUserGroupList = (organizationId: string) => {
    return alovaInstance.Get<UserGroupItem[]>(`/user/role/organization/list/${organizationId}`, {cacheFor: 600})
}
export const fetchProjectUserGroupList = (projectId: string) => {
    return alovaInstance.Get<UserGroupItem[]>(`/user/role/organization/list/${projectId}`, {cacheFor: 600})
}
/**
 * 系统-获取用户组对应的权限配置
 * @param id
 */
export const fetchGlobalUSetting = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/global/permission/setting/${id}`, {cacheFor: 600})
}
/**
 * 组织-获取用户组对应的权限配置
 * @param id
 */
export const fetchOrgUSetting = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/organization/permission/setting/${id}`, {cacheFor: 600})
}
/**
 * 项目-获取用户组对应的权限
 * @param id
 */
export const fetchAuthByUserGroup = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/project/permission/setting/${id}`, {cacheFor: 600})
}
/**
 * 系统-获取用户组对应的用户列表
 * @param data
 */
export const fetchUserByUserGroup = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>(`/user/role/relation/global/list`, data)
}
/**
 * 组织-根据用户组获取用户列表
 * @param data
 */
export const fetchOrgUserByUserGroup = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>(`/user/role/organization/list-member`, data)
}
/**
 * 系统-获取需要关联的用户选项
 * @param id
 * @param keyword
 */
export const getSystemUserGroupOption = (id: string, keyword: string) => {
    return alovaInstance.Get<IUserSelectorOption[]>(`/user/role/relation/global/user/option/${id}`, {params: {keyword}})
}
/**
 * 组织-获取需要关联的用户选项
 * @param organizationId
 * @param roleId
 * @param keyword
 */
export const getOrgUserGroupOption = (organizationId: string, roleId: string, keyword: string) => {
    return alovaInstance.Get<IUserSelectorOption[]>(`/user/role/organization/get-member/option/${organizationId}/${roleId}`,
        {params: {keyword}, cacheFor: 600})
}
/**
 * 系统-添加用户到用户组
 * @param data
 */
export const addUserToUserGroup = (data: { roleId: string; userIds: string[] }) => {
    return alovaInstance.Post<string>(`/user/role/relation/global/add`, data)
}
export const addOrgUserToUserGroup = (data: { userRoleId: string; userIds: string[]; organizationId: string }) => {
    return alovaInstance.Post<string>(`/user/role/organization/add-member`, data)
}
/**
 * 系统-删除用户组对应的用户
 * @param id
 */
export const deleteUserFromUserGroup = (id: string) => {
    return alovaInstance.Get<string>(`/user/role/relation/global/delete/${id}`)
}
/**
 * 组织-删除用户组对应的用户
 * @param data
 */
export const deleteOrgUserFromUserGroup = (data: { userRoleId: string; userIds: string[]; organizationId: string }) => {
    return alovaInstance.Post<string>(`/user/role/organization/remove-member`, data)
}
/**
 * 系统-创建或修改用户组
 * @param data
 */
export const updateOrAddUserGroup = (data: SystemUserGroupParams) => {
    return alovaInstance.Post<UserGroupItem>(data.id ? '/user/role/global/update' : `/user/role/global/save`, data)
}
/**
 * 组织-创建或修改用户组
 * @param data
 */
export const updateOrAddOrgUserGroup = (data: OrgUserGroupParams) => {
    return alovaInstance.Post<UserGroupItem>(data.id ? '/user/role/organization/update' : `/user/role/organization/save`, data)
}
/**
 * 项目-创建或修改用户组
 * @param data
 */
export const updateOrAddProjectUserGroup = (data: SystemUserGroupParams) => {
    return alovaInstance.Post<UserGroupItem>(data.id ? '/user/role/project/update' : `/user/role/project/add`, data)
}

export const getGlobalUserGroup = (organizationId: string) => {
    return alovaInstance.Get<Array<ISelectOption>>(`/organization/user/role/list/${organizationId}`)
}
/**
 * 系统-编辑用户组对应的权限配置
 * @param data
 */
export const saveGlobalUSetting = (data: SaveGlobalUSettingData) => {
    return alovaInstance.Post<Array<UserGroupAuthSetting>>('/user/role/global/permission/update', data)
}
/**
 * 组织-编辑用户组对应的权限配置
 * @param data
 */
export const saveOrgUSetting = (data: SaveGlobalUSettingData) => {
    return alovaInstance.Post<Array<UserGroupAuthSetting>>('/user/role/organization/permission/update', data)
}
/**
 * 项目-编辑用户组对应的权限配置
 * @param data
 */
export const saveProjectUGSetting = (data: SaveGlobalUSettingData) => {
    return alovaInstance.Post<Array<UserGroupAuthSetting>>('/user/role/project/permission/update', data)
}