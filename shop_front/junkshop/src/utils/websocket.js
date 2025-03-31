// WebSocket地址
const url = 'ws://localhost:8080/chat'

// WebSocket实例
let ws = null

// 重连定时器实例
let reconnectTimer = null

// WebSocket重连开关
let isReconnecting = false

// 消息处理回调函数
let messageCallback = null

// WebSocket对象
const websocket = {
  // 初始化WebSocket连接
  init(callback) {
    if (messageCallback === null) {
      messageCallback = callback
    }
    
    this.connection()
  },

  // 建立WebSocket连接
  connection() {
    if (!window.WebSocket) {
      console.log('您的浏览器不支持WebSocket')
      return
    }

    ws = new WebSocket(url)

    // 监听WebSocket连接打开
    ws.onopen = () => {
      console.log('WebSocket连接成功')
      // 连接成功后关闭重连
      isReconnecting = false
      clearTimeout(reconnectTimer)
    }

    // 监听WebSocket错误信息
    ws.onerror = (e) => {
      console.log('WebSocket连接发生错误', e)
      this.reconnect()
    }

    // 监听WebSocket关闭
    ws.onclose = (e) => {
      console.log('WebSocket连接关闭', e)
      this.reconnect()
    }

    // 监听WebSocket消息
    ws.onmessage = (e) => {
      // 调用回调函数处理消息
      if (messageCallback) {
        messageCallback(e.data)
      }
    }
  },

  // 重新连接
  reconnect() {
    if (isReconnecting) return
    isReconnecting = true
    
    // 清除之前的重连定时器
    clearTimeout(reconnectTimer)
    
    // 设置重连定时器
    reconnectTimer = setTimeout(() => {
      console.log('WebSocket重新连接...')
      this.connection()
      isReconnecting = false
    }, 3000)
  },

  // 发送消息
  send(data) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(data)
      return true
    }
    console.log('WebSocket连接未建立，无法发送消息')
    return false
  },

  // 关闭连接
  close() {
    if (ws) {
      ws.close()
      ws = null
      messageCallback = null
      clearTimeout(reconnectTimer)
    }
  }
}

export default websocket 