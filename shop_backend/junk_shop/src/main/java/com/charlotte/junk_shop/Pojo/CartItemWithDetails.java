package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CartItemWithDetails {
    private int cartItemID;
    private int cartID;
    private int itemID;
    private int quantity;
    private Date addedAt;
    
    // 商品详情
    private String itemName;
    private String description;
    private double price;
    private String category;
    private String condition;
    private String status;
    private int sellerID;
    private int availableQuantity; // 库存中可用的数量
    private String imageURL;
    private List<Item_image> images; // 商品图片列表
} 