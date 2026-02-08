<script setup>
import ConnectionStatus from '@/components/Util/ConnectionStatus.vue';
import { ref, useTemplateRef, watch } from 'vue';
import { ConnectionHandler } from '@/utils/connection-handler';
import { eventListener } from '@/utils/misc';
import { EventBus } from '@/utils/event-bus';
import SettingsModal from "@/components/SettingsModal.vue";

const initialConnections = []
for (const [id, connection] of Object.entries(ConnectionHandler.connections)) {
  initialConnections.push({
    id: id,
    username: connection.username,
    active: connection.active
  });
}

const connections = ref(initialConnections);
const activeConnectionId = ref(ConnectionHandler.getActiveConnectionId());
const query = ref('');
const settingsModal = useTemplateRef('settings-modal');

function onConnectionChanged (event) {
  const connectionInfo = ConnectionHandler.getConnection(event.detail);

  // Find and update existing connection or add new one
  const existingConnection = connections.value.find(c => c.id === event.detail);
  if (existingConnection) {
    existingConnection.username = connectionInfo.username;
    existingConnection.active = connectionInfo.active;
    return;
  }

  connections.value.push({
    id: event.detail,
    username: connectionInfo.username,
    active: connectionInfo.active
  });

  if (activeConnectionId.value == null) {
    selectConnection(null, event.detail);
  }
}

eventListener(ConnectionHandler, 'connection-created', onConnectionChanged);
eventListener(ConnectionHandler, 'connection-updated', onConnectionChanged);

const selectConnection = (e, connectionId) => {
  activeConnectionId.value = connectionId;
  ConnectionHandler.setActiveConnection(connectionId);
};

watch(() => query.value, (newQuery) => {
  EventBus.$emit('packet-filter', newQuery)
});

function settingsClick() {
  settingsModal.value.show();
}
</script>

<template>
  <header class="flex-shrink-0">
    <div class="flex-fill title-bar">
      <button class="left" title="Settings" @click="settingsClick"><img src="@/assets/icons/minecraft/settings_pause_menu_icon.png" alt="Settings"></button>
      Geyser Packet Logger
      <div class="right"><ConnectionStatus /></div>
    </div>

    <div class="tabs tabs-block">
      <button v-for="connection in connections" class="tab d-flex align-items-center justify-content-center" :key="connection.id" :class="{active: activeConnectionId == connection.id}" @click="(e) => selectConnection(e, connection.id)" :title="connection.id">
        {{ connection.username || "Unknown" }}
        <img v-if="connection.active" class="ms-1 icon" src="@/assets/icons/minecraft/player_online_icon.png" title="Active" alt="Active" />
        <img v-else class="ms-1 icon" src="@/assets/icons/minecraft/player_offline_icon.png" title="Inactive" alt="Inactive" />
      </button>
    </div>

    <!-- TODO Remove the need for the wrapper div here -->
    <div class="d-flex">
      <input v-model="query" type="text" class="input flex-fill" placeholder="Search" id="search">
    </div>
  </header>

  <SettingsModal ref="settings-modal" />
</template>
