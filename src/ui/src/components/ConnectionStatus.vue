<script setup>
import { ref } from 'vue';
import { BackendWebSocket } from '@/utils/backend-websocket';

const connected = ref(BackendWebSocket.isConnected());

function onStatusChanged(event) {
  connected.value = event.type === 'connected';
}

BackendWebSocket.on('connected', onStatusChanged);
BackendWebSocket.on('disconnected', onStatusChanged);
</script>

<template>
  <span v-if="connected" class="badge text-bg-success">Connected</span>
  <span v-else class="badge text-bg-danger">Disconnected</span>
</template>