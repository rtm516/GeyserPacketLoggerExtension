// Source - https://stackoverflow.com/a/71461086
// Posted by OJay, modified by community. See post 'Timeline' for change history
// Retrieved 2025-11-21, License - CC BY-SA 4.0

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from "vue";
import { Modal } from "bootstrap";
defineProps({
  title: {
    type: String,
    default: "<<Title goes here>>",
  },
  dialogClasses: {
    type: String,
    default: "",
  },
});
let modalEle = ref(null);
let thisModalObj = null;

onMounted(() => {
  thisModalObj = new Modal(modalEle.value);
});
onUnmounted(() => {
  thisModalObj.dispose();
});
function _show() {
  thisModalObj.show();
}
defineExpose({ show: _show });
</script>

<template>
  <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="" aria-hidden="true" ref="modalEle">
    <div class="modal-dialog" :class="dialogClasses">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">{{ title }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <slot name="body"></slot>
        </div>
        <div class="modal-footer">
          <slot name="footer"></slot>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
