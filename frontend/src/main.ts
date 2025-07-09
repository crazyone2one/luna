import {createApp} from 'vue'
// import './style.css'
import App from './App.vue'
import {naive} from '/@/plugins/naive.ts'
import router from '/@/router/index.ts'
import 'virtual:uno.css'
import '@unocss/reset/tailwind-compat.css'

const app = createApp(App)
app.use(naive)
app.use(router)
app.mount('#app')
