import {
    create,
    NA,
    NAvatar,
    NBadge,
    NButton,
    NCard,
    NCheckbox,
    NCheckboxGroup,
    NDataTable,
    NDropdown,
    NForm,
    NFormItem,
    NFormItemGi,
    NGrid,
    NIcon,
    NInput,
    NLayout,
    NLayoutContent,
    NLayoutFooter,
    NLayoutHeader,
    NLayoutSider,
    NMenu,
    NP,
    NResult,
    NSelect,
    NSpace,
    NSpin,
    NSwitch,
    NTag,
    NText,
    NTooltip
} from 'naive-ui'
import {Home, LogOut, Menu as MenuIcon, Moon, Settings,} from '@vicons/ionicons5'

// 注册常用组件
const naive = create({
    components: [
        NButton, NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NLayoutFooter,
        NMenu, NIcon, NAvatar, NDropdown, NBadge, NTooltip,
        NResult, NCard, NForm, NFormItem, NGrid, NFormItemGi, NInput, NA, NText, NP, NSpace, NDataTable, NSpin, NSelect,
        NTag, NSwitch, NCheckboxGroup, NCheckbox
    ]
})

// 全局图标注册（方便在模板中直接使用）
export const globalIcons = {
    MenuIcon, Home, Settings, LogOut, Moon
}

export {naive}