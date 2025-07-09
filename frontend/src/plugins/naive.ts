import {
    create,
    NA,
    NAvatar,
    NBadge,
    NButton,
    NCard,
    NConfigProvider,
    NDialogProvider,
    NDropdown,
    NForm,
    NFormItem,
    NGlobalStyle,
    NIcon,
    NInput,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLayoutSider,
    NLoadingBarProvider,
    NMenu,
    NMessageProvider,
    NP,
    NResult,
    NSpace,
    NText,
    NTooltip
} from 'naive-ui'
import {Home, LogOut, Menu as MenuIcon, Moon, Settings,} from '@vicons/ionicons5'

// 注册常用组件
const naive = create({
    components: [
        NButton, NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NLayoutFooter, NGlobalStyle, NLoadingBarProvider,
        NMenu, NIcon, NAvatar, NDropdown, NBadge, NTooltip,
        NMessageProvider, NDialogProvider, NConfigProvider, NResult, NCard, NForm, NFormItem, NInput, NA, NText, NP, NSpace
    ]
})

// 全局图标注册（方便在模板中直接使用）
export const globalIcons = {
    MenuIcon, Home, Settings, LogOut, Moon
}

export {naive}