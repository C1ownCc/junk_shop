package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Message;
import com.charlotte.junk_shop.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    // WebSocket的消息处理已经移到WebSocketHandler中
    // 这个控制器现在只用于处理HTTP请求
}
