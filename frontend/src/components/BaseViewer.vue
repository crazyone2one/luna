<script setup lang="ts">

import {useDialog, useLoadingBar, useMessage, useNotification,} from 'naive-ui';
import router from '/@/router';

const loadingBar = useLoadingBar();

router.beforeEach(() => loadingBar?.start());

router.afterEach(() => loadingBar?.finish());
window.$message = useMessage();
window.$dialog = useDialog();
window.$notification = useNotification();
</script>

<template>
  <router-view />
</template>

<style scoped>

</style>