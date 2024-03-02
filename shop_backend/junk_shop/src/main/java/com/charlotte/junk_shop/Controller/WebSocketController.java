package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Dao.MessageMapper;
import com.charlotte.junk_shop.Pojo.Message;
import com.charlotte.junk_shop.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message send(Message message) throws  Exception{
//        System.out.println(message);
        messageService.insertMessage(message);
        return message;
    }
}
