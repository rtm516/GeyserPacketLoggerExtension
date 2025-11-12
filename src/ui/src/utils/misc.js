import { onBeforeUnmount } from 'vue'

export function eventListener (target, type, listener) {
  target.addEventListener(type, listener)

  onBeforeUnmount(() => {
    target.removeEventListener(type, listener)
  })
}
