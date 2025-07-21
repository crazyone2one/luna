import {alovaInstance} from '/@/api'
import type {UserGroupAuthSetting, UserGroupItem} from '/@/types/user-group.ts'

/**
 * 系统-获取用户组列表
 */
export const fetchUserGroupList = () => {
    return alovaInstance.Get<UserGroupItem[]>('/user/role/global/list')
}
/**
 * 组织-获取用户组列表
 * @param organizationId
 */
export const fetchOrgUserGroupList = (organizationId: string) => {
    return alovaInstance.Get<UserGroupItem[]>(`/user/role/organization/list/${organizationId}`)
}
export const fetchProjectUserGroupList = (projectId: string) => {
    return alovaInstance.Get<UserGroupItem[]>(`/user/role/organization/list/${projectId}`)
}
/**
 * 系统-获取用户组对应的权限配置
 * @param id
 */
export const fetchGlobalUSetting = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/global/permission/setting/${id}`)
}
/**
 * 组织-获取用户组对应的权限配置
 * @param id
 */
export const fetchOrgUSetting = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/organization/permission/setting/${id}`)
}
/**
 * 项目-获取用户组对应的权限
 * @param id
 */
export const fetchAuthByUserGroup = (id: string) => {
    return alovaInstance.Get<UserGroupAuthSetting[]>(`/user/role/project/permission/setting${id}`)
}