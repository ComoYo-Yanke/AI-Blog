let ws = null
let reconnectTimer = null
let notificationCallback = null
let kickCallback = null
let pingInterval = null
let intentionalClose = false

export function connectWebSocket(onNotification, onKicked) {
  const token = localStorage.getItem('token')
  if (!token) return

  notificationCallback = onNotification
  kickCallback = onKicked
  intentionalClose = false

  function connect() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
      return
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const host = window.location.host

    ws = new WebSocket(`${protocol}//${host}/ws?token=${encodeURIComponent(token)}`)

    ws.onopen = () => {
      console.log('[WS] Connected')
      pingInterval = setInterval(() => {
        if (ws && ws.readyState === WebSocket.OPEN) {
          ws.send('ping')
        }
      }, 30000)
    }

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        if (data.type === 'kicked') {
          // 被顶号：强制下线
          if (kickCallback) kickCallback(data.message)
          return
        }
        // 普通通知消息
        if (notificationCallback) {
          notificationCallback(data)
        }
      } catch (e) {
        // ignore non-JSON (e.g. "pong")
      }
    }

    ws.onclose = () => {
      clearInterval(pingInterval)
      if (!intentionalClose) {
        console.log('[WS] Disconnected, reconnecting in 5s...')
        reconnectTimer = setTimeout(connect, 5000)
      }
    }

    ws.onerror = () => {
      ws?.close()
    }
  }

  connect()
}

export function disconnectWebSocket() {
  intentionalClose = true
  clearTimeout(reconnectTimer)
  clearInterval(pingInterval)
  if (ws) {
    ws.onclose = null
    ws.close()
    ws = null
  }
}
