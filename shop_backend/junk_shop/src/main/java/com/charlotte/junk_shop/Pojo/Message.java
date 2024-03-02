package com.charlotte.junk_shop.Pojo;

import lombok.Data;


import java.util.Date;

@Data
public class Message {

    private int messageID;
    private int fromUserID;
    private int toUserID;
    private String content;
    private Date sentAt;

}
