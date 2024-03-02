package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.MessageMapper;
import com.charlotte.junk_shop.Pojo.Message;
import com.charlotte.junk_shop.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Map<String, Object> getAllMessage(String content, int page, int size) {
        int offset = size * page;
        List<Message> messages= messageMapper.getAllMessage(content, offset, size);
        int total = messageMapper.getMessageCount(content);
        Map<String, Object> res = new HashMap<>();
        res.put("messages", messages);
        res.put("total", total);
//        System.out.println(res);
        return res;
    }

    @Override
    public List<Message> getMessage(int fromUserID, int toUserID) {
        return messageMapper.getMessage(fromUserID, toUserID);
    }

    @Override
    public void insertMessage(Message message) {
        messageMapper.insertMessage(message);


    }

    @Override
    public String deleteMessageByID(int messageID) {
        int res = messageMapper.deleteMessageByID(messageID);
        return res>0? "success":"error";
    }
}
