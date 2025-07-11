import {createRouter, createWebHashHistory, type RouteRecordRaw} from 'vue-router'
import {storage} from '../utils'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('/@/views/login/index.vue'),
        meta: {requiresAuth: false, title: '登录'}
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('/@/views/error/NotFound.vue'),
        meta: {requiresAuth: false, title: 'Oh no!'}
    },
    {
        path: '/', component: () => import('/src/layout/MainLayout.vue'),
        meta: {requiresAuth: true},
        children: [
            {
                path: '',
                redirect: '/home'
            },
            {
                path: 'home',
                name: 'Home',
                component: () => import('/@/views/home/index.vue'),
                meta: {title: '首页', menuKey: 'home'}
            },

            {
                path: '/setting',
                name: 'setting',
                children: [
                    {
                        path: 'taskCenter',
                        name: 'settingOrganizationTaskCenter',
                        component: () => import('/@/views/setting/organization/task-center/index.vue'),
                        meta: {title: '任务中心', menuKey: 'settingOrganizationTaskCenter'}
                    },
                    {
                        path: 'taskCenter',
                        name: 'settingSystemTaskCenter',
                        component: () => import('/@/views/setting/system/task-center/index.vue'),
                        meta: {title: '任务中心', menuKey: 'settingSystemTaskCenter'}
                    },
                    {
                        path: 'org-project',
                        name: 'settingOrganizationProject',
                        component: () => import('/@/views/setting/organization/project/OrgProject.vue'),
                        meta: {title: '项目', menuKey: 'settingOrganizationProject'}
                    },
                    {
                        path: 'org-user-group',
                        name: 'settingOrganizationUserGroup',
                        component: () => import('/@/views/setting/organization/user-group/OrgUserGroup.vue'),
                        meta: {title: '用户组', menuKey: 'settingOrganizationUserGroup'}
                    },
                    {
                        path: 'system-user-group',
                        name: 'settingSystemUserGroup',
                        component: () => import('/@/views/setting/system/user-group/SystemUserGroup.vue'),
                        meta: {title: '用户组', menuKey: 'settingSystemUserGroup'}
                    },
                    {
                        path: 'user',
                        name: 'settingSystemUser',
                        component: () => import('/@/views/setting/system/user/index.vue'),
                        meta: {title: '用户', menuKey: 'settingSystemUser'}
                    },
                ]
            },
        ]
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})
// Authorize (Make sure that is the first hook.)
router.beforeEach(to => {
    const {expires = 0} = storage.get<{ expires: number }>('token') ?? {}
    // already authorized
    if (to.name === 'Login' && expires > Date.now()) {
        return to.query.redirect?.toString() ?? '/'
    }
    // need authorize & token is invalid
    if (to.meta.requiresAuth === true && expires <= Date.now()) {
        storage.clearToken();
        return {name: 'Login', query: {redirect: to.fullPath}}
    }
})

router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.title as string)
    document.title = items.join(' · ')
})
export default router;