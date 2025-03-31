package com.charlotte.junk_shop.config;

import com.charlotte.junk_shop.Pojo.Message;
import com.charlotte.junk_shop.Pojo.WebSocketMessage;
import com.charlotte.junk_shop.Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private MessageService messageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        SESSIONS.put(session.getId(), session);
        System.out.println("WebSocket连接建立: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("收到消息: " + payload);

        try {
            // 解析WebSocket消息
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            // 转换为数据库消息实体
            Message dbMessage = new Message();
            dbMessage.setFromUserID(wsMessage.getFromUserID().intValue());
            dbMessage.setToUserID(wsMessage.getToUserID().intValue());
            dbMessage.setContent(wsMessage.getContent());
            
            // 保存消息到数据库
            messageService.insertMessage(dbMessage);

            // 广播消息给所有连接的客户端
            for (WebSocketSession webSocketSession : SESSIONS.values()) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(message);
                }
            }
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SESSIONS.remove(session.getId());
        System.out.println("WebSocket连接关闭: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("WebSocket传输错误: " + exception.getMessage());
        exception.printStackTrace();
    }
} 