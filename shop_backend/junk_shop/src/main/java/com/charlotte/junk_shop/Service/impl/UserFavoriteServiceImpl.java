package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.UserFavoriteMapper;
import com.charlotte.junk_shop.Pojo.UserFavorite;
import com.charlotte.junk_shop.Pojo.UserFavoriteWithDetails;
import com.charlotte.junk_shop.Service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    public String addFavorite(Integer userID, Integer itemID) {
        // 检查是否已收藏
        if (isFavorite(userID, itemID)) {
            return "已经收藏过该商品";
        }
        
        // 创建收藏记录
        UserFavorite favorite = new UserFavorite();
        favorite.setUserID(userID);
        favorite.setItemID(itemID);
        
        // 添加收藏
        int result = userFavoriteMapper.addFavorite(favorite);
        
        if (result > 0) {
            return "收藏成功";
        } else {
            return "收藏失败";
        }
    }

    @Override
    public String removeFavorite(Integer userID, Integer itemID) {
        // 检查是否已收藏
        if (!isFavorite(userID, itemID)) {
            return "未收藏该商品";
        }
        
        // 移除收藏
        int result = userFavoriteMapper.removeFavorite(userID, itemID);
        
        if (result > 0) {
            return "取消收藏成功";
        } else {
            return "取消收藏失败";
        }
    }

    @Override
    public boolean isFavorite(Integer userID, Integer itemID) {
        return userFavoriteMapper.checkFavorite(userID, itemID) > 0;
    }

    @Override
    public List<UserFavoriteWithDetails> getUserFavorites(Integer userID) {
        return userFavoriteMapper.getUserFavorites(userID);
    }

    @Override
    public String deleteFavorite(Integer favoriteID) {
        int result = userFavoriteMapper.deleteFavorite(favoriteID);
        
        if (result > 0) {
            return "删除收藏成功";
        } else {
            return "删除收藏失败";
        }
    }
} 