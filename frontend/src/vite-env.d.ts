/// <reference types="vite/client" />
interface ViteTypeOptions {
    // 添加这行代码，你就可以将 ImportMetaEnv 的类型设为严格模式，
    // 这样就不允许有未知的键值了。
    strictImportMetaEnv: unknown;
}

interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string;
    readonly VITE_DEV_DOMAIN: string; // 开发环境域名
    readonly VITE_APP_TITLE: string
    readonly VITE_APP_BASE_API: string;
    readonly VITE_APP_TOKEN_STORAGE: 'sessionStorage' | 'localStorage'
}
interface ImportMeta {
    readonly env: ImportMetaEnv;
}
declare module "*.vue" {
    import type {DialogProviderInst, MessageProviderInst, NotificationProviderInst,} from 'naive-ui';
    import {DefineComponent} from 'vue';
    global {
        interface Window {
            $message: MessageProviderInst;
            $dialog: DialogProviderInst;
            $notification: NotificationProviderInst;
        }
    }
    const component: DefineComponent;
    export default component;
}