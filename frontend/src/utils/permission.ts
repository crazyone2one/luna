import type {IUserRole, IUserRoleRelation, SystemScopeType} from '/@/store/module/user/types.ts'
import {useUserStore} from '/@/store'

export const composePermissions = (userRoleRelations: IUserRoleRelation[], type: SystemScopeType, id: string) => {
    if (type === 'SYSTEM') {
        return userRoleRelations
            .filter((ur) => ur.userRole && ur.userRole.type === 'SYSTEM')
            .flatMap((role) => role.userRolePermissions)
            .map((g) => g.permissionId);
    }
    let func: (role: IUserRole) => boolean;
    switch (type) {
        case 'PROJECT':
            func = (role) => role && role.type === 'PROJECT';
            break;
        case 'ORGANIZATION':
            func = (role) => role && role.type === 'ORGANIZATION';
            break;
        default:
            func = (role) => role && role.type === 'SYSTEM';
            break;
    }
    return userRoleRelations
        .filter((ur) => func(ur.userRole))
        .filter((ur) => ur.sourceId === id)
        .flatMap((role) => role.userRolePermissions)
        .map((g) => g.permissionId);
}

export const hasPermission = (permission: string, typeList: string[]) => {
    const userStore = useUserStore();
    if (userStore.isAdmin) {
        return true
    }
    const {projectPermissions, orgPermissions, systemPermissions} = userStore.currentRole;
    if (projectPermissions.length === 0 && orgPermissions.length === 0 && systemPermissions.length === 0) {
        return false;
    }
    if (typeList.includes('PROJECT') && projectPermissions.includes(permission)) {
        return true;
    }
    if (typeList.includes('ORGANIZATION') && orgPermissions.includes(permission)) {
        return true;
    }
    return typeList.includes('SYSTEM') && systemPermissions.includes(permission);
}

export const hasAnyPermission = (permissions: string[], typeList = ['PROJECT', 'ORGANIZATION', 'SYSTEM']) => {
    if (!permissions || permissions.length === 0) {
        return true;
    }
    return permissions.some((permission) => hasPermission(permission, typeList));
}

export const hasAllPermission = (permissions: string[], typeList = ['PROJECT', 'ORGANIZATION', 'SYSTEM']) => {
    if (!permissions || permissions.length === 0) {
        return true;
    }
    return permissions.every((permission) => hasPermission(permission, typeList));
}