<script setup>
const props = defineProps({
  packetInfo: {
    type: Object,
    required: true,
  }
})

/** @type { { time: string, side: 'BEDROCK' | 'JAVA', direction: 'SERVERBOUND' | 'CLIENTBOUND', packetName: string, packetId: number, packetData: any } } */
const packetInfo = props.packetInfo

const prettyDirection = packetInfo.direction === 'SERVERBOUND' ? 'C → S' : packetInfo.direction === 'CLIENTBOUND' ? 'S → C' : '?'
</script>

<template>
  <tr>
    <td class="fit">
      <img v-if="packetInfo.side === 'BEDROCK'" src="@/assets/bedrock_block.png" alt="B" width="16" height="16" />
      <img v-else-if="packetInfo.side === 'JAVA'" src="@/assets/grass_block.png" alt="J" width="16" height="16" />
      <span v-else>?</span>
    </td>
    <td class="fit">{{ prettyDirection }}</td>
    <td>{{ packetInfo.packetName }}</td>
  </tr>
</template>
