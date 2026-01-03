<script setup>
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  setting: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <div class="mb-3" :class="{'form-check form-switch': setting.type === 'boolean'}">
    <label :for="`setting-input-${id}`" class="form-label">{{ id }}</label>
    <div v-if="setting.type !== 'boolean'" class="form-text">{{ setting.description }}</div>
    <input
      v-if="setting.type === 'string'"
      type="text"
      class="form-control"
      :id="`setting-input-${id}`"
      v-model="setting.value"
    />
    <input
      v-else-if="setting.type === 'integer'"
      type="number"
      class="form-control"
      :id="`setting-input-${id}`"
      v-model.number="setting.value"
    />
    <input
      v-else-if="setting.type === 'boolean'"
      type="checkbox"
      role="switch"
      class="form-check-input"
      :id="`setting-input-${id}`"
      v-model="setting.value"
    />
    <input
      v-else-if="setting.type === 'list'"
      type="text"
      class="form-control"
      :id="`setting-input-${id}`"
      v-model="setting.value"
    />
    <div v-else class="text-danger">Unsupported setting type: {{ setting.type }}</div>
    <div v-if="setting.type === 'boolean'" class="form-text">{{ setting.description }}</div>
  </div>
</template>
