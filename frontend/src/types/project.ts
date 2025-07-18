export interface IProjectItem {
    id: string;
    name: string;
    description: string;
    enable: boolean;
    // adminList: UserItem[];
    organizationId: string;
    organizationName: string;
    num: number;
    updateTime: number;
    createTime: number;
    memberCount: number;
    userIds: string[];
    resourcePoolIds: string[];
    orgAdmins: Record<string, any>;
    moduleSetting: string[];
    allResourcePool: boolean;
}
export interface CreateOrUpdateSystemProjectParams {
    id?: string;
    // 项目名称
    name: string;
    num: string;
    // 项目描述
    description?: string;
    // 启用或禁用
    enable?: boolean;
    // 项目成员
    userIds?: string[];
    // 模块配置
    moduleSetting?: string[];
    // 所属组织
    organizationId?: string;
}
export interface SystemOrgOption {
    id: string;
    name: string;
}