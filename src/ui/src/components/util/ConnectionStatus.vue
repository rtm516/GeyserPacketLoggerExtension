<script setup>
import { ref } from 'vue';
import { BackendWebSocket } from '@/utils/backend-websocket';
import { eventListener } from '@/utils/misc';

const connected = ref(BackendWebSocket.isConnected());

function onStatusChanged(event) {
  connected.value = event.type === 'connected';
}

eventListener(BackendWebSocket, 'connected', onStatusChanged);
eventListener(BackendWebSocket, 'disconnected', onStatusChanged);
</script>

<template>
  <span v-if="connected" class="badge text-bg-success">Connected</span>
  <span v-else class="badge text-bg-danger">Disconnected</span>
</template>
