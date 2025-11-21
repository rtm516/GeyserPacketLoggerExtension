<script setup>
import { EventBus } from '@/utils/event-bus';
import { nextTick, ref, useTemplateRef, watch } from 'vue';
import PacketRow from '@/components/PacketRow.vue';
import { BackendWebSocket } from '@/utils/backend-websocket';
import { ConnectionHandler } from '@/utils/connection-handler';
import { eventListener } from '@/utils/misc';

const selectedPacket = ref(null);
const packets = ref([]);
const scrollArea = useTemplateRef('scrollArea');

const packetFilter = ref('');
const displayedPackets = ref([]);

async function onPacketReceived(event) {
  const packetInfo = event.detail

  if (ConnectionHandler.getActiveConnectionId() !== packetInfo.connectionId) {
    return;
  }

  const isScrolledToBottom = scrollArea.value != null && (scrollArea.value.scrollHeight - scrollArea.value.clientHeight <= scrollArea.value.scrollTop + 1);

  // packets.value.push(packetInfo);
  const newPackets = packets.value.slice();
  newPackets.push(packetInfo);
  packets.value = newPackets;

  // Scroll to bottom if we were already at the bottom
  if (scrollArea.value != null && isScrolledToBottom) {
    await nextTick();
    scrollArea.value.scrollTop = scrollArea.value.scrollHeight
  }
}

eventListener(BackendWebSocket, 'message-packet', onPacketReceived);

eventListener(ConnectionHandler, 'active-connection-changed', async () => {
  packets.value = ConnectionHandler.getActiveConnection()?.packets.slice() || [];
  selectPacket(null, null);

  await nextTick();
  scrollArea.value.scrollTop = scrollArea.value.scrollHeight
});

const onSelectPacket = (e, packet) => {
  selectPacket(e.currentTarget, packet);
}

const selectPacket = (newSelection, packet) => {
  // Prevent re-selecting the same packet
  if (selectedPacket.value === newSelection) {
    return;
  }

  newSelection?.classList.add('table-active');
  selectedPacket.value?.classList.remove('table-active');
  selectedPacket.value = newSelection;

  EventBus.$emit('select-packet', packet)
};

eventListener(window, 'keydown', (e) => {
  // arrow keys select previous/next packet
  if (e.key === 'ArrowUp' || e.key === 'ArrowDown') {
    e.preventDefault();

    // If we dont have a selected packet, do nothing
    if (!selectedPacket.value) {
      return;
    }

    const currentRow = selectedPacket.value;
    const targetRow = e.key === 'ArrowUp' ? currentRow.previousElementSibling : currentRow.nextElementSibling;
    if (targetRow) {
      targetRow.click();

      // TODO Scroll into view if needed
    }
  }
});

// Listen for filter changes
EventBus.$on('packet-filter', (query) => {
  packetFilter.value = query;
});

// Update displayed packets when packets or filter changes
watch([packets, packetFilter], () => {
  if (!packetFilter.value || packetFilter.value.trim() === '') {
    displayedPackets.value = packets.value;
    return;
  }

  const lowerQuery = packetFilter.value.toLowerCase();
  displayedPackets.value = packets.value.filter(packet => {
    return packet.packetName.toLowerCase().includes(lowerQuery) ||
           String(packet.packetId).includes(lowerQuery) ||
           JSON.stringify(packet.packetData).toLowerCase().includes(lowerQuery);
  });
});
</script>

<template>
  <div class="overflow-y-scroll" ref="scrollArea">
    <table id="packet-table" class="table table-striped table-sm m-0">
      <thead>
        <tr>
          <th scope="col" class="sticky-top fit"></th>
          <th scope="col" class="sticky-top fit"></th>
          <th scope="col" class="sticky-top">Packet</th>
        </tr>
      </thead>
      <tbody>
        <PacketRow v-for="(packet, index) in displayedPackets" :key="index + packet.packetName" :packetInfo="packet" @click="(e) => onSelectPacket(e, packet)" />
      </tbody>
    </table>
  </div>
</template>

<style>
#packet-table td.fit,
#packet-table th.fit {
  white-space: nowrap;
  width: 1%;
}
</style>

<style scoped>
tr {
  cursor: pointer;
}

.table-active {
  --bs-table-bg-state: rgb(0 0 255 / 10%);
}
</style>
