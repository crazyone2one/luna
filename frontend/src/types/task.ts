export interface ITaskCenterTaskItem {
    organizationName: string; // 所属组织名称
    projectName: string; // 所属项目名称
    projectId: string; // 项目ID
    organizationId: string; // 组织ID
    id: string;
    reportId: string;
    taskName: string;
    resourceId: string; // 资源ID
    num: number;
    resourceType: string; // 资源类型
    resourceNum: number; // 资源num
    value: string;
    nextTime: number;
    enable: boolean;
    createUserId: string;
    createUserName: string;
    createTime: number;
    [key: string]: any;
}
export interface ITaskItem {
    id?: string;
    name: string;
    resourceId?: string; // 资源ID
    num?: number;
    resourceType?: string; // 资源类型
    resourceNum?: number; // 资源num
    value: string;
    nextTime?: number;
    enable?: boolean;
    createUserId?: string;
    createUserName?: string;
    job: string;
    createTime?: number;
    [key: string]: any;
}