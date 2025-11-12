<script setup>
import ConnectionStatus from '@/components/ConnectionStatus.vue';
import { ref } from 'vue';
import { ConnectionHandler } from '@/utils/connection-handler';
import { eventListener } from '@/utils/misc';

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

</script>

<template>
  <header class="flex-shrink-0">
    <ul class="flex-fill nav nav-tabs" id="tabs">
      <li class="nav-item">
        <button class="nav-link" title="Settings"><i class="bi bi-wrench"></i></button>
      </li>
      <li v-for="connection in connections" class="nav-item">
        <button class="nav-link d-flex align-items-center" :class="{active: activeConnectionId == connection.id}" @click="(e) => selectConnection(e, connection.id)">
          {{ connection.username || "Unknown" }}
          <span class="ms-1 p-2 border rounded-circle d-inline-block" :class="{ 'bg-success': connection.active, 'bg-danger': !connection.active }"></span>
        </button>
      </li>
      <li class="d-flex align-items-center ms-auto me-3">
        <ConnectionStatus />
      </li>
    </ul>
    <!-- Search -->
  </header>
</template>
