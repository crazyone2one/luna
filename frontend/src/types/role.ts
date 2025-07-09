export interface IRole {
    /** 用户id */
    id: string;
    /** 用户名 */
    name: string;
    internal: boolean;
}
export type IRoleType = "super" | "admin" | "user";
export interface IUser {
    /** 用户id */
    id: number;
    /** 用户名 */
    name: string;
    /* 用户邮箱 */
    email?: string;
    /** 用户角色类型 */
    role?: IRoleType[];
}
export interface ILoginRespInfo {
    /** 访问token */
    access_token: string;
    /** 刷新token */
    refresh_token?: string;
    user: IUser;
}