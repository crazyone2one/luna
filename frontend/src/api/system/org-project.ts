import type {ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import {alovaInstance} from '/@/api'
import type {CreateOrUpdateSystemProjectParams, IProjectItem, SystemOrgOption} from '/@/types/project.ts'
import type {AddUserToOrgOrProjectParams, UserListItem, UserSelectorOption} from '/@/types/user.ts'
import type {ILoginRespInfo} from '/@/types/role.ts'

export const fetchOrgProjectPage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<IProjectItem>>('/organization/project/page', data)
}
export const fetchProjectList = (organizationId: string) => {
    return alovaInstance.Get<Array<IProjectItem>>(`/project/list/options/${organizationId}`)
}
export const createOrUpdateProjectByOrg = (data: CreateOrUpdateSystemProjectParams) => {
    return alovaInstance.Post(data.id ? '/organization/project/update' : '/project/save', data)
}
/**
 * 获取组织下拉选项
 */
export const getSystemOrgOption = () => {
    return alovaInstance.Post<Array<SystemOrgOption>>('/organization/option/all')
}
/**
 * 组织-获取项目下的管理员选项
 * @param organizationId
 * @param keyword
 */
export const getAdminByProjectByOrg = (organizationId: string, keyword: string) => {
    return alovaInstance.Get<Array<UserSelectorOption>>(`/organization/project/user-admin-list/${organizationId}`, {params: keyword})
}
/**
 * 切换项目数据
 * @param data
 */
export const switchProject = (data: { projectId: string; userId: string }) => {
    return alovaInstance.Post<ILoginRespInfo>('/project/switch', data)
}
/**
 * 组织-启用或禁用项目
 * @param id
 * @param isEnable
 */
export const enableOrDisableProjectByOrg = (id: string, isEnable = true) => {
    return alovaInstance.Get(isEnable ? `/project/organization/enable/${id}` : `/project/organization/disable/${id}`)
}

export const postProjectMemberByProjectId = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>('/organization/project/member-list', data)
}
/**
 * 组织-移除项目成员
 * @param projectId
 * @param userId
 */
export const deleteProjectMemberByOrg = (projectId: string, userId: string) => {
    return alovaInstance.Get(`/organization/project/remove-member/${projectId}/${userId}`)
}
/**
 * 系统设置-系统-组织与项目-获取添加成员列表
 * @param data
 */
export const getSystemMemberListPage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>('/system/user/organization/member-list', data)
}
/**
 * 系统设置-组织-项目-分页获取成员列表
 * @param data
 */
export const getOrganizationMemberListPage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<UserListItem>>('/organization/project/user-list', data)
}

export const addUserToOrgOrProject = (data: AddUserToOrgOrProjectParams) => {
    return alovaInstance.Post(data.projectId ? '/project/system/add-member' : '/organization/system/add-member', data)
}

export const addProjectMemberByOrg = (data: AddUserToOrgOrProjectParams) => {
    return alovaInstance.Post('/organization/project/add-members', data)
}