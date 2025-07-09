import {alovaInstance} from '/@/api';
import type {ILoginRespInfo} from '/@/types/role.ts'

interface ILogin {
    username: string;
    password: string;
}

export const fetchLogin = (param: ILogin) => {
    const methodInstance = alovaInstance.Post<ILoginRespInfo>(
        '/auth/login',
        param
    );
    methodInstance.meta = {authRole: null,};
    return methodInstance;
};