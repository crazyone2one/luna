/// <reference types="vite/client" />
interface ViteTypeOptions {
    // 添加这行代码，你就可以将 ImportMetaEnv 的类型设为严格模式，
    // 这样就不允许有未知的键值了。
    strictImportMetaEnv: unknown;
}
declare module '*.vue' {
    import {DefineComponent} from 'vue';
    const component: DefineComponent<{}, {}, any>;
    export default component;
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
interface Window {
    $loadingBar?: import('naive-ui').LoadingBarProviderInst
    $dialog: import('naive-ui').DialogProviderInst
    $message: import('naive-ui').MessageProviderInst
    $notification: import('naive-ui').NotificationProviderInst
}

type Layout = 'base' | 'blank'