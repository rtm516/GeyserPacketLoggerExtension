import { BackendWebSocket } from '@/utils/backend-websocket'

class ConnectionHandlerWrapper extends EventTarget {
  constructor () {
    super()

    this.connections = {}
    this.activeConnection = null

    BackendWebSocket.addEventListener('message-join', this.onJoin.bind(this))
    BackendWebSocket.addEventListener('message-auth', this.onAuth.bind(this))
    BackendWebSocket.addEventListener('message-packet', this.onPacket.bind(this))
    BackendWebSocket.addEventListener('message-leave', this.onLeave.bind(this))
  }

  ensureConnectionExists (connectionId) {
    if (this.connections[connectionId]) {
      return
    }

    this.connections[connectionId] = {
      id: connectionId,
      username: null,
      active: true,
      packets: []
    }

    this.dispatchEvent(new CustomEvent('connection-created', { detail: connectionId }))
  }

  onJoin (event) {
    const data = event.detail
    this.ensureConnectionExists(data.connectionId)
    this.connections[data.connectionId].active = true

    // this.dispatchEvent(new CustomEvent('connection-updated', { detail: data.connectionId }))
  }

  onAuth (event) {
    const data = event.detail

    this.ensureConnectionExists(data.connectionId)
    this.connections[data.connectionId].username = data.username

    this.dispatchEvent(new CustomEvent('connection-updated', { detail: data.connectionId }))
  }

  onPacket (event) {
    const data = Object.assign({}, event.detail)
    const connectionId = data.connectionId

    this.ensureConnectionExists(connectionId)

    delete data.connectionId
    this.connections[connectionId].packets.push(data)
  }

  onLeave (event) {
    const data = event.detail
    // TODO Not sure if we want to keep session data after leave
    // delete this.connections[leaveData.connectionId]
    if (!this.connections[data.connectionId]) {
      return
    }
    this.connections[data.connectionId].active = false

    this.dispatchEvent(new CustomEvent('connection-updated', { detail: data.connectionId }))
  }

  getConnectionIds () {
    return Object.keys(this.connections)
  }

  getConnection (connectionId) {
    return this.connections[connectionId]
  }

  getActiveConnectionId () {
    return this.activeConnectionId
  }

  getActiveConnection () {
    return this.connections[this.activeConnectionId] || null
  }

  setActiveConnection (connectionId) {
    this.activeConnectionId = connectionId

    this.dispatchEvent(new CustomEvent('active-connection-changed', { detail: connectionId }))
  }
}

export const ConnectionHandler = new ConnectionHandlerWrapper()
