import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import UnoCSS from 'unocss/vite'
// https://vite.dev/config/
export default defineConfig(({mode}) => {
    const isProduction = mode === 'production'
    return {
        plugins: [vue(), UnoCSS()],
        server: !isProduction ? {
            proxy: {
                '/front': {
                    target: 'http://127.0.0.1:8080/',
                    changeOrigin: true,
                    rewrite: (path: string) => path.replace(new RegExp('^/front'), ''),
                },
            }
        } : {},
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
                        'naive-ui': ['naive-ui'],
                        vue: ['vue', 'vue-router', 'pinia'],
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
