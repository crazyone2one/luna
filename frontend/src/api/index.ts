// user alova instance
import {createAlova} from 'alova'
import adapterFetch from 'alova/fetch'
import VueHook from 'alova/vue';
import {ContentTypeEnum} from '/@/enums/httpEnum.ts'
import {storage} from '/@/utils'
import {xhrRequestAdapter} from '@alova/adapter-xhr'

export const alovaInstance = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL}`,
    statesHook: VueHook,
    requestAdapter: adapterFetch(),
    timeout: 300 * 1000,

    async beforeRequest(method) {
        const {access = ''} = storage.get<{ access: string }>('token') ?? {}
        // const token = storage.get(method.meta?.authRole === 'refreshToken' ? 'refreshToken' : 'accessToken')
        if (access) {
            method.config.headers.Authorization = `Bearer ${access}`;
        }
        method.config.headers = {...method.config.headers, 'Content-Type': ContentTypeEnum.JSON}
    },
    responded: {
        // 请求成功的拦截器
        // 当使用 `alova/fetch` 请求适配器时，第一个参数接收Response对象
        // 第二个参数为当前请求的method实例，你可以用它同步请求前后的配置信息
        onSuccess: async (response, method) => {
            const json = await response.json();
            const {status} = response
            if (status >= 400) {
                window.$message.error(json.message ?? response.statusText)
                throw new Error(response.statusText);
            }
            if (json.code !== 100200) {
                // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
                throw new Error(json.message);
            }
            if (method.meta?.isBlob) {
                return response.blob();
            }
            return json.data;
        },
        // 请求失败的拦截器
        // 请求错误时将会进入该拦截器。
        // 第二个参数为当前请求的method实例，你可以用它同步请求前后的配置信息
        onError: (err, method) => {
            if (import.meta.env.MODE === 'development') {
                console.error('[Alova Error]', err, method)
            }
            alert(err.message);
        },
        onComplete: async _method => {
            // 处理请求完成逻辑
        }
    }
});
/**
 * 使用xhrRequest创建实例
 */
export const xhrInst = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL}`,
    statesHook: VueHook,
    requestAdapter: xhrRequestAdapter(),
    // ...
    async beforeRequest(method) {
        const {access = ''} = storage.get<{ access: string }>('token') ?? {}
        if (access) {
            method.config.headers.Authorization = `Bearer ${access}`;
        }
    },
    responded: {
        onSuccess: async (response, _method) => {
            return response;
        },
    }
});