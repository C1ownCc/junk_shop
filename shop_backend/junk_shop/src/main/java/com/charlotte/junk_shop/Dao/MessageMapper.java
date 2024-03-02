package com.charlotte.junk_shop.Dao;

import com.charlotte.junk_shop.Pojo.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    List<Message> getAllMessage(String content, int offset, int size);
    int getMessageCount(String content);

    List<Message> getMessage(int fromUserID, int toUserID);

    @Insert("INSERT INTO messages(fromUserID,toUserID, content, SentAt) VALUES (#{fromUserID},#{toUserID},#{content},now())")
    @Options(useGeneratedKeys = true, keyProperty = "messageID")
    void insertMessage(Message message);

    @Delete("DELETE FROM messages WHERE messageID=#{messageID}")
    int deleteMessageByID(@Param("messageID") int messageID);
}
