import {
    create,
    NA,
    NAlert,
    NAvatar,
    NBadge,
    NButton,
    NCard,
    NCheckbox,
    NCheckboxGroup,
    NDataTable,
    NDivider,
    NDrawer,
    NDrawerContent,
    NDropdown,
    NForm,
    NFormItem,
    NFormItemGi,
    NGi,
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
    NPopover,
    NRadio,
    NRadioButton,
    NRadioGroup,
    NResult,
    NSelect,
    NSpace,
    NSpin,
    NSplit,
    NSwitch,
    NTag,
    NText,
    NTooltip,
    NUpload,
    NUploadDragger
} from 'naive-ui'
import {Home, LogOut, Menu as MenuIcon, Moon, Settings,} from '@vicons/ionicons5'

// 注册常用组件
const naive = create({
    components: [
        NButton, NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NLayoutFooter,
        NMenu, NIcon, NAvatar, NDropdown, NBadge, NTooltip,
        NResult, NCard, NForm, NFormItem, NGrid, NFormItemGi, NInput, NA, NText, NP, NSpace, NDataTable, NSpin, NSelect,
        NTag, NSwitch, NCheckboxGroup, NCheckbox, NSplit, NRadioGroup, NRadio, NRadioButton, NPopover, NDivider, NGi,
        NAlert,NUpload,NUploadDragger,NDrawer,NDrawerContent
    ]
})

// 全局图标注册（方便在模板中直接使用）
export const globalIcons = {
    MenuIcon, Home, Settings, LogOut, Moon
}

export {naive}