package com.charlotte.junk_shop.Pojo;

import lombok.Data;

@Data
public class WebSocketMessage {
    private Long fromUserID;
    private Long toUserID;
    private String content;
    private Long timestamp;
} 