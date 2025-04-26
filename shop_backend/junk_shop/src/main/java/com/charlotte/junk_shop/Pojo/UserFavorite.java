package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;

@Data
public class UserFavorite {
    private Integer favoriteID;
    private Integer userID;
    private Integer itemID;
    private Date createdAt;
} 