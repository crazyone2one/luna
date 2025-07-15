import {defineStore} from 'pinia'
import type {IAppState} from '/@/store/module/app/types.ts'
import {fetchProjectList} from '/@/api/system/org-project.ts'

const useAppStore = defineStore('app', {
    state: (): IAppState => ({
        currentOrgId: '',
        currentProjectId: '',
        projectList: []
    }),
    persist: {
        pick: ['currentOrgId', 'currentProjectId']
    },
    getters: {
        getCurrentOrgId(state: IAppState): string {
            return state.currentOrgId;
        },
        getCurrentProjectId(state: IAppState): string {
            return state.currentProjectId;
        },
    },
    actions: {
        setCurrentOrgId(id: string) {
            this.currentOrgId = id;
        },
        setCurrentProjectId(id: string) {
            this.currentProjectId = id;
        },
        async initProjectList(){
            if (this.currentOrgId) {
                this.projectList = await fetchProjectList(this.currentOrgId)
            } else {
                this.projectList=[]
            }
        }
    }
})
export default useAppStore