import type {ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import {alovaInstance} from '/@/api'
import type {CreateOrUpdateSystemProjectParams, IProjectItem, SystemOrgOption} from '/@/types/project.ts'
import type {UserSelectorOption} from '/@/types/user.ts'
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