import {createPinia} from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import useUserStore from './module/user'
import useAppStore from './module/app'

const pinia = createPinia().use(piniaPluginPersistedstate);
export {useUserStore,useAppStore};
export default pinia;