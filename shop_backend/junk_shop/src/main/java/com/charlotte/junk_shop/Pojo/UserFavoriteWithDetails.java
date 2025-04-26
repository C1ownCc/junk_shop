package com.charlotte.junk_shop.Pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserFavoriteWithDetails {
    private Integer favoriteID;
    private Integer userID;
    private Integer itemID;
    private Date createdAt;
    
    // 商品详情
    private String name;
    private String description;
    private Double price;
    private String category;
    private String condition;
    private Integer quantity;
    private String status;
    private Integer sellerID;
    
    // 商品图片
    private List<Item_image> images;
} 