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
                path: 'user',
                name: 'UserList',
                component: () => import('/@/views/user/UserList.vue'),
                meta: {title: '用户管理', menuKey: 'user'}
            },
            {
                path: '/settings',
                name: 'setting',
                children: [
                    {
                        path: 'task',
                        name: 'Task',
                        component: () => import('/@/views/task/index.vue'),
                        meta: {title: '任务管理', menuKey: 'task'}
                    }
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
        return {name: 'Login', query: {redirect: to.fullPath}}
    }
})

router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.title as string)
    document.title = items.join(' · ')
})
export default router;