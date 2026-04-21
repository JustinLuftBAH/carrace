import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.subscriptions = {}
  }

  connect(onConnected, onError) {
    const socket = new SockJS('/ws')
    this.stompClient = Stomp.over(socket)
    
    // Disable debug logs
    this.stompClient.debug = null
    
    this.stompClient.connect(
      {},
      () => {
        console.log('WebSocket connected')
        if (onConnected) onConnected()
      },
      (error) => {
        console.error('WebSocket connection error:', error)
        if (onError) onError(error)
      }
    )
  }

  disconnect() {
    if (this.stompClient && this.stompClient.connected) {
      Object.values(this.subscriptions).forEach(sub => sub.unsubscribe())
      this.subscriptions = {}
      this.stompClient.disconnect()
      console.log('WebSocket disconnected')
    }
  }

  subscribeToRace(raceId, callback) {
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/topic/race/${raceId}`
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const data = JSON.parse(message.body)
      callback(data)
    })

    this.subscriptions[destination] = subscription
    return subscription
  }

  subscribeToLobby(raceId, callback) {
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/topic/lobby/${raceId}`
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const data = JSON.parse(message.body)
      callback(data)
    })

    this.subscriptions[destination] = subscription
    return subscription
  }

  unsubscribe(destination) {
    if (this.subscriptions[destination]) {
      this.subscriptions[destination].unsubscribe()
      delete this.subscriptions[destination]
    }
  }

  isConnected() {
    return this.stompClient && this.stompClient.connected
  }
}

export default new WebSocketService()
