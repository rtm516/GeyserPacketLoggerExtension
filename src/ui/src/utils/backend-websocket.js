/* global WebSocket, location */

class BackendWebSocketHandler extends EventTarget {
  constructor () {
    super()
    this.socket = null
    this.reconnectInterval = 5000

    // If we are in development mode, simulate a WebSocket connection with test data
    if (import.meta.env.DEV) {
      setTimeout(async () => {
        const testPackets = (await import('@/assets/test-packets.json')).default

        console.log('Debug mode enabled: Simulating WebSocket connection')
        // Send the join and auth data
        this.dispatchEvent(new CustomEvent('message-join', { detail: { connectionId: 'debug-connection' } }))
        this.dispatchEvent(new CustomEvent('message-auth', { detail: { connectionId: 'debug-connection', username: 'DebugUser' } }))

        // Send the test packets
        for (const packet of testPackets) {
          this.dispatchEvent(new CustomEvent('message-packet', {
            detail: Object.assign({
              connectionId: 'debug-connection'
            }, packet)
          }))
        }

        this.dispatchEvent(new CustomEvent('message-leave', { detail: { connectionId: 'debug-connection' } }))
      }, 100)
    }
  }

  connect () {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      console.log('WebSocket is already connected or connecting')
      return
    }

    console.log('Attempting to reconnect to WebSocket...')
    this.socket = new WebSocket('ws://' + location.host + '/api/messaging')

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
