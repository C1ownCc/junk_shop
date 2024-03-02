package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Dao.MessageMapper;
import com.charlotte.junk_shop.Pojo.Message;
import com.charlotte.junk_shop.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/getToMessage")
    public List<Message> getMessages(@RequestParam int toUserID, @RequestParam int fromUserID){

        return messageService.getMessage(fromUserID, toUserID);
    }

    @GetMapping("/getAllMessages")
    public Map<String, Object> getAllMessages(@RequestParam String content, @RequestParam int page, @RequestParam int size){
        if (content == null){
            content = "";
        }
        return messageService.getAllMessage(content, page, size);
    }

    @DeleteMapping("/deleteMessageByID")
    public String deleteMessageByID(@RequestParam int messageID){
        return messageService.deleteMessageByID(messageID);
    }
}
