<script setup>
import { EventBus } from '@/utils/event-bus';
import { nextTick, onBeforeUnmount, ref, useTemplateRef } from 'vue';
import PacketRow from '@/components/PacketRow.vue';
import { BackendWebSocket } from '@/utils/backend-websocket';

const selectedPacket = ref(null);
const packets = ref([]);
const scrollArea = useTemplateRef('scrollArea');

async function onPacketReceived(event) {
  const packetInfo = event.detail

  const isScrolledToBottom = scrollArea.value.scrollHeight - scrollArea.value.clientHeight <= scrollArea.value.scrollTop + 1;

  packets.value.push(packetInfo);

  // Scroll to bottom if we were already at the bottom
  if (isScrolledToBottom) {
    await nextTick();
    scrollArea.value.scrollTop = scrollArea.value.scrollHeight
  }
}

BackendWebSocket.on('message-packet', onPacketReceived);

const selectPacket = (e, packet) => {
  // Prevent re-selecting the same packet
  if (selectedPacket.value === e.currentTarget) {
    return;
  }

  e.currentTarget.classList.add('table-active');
  selectedPacket.value?.classList.remove('table-active');
  selectedPacket.value = e.currentTarget;

  EventBus.$emit('select-packet', packet)
};
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
        <PacketRow v-for="(packet, index) in packets" :key="index" :packetInfo="packet" @click="(e) => selectPacket(e, packet)" />
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
