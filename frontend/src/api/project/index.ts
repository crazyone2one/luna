import {alovaInstance} from '/@/api'
import type {ISelectOption} from '/@/types/common.ts'

export const getProjectUserGroup = (projectId: string) => {
    return alovaInstance.Get<Array<ISelectOption>>(`/project/member/get-role/option/${projectId}`)
}