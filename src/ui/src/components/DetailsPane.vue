<script setup>
import { EventBus } from '@/utils/event-bus';
import { ref, useTemplateRef } from 'vue';
import { formatPacketTime } from '@/utils/time-format';

/** @type { { time: string, side: 'BEDROCK' | 'JAVA', direction: 'SERVERBOUND' | 'CLIENTBOUND', packetName: string, packetId: number, packetData: any } | null } */
const selectedPacket = ref(null);

const scrollArea = useTemplateRef('scrollArea');

EventBus.$on('select-packet', (packetInfo) => {
  selectedPacket.value = packetInfo;
  scrollArea.value.scrollTop = 0;
});
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
          <pre class="m-0"><code>{{ JSON.stringify(selectedPacket.packetData, null, 2) }}</code></pre>
        </div>
      </div>
    </div>
  </div>
</template>