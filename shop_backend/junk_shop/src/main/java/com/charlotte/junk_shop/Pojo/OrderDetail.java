package com.charlotte.junk_shop.Pojo;

import lombok.Data;

@Data
public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private int itemID;
    private String itemName;
    private double itemPrice;
    private int quantity;
    private String itemImage;
} 