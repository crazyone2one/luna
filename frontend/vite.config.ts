import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import UnoCSS from 'unocss/vite'
// https://vite.dev/config/
export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), '');
    return {
        mode: 'development',
        plugins: [vue(), UnoCSS()],
        server: {
            proxy: {
                [env.VITE_APP_BASE_API]: {
                    target: env.VITE_DEV_DOMAIN,
                    changeOrigin: true,
                    rewrite: (path: string) =>
                        path.replace(new RegExp('^' + env.VITE_APP_BASE_API), ''),
                },
            }
        },
        resolve: {
            alias: [
                {
                    find: /\/@\//,
                    replacement: path.resolve(__dirname, '.', 'src') + '/',
                },
            ]
        },
        build: {
            rollupOptions: {
                output: {
                    manualChunks: {
                        'naive-ui': ['naive-ui']
                    }
                }
            }
        },
        define: {
            'process.env': {},
            // 定义特性标志
            '__VUE_OPTIONS_API__': true,
            '__VUE_PROD_DEVTOOLS__': false,
            // 设置hydration不匹配详细信息的标志
            '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': true,
        }
    }
})
