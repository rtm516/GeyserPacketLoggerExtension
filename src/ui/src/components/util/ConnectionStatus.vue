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
  <img v-if="connected" class="icon" src="@/assets/icons/minecraft/Ping_Green.png" title="Connected" alt="Connected" />
  <img v-else class="icon" src="@/assets/icons/minecraft/Ping_Red.png" title="Disconnected" alt="Disconnected" />
</template>
