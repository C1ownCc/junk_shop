package com.charlotte.junk_shop.Pojo;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private int userID;
    private String username;
    private String nickName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private double walletBalance;
    private String recipientName;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private Integer role;  // 用户角色：0 - 管理员，1 - 普通用户
    private String avatar;
}
