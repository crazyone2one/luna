import {defineConfig, presetIcons, presetWind3} from 'unocss'

import {FileSystemIconLoader} from '@iconify/utils/lib/loader/node-loaders'

export default defineConfig({
    // ...UnoCSS options
    content: {
        pipeline: {
            exclude: ['node_modules', 'dist'],
        },
    },
    presets: [
        presetWind3,
        presetIcons({
            scale: 1,
            extraProperties: {},
            warn: true,
            collections: {
                local: FileSystemIconLoader('src/assets/svg-icons',
                        svg => svg.replace(/^<svg /, '<svg fill="currentColor" '))
            },
        }),

    ],
})