package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.UserFavorite;
import com.charlotte.junk_shop.Pojo.UserFavoriteWithDetails;

import java.util.List;

public interface UserFavoriteService {
    // 添加收藏
    String addFavorite(Integer userID, Integer itemID);
    
    // 取消收藏
    String removeFavorite(Integer userID, Integer itemID);
    
    // 检查是否已收藏
    boolean isFavorite(Integer userID, Integer itemID);
    
    // 获取用户收藏列表
    List<UserFavoriteWithDetails> getUserFavorites(Integer userID);
    
    // 删除收藏
    String deleteFavorite(Integer favoriteID);
} 