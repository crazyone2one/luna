import type {IUserRole, IUserRoleRelation, SystemScopeType} from '/@/store/module/user/types.ts'

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