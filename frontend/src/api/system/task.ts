import {alovaInstance} from '/@/api';
import type {ICommonPage, ITableQueryParams} from '/@/types/common.ts'
import type {ITaskCenterTaskItem, ITaskItem} from '/@/types/task.ts'

export const fetchSchedulePage = (data: ITableQueryParams) => {
    return alovaInstance.Post<ICommonPage<ITaskCenterTaskItem>>('/system-schedule/organization/schedule/page', data)
}
/**
 * 编辑 cron 表达式
 * @param cron cron 表达式
 * @param id 任务id
 */
export const organizationEditCron = (cron: string, id: string) => {
    return alovaInstance.Post('/system-schedule/organization/task-center/schedule/update-cron', {cron, id})
}

export const fetchScheduleSwitch = (id: string) => {
    return alovaInstance.Get(`/system-schedule/schedule/switch/${id}`)
}
export const fetchCreateSchedule = (data: ITaskItem) => {
    return alovaInstance.Post(`/system-schedule/save`, data)
}