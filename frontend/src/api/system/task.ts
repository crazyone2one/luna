import {alovaInstance} from '/@/api';
import type {ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import type {ITaskCenterTaskItem, ITaskItem} from '/@/types/task.ts'

export const fetchSchedulePage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<ITaskCenterTaskItem>>('/system-schedule/page', data)
}

export const fetchScheduleSwitch = (id: string) => {
    return alovaInstance.Get(`/system-schedule/schedule/switch/${id}`)
}
export const fetchCreateSchedule = (data: ITaskItem) => {
    return alovaInstance.Post(`/system-schedule/save`, data)
}