package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Item {

    private int itemID;
    private int sellerID;
    private String name;
    private String description;
    private double  price;
    private String category;
    private String condition;
    private int quantity;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}

