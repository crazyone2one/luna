import type {AlovaXHRResponseHeaders} from '@alova/adapter-xhr'

export interface ICommonResponse<T> {
    code: number;
    message: string;
    messageDetail: string;
    data: T;
}

export interface ICommonPage<T> {
    [x: string]: any;

    pageSize: number;
    totalPage: number;
    pageNumber: number;
    totalRow: number;
    records: T[];
}

export interface ITableQueryParams {
    // 当前页
    page?: number;
    // 每页条数
    pageSize?: number;
    // 排序仅针对单个字段
    sort?: object;
    // 排序仅针对单个字段
    sortString?: string;
    // 表头筛选
    filter?: object;
    // 查询条件
    keyword?: string;
}

export interface ISelectOption {
    id: string;
    name: string;
    disabled?: boolean;
}

export interface BatchActionQueryParams {
    excludeIds?: string[]; // 排除的id
    selectedIds?: string[];
    selectAll: boolean; // 是否跨页全选
    params?: ITableQueryParams; // 查询参数
    currentSelectCount?: number; // 当前选中的数量
    condition?: any; // 查询条件
    [key: string]: any;
}

export interface AlovaXHRResponse<T = any> {
    status: number;
    statusText: string;
    data: T;
    headers: AlovaXHRResponseHeaders;
}

export interface ImportResult {
    importCount: number;
    successCount: number;
    errorMessages: Record<string, any>;
}