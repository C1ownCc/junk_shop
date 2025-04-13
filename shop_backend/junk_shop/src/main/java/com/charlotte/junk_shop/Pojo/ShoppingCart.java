package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ShoppingCart {
    private int cartID;
    private int userID;
    private Date createdAt;
    private Date updatedAt;
} 