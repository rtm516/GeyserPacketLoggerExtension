<script setup lang="ts">
import ConnectionStatus from '@/components/ConnectionStatus.vue';
</script>

<template>
  <header class="flex-shrink-0">
    <ul class="flex-fill nav nav-tabs" id="tabs">
      <li class="nav-item">
        <button class="nav-link active" id="user1-tab">User1</button>
      </li>
      <li class="nav-item">
        <button class="nav-link" id="user2-tab">User2</button>
      </li>
      <li class="nav-item">
        <button class="nav-link" id="user3-tab">User3</button>
      </li>
      <li class="d-flex align-items-center ms-auto me-3">
        <ConnectionStatus />
      </li>
    </ul>
    <!-- Search -->
  </header>
</template>
