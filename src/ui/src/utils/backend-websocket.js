/* global WebSocket, location */
import { onBeforeUnmount } from 'vue'

class BackendWebSocketHandler extends EventTarget {
  constructor () {
    super()
    this.socket = null
    this.reconnectInterval = 5000
  }

  connect () {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      console.log('WebSocket is already connected or connecting')
      return
    }

    console.log('Attempting to reconnect to WebSocket...')
    this.socket = new WebSocket('ws://' + location.host + '/messaging')

    this.socket.onopen = (event) => {
      console.log('Connected to WebSocket')
      this.dispatchEvent(new CustomEvent('connected', { detail: event }))
    }

    this.socket.onmessage = (event) => {
      // Parse and dispatch custom events based on message type
      const data = JSON.parse(event.data)
      this.dispatchEvent(new CustomEvent('message-' + data.type, { detail: data.data }))
    }

    this.socket.onerror = (event) => {
      console.error('WebSocket error:', event)
      this.dispatchEvent(new CustomEvent('error', { detail: event }))
    }

    this.socket.onclose = (event) => {
      console.log('Disconnected from WebSocket')
      setTimeout(() => this.connect(), this.reconnectInterval)
      this.dispatchEvent(new CustomEvent('disconnected', { detail: event }))
    }
  }

  isConnected () {
    return this.socket && this.socket.readyState === WebSocket.OPEN
  }
}

export const BackendWebSocket = new BackendWebSocketHandler()
