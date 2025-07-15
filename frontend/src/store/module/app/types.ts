import type {IProjectItem} from '/@/types/project.ts'

export interface IAppState{
    currentOrgId: string;
    currentProjectId: string;
    projectList: IProjectItem[];
}