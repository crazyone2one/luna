import type {ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import {alovaInstance} from '/@/api'
import type {CreateOrUpdateSystemProjectParams, IProjectItem} from '/@/types/project.ts'

export const fetchOrgProjectPage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<IProjectItem>>('/organization/project/page', data)
}
export const fetchProjectList = (organizationId: string) => {
    return alovaInstance.Get<Array<IProjectItem>>('/project/list/options', {params: organizationId})
}
export const createOrUpdateProjectByOrg = (data: CreateOrUpdateSystemProjectParams) => {
    return alovaInstance.Post(data.id ? '/organization/project/update' : '/organization/project/add', data)
}