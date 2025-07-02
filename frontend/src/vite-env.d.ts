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
}
interface ImportMeta {
    readonly env: ImportMetaEnv;
}