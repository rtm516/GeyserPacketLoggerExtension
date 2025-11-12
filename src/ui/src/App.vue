<script setup>
import Header from '@/components/core/Header.vue';
import Main from '@/components/core/Main.vue';
import Footer from '@/components/core/Footer.vue';

import { BackendWebSocket } from '@/utils/backend-websocket';

BackendWebSocket.connect();
</script>

<template>
  <Header />
  <Main />
  <Footer />
</template>

<style scoped>

</style>
