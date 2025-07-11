import {defineStore} from 'pinia'
import type {IAppState} from '/@/store/module/app/types.ts'

const useAppStore = defineStore('app', {
    state: (): IAppState => ({
        currentOrgId: '',
        currentProjectId: '',
    }),
    persist: {
        pick: ['currentOrgId', 'currentProjectId']
    },
    getters:{
        getCurrentOrgId(state: IAppState): string {
            return state.currentOrgId;
        },
        getCurrentProjectId(state: IAppState): string {
            return state.currentProjectId;
        },
    },
    actions:{
        setCurrentOrgId(id: string) {
            this.currentOrgId = id;
        },
        setCurrentProjectId(id: string) {
            this.currentProjectId = id;
        },
    }
})
export default useAppStore