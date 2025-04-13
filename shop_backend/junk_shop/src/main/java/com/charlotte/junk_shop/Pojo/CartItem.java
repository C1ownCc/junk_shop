package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;

@Data
public class CartItem {
    private int cartItemID;
    private int cartID;
    private int itemID;
    private int quantity;
    private Date addedAt;
} 