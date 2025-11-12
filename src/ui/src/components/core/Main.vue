<script setup>
import PacketTable from '@/components/PacketTable.vue';
import DetailsPane from '@/components/DetailsPane.vue';
</script>

<template>
  <main class="d-flex flex-grow-1 overflow-hidden">
    <PacketTable class="flex-fill w-100" />
    <DetailsPane class="flex-fill w-100" />
  </main>
</template>
