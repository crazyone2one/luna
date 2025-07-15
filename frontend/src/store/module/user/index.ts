import {defineStore} from 'pinia'
import type {IUserState} from '/@/store/module/user/types.ts'
import {useAppStore} from '/@/store'
import {composePermissions} from '/@/utils/permission.ts'

const useUserStore = defineStore('user', {
    persist: true,
    state: (): IUserState => ({
        name: undefined,
        organization: undefined,
        email: undefined,
        organizationName: undefined,
        phone: undefined,
        role:'',
        id: undefined,
        userRolePermissions: [],
        userRoles: [],
        userRoleRelations: [],
        lastProjectId: '',
        lastOrganizationId: '',
    }),
    getters:{
        userInfo(state: IUserState): IUserState {
            return { ...state };
        },
        currentRole(state: IUserState): {
            projectPermissions: string[];
            orgPermissions: string[];
            systemPermissions: string[];
        }{
            const appStore = useAppStore();
            state.userRoleRelations?.forEach((ug) => {
                state.userRolePermissions?.forEach((gp) => {
                    if (gp.userRole.id === ug.roleId) {
                        ug.userRolePermissions = gp.userRolePermissions;
                        ug.userRole = gp.userRole;
                    }
                });
            });
            return {
                projectPermissions: composePermissions(state.userRoleRelations || [], 'PROJECT', appStore.currentProjectId),
                orgPermissions: composePermissions(state.userRoleRelations || [], 'ORGANIZATION', appStore.currentOrgId),
                systemPermissions: composePermissions(state.userRoleRelations || [], 'SYSTEM', 'global'),
            };
        },
        isAdmin(state: IUserState):boolean{
            if (!state.userRolePermissions) return false;
            return state.userRolePermissions.findIndex((ur) => ur.userRole.id === 'admin') > -1;
        }
    },
    actions:{
        setInfo(partial: Partial<IUserState>) {
            this.$patch(partial);
        },
        resetInfo() {
            this.$reset();
        },
    }
})
export default useUserStore;