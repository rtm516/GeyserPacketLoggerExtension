<script setup>
import { ref, onMounted, watch, nextTick, markRaw } from 'vue'

import 'vue-json-pretty/lib/styles.css';

const props = defineProps({
  data: {
    type: Object,
    required: true,
  }
})

const componentReady = ref(false)
const VueJsonPretty = ref(null)
const componentKey = ref(0)

const loadComponent = async () => {
  componentReady.value = false

  await nextTick()

  // Load component in background
  if (!VueJsonPretty.value) {
    const module = await import('vue-json-pretty')
    VueJsonPretty.value = markRaw(module.default)
  }

  // Small delay to ensure component is fully initialized
  await new Promise(resolve => setTimeout(resolve, 0))

  // Update key to force re-render
  componentKey.value++
  componentReady.value = true
}

onMounted(loadComponent)
watch(() => props.data, loadComponent)
</script>

<template>
  <div>
    <div v-if="!componentReady">Loading...</div>
    <component v-else :is="VueJsonPretty" :key="componentKey" :data="data" :deep="2" :collapsedNodeLength="100" :showLength="true" />
  </div>
</template>

<style>
.vjs-tree-node.is-highlight, .vjs-tree-node:hover {
  background-color: rgba(0, 149, 255, 0.1);
}
</style>
