package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {

    Map<String, Object> getAllMessage(String content, int page, int size);
    List<Message> getMessage(int fromUserID, int toUserID);
    void insertMessage(Message message);
    String deleteMessageByID(int messageID);
}
