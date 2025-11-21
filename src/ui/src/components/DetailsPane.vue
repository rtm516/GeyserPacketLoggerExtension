<script setup>
import { EventBus } from '@/utils/event-bus';
import { ref, useTemplateRef } from 'vue';
import { formatPacketTime } from '@/utils/time-format';
import JsonViewer from './JsonViewer.vue';

/** @type { { time: string, side: 'BEDROCK' | 'JAVA', direction: 'SERVERBOUND' | 'CLIENTBOUND', packetName: string, packetId: number, packetData: any } | null } */
const selectedPacket = ref(null);

const copied = ref(false);
const copiedTimeout = ref(null);

const scrollArea = useTemplateRef('scrollArea');

EventBus.$on('select-packet', (packetInfo) => {
  selectedPacket.value = packetInfo;
  scrollArea.value.scrollTop = 0;
});

function copyToClip() {
  if (selectedPacket.value === null) {
    return;
  }

  const packetDataStr = JSON.stringify(selectedPacket.value.packetData, null, 2);
  navigator.clipboard.writeText(packetDataStr);

  copied.value = true;
  copiedTimeout.value && clearTimeout(copiedTimeout.value);
  copiedTimeout.value = setTimeout(() => {
    copied.value = false;
  }, 1000);
}
</script>

<template>
  <div class="overflow-y-scroll p-3" ref="scrollArea">
    <div v-if="selectedPacket !== null">
      <h3>{{ selectedPacket.packetName }} ({{ selectedPacket.packetId }})</h3>

      Platform: {{ selectedPacket.side }}<br>
      Direction: {{ selectedPacket.direction }}<br>
      Time: {{ formatPacketTime(selectedPacket.time) }}<br>

      <br>

      <div class="card">
        <div class="card-body">
          <button class="btn btn-sm btn-secondary float-end" :class="{'btn-success': copied}" @click="copyToClip"><i class="bi" :class="{'bi-clipboard': !copied, 'bi-clipboard-check': copied }"></i></button>
          <JsonViewer v-if="Object.keys(selectedPacket.packetData).length >= 1" :data="selectedPacket.packetData" />
          <div v-else>No packet data available.</div>
        </div>
      </div>
    </div>
  </div>
</template>
