<script setup>
import Modal from "@/components/util/Modal.vue";
import { ref, useTemplateRef } from "vue";
import SettingInput from "@/components/util/SettingInput.vue";


// { "logToFile": { "value": true, "description": "Whether to log packets to a file", "type": "boolean" }, "enableWebserver": { "value": true, "description": "Whether to enable the webserver for live packet viewing", "type": "boolean" }, "webserverPort": { "value": 8082, "description": "The port the webserver will run on", "type": "integer" }, "ignoredPackets": { "value": [ "NetworkStackLatencyPacket", "LevelChunkPacket", "MovePlayerPacket", "PlayerAuthInputPacket", "NetworkChunkPublisherUpdatePacket", "ClientCacheBlobStatusPacket", "ClientCacheMissResponsePacket", "UpdateBlockPacket", "MoveEntityDeltaPacket", "MoveEntityAbsolutePacket", "SetEntityMotionPacket" ], "description": "List of packet class names to ignore", "type": "list" } }
/** @type { string: { value: any, description: string, type: string } } */
const settingsData = ref(null);
const settingsModal = useTemplateRef('settings-modal');

function _show() {
  fetch('/api/settings').then(response => response.json()).then(data => {
    settingsData.value = data;
  })

  settingsModal.value.show();
}
defineExpose({ show: _show });
</script>

<template>
  <Modal title="Settings" ref="settings-modal" dialog-classes="modal-dialog-scrollable modal-xl">
    <template #body>
      <div v-if="settingsData !== null">
        <SettingInput v-for="(setting, key) in settingsData" :key="key" :id="key" :setting="setting" />
      </div>
      <div v-else>
        Loading...
      </div>
    </template>
  </Modal>
</template>
