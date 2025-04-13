package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Order {
    private int orderID;
    private int buyerID;
    private int itemID;
    private double price;
    private String orderStatus;
    private Date createdAt;
    private Date completedAt;
    private int sellerID;
    private String itemName;
    private int quantity;
    private String recipientName;
    private String address;
    private String phoneNumber;
    private String message;
}
