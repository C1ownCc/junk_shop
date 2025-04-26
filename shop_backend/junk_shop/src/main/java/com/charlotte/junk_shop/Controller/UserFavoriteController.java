package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.UserFavoriteWithDetails;
import com.charlotte.junk_shop.Service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Map<String, Object> addFavorite(@RequestParam("userID") Integer userID, 
                                         @RequestParam("itemID") Integer itemID) {
        Map<String, Object> result = new HashMap<>();
        String message = userFavoriteService.addFavorite(userID, itemID);
        
        if (message.contains("成功")) {
            result.put("success", true);
            result.put("message", message);
        } else {
            result.put("success", false);
            result.put("message", message);
        }
        
        return result;
    }

    /**
     * 取消收藏
     */
    @PostMapping("/remove")
    public Map<String, Object> removeFavorite(@RequestParam("userID") Integer userID, 
                                            @RequestParam("itemID") Integer itemID) {
        Map<String, Object> result = new HashMap<>();
        String message = userFavoriteService.removeFavorite(userID, itemID);
        
        if (message.contains("成功")) {
            result.put("success", true);
            result.put("message", message);
        } else {
            result.put("success", false);
            result.put("message", message);
        }
        
        return result;
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Map<String, Object> checkFavorite(@RequestParam("userID") Integer userID, 
                                           @RequestParam("itemID") Integer itemID) {
        Map<String, Object> result = new HashMap<>();
        boolean isFavorite = userFavoriteService.isFavorite(userID, itemID);
        
        result.put("isFavorite", isFavorite);
        
        return result;
    }

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/list")
    public Map<String, Object> getUserFavorites(@RequestParam("userID") Integer userID) {
        Map<String, Object> result = new HashMap<>();
        List<UserFavoriteWithDetails> favorites = userFavoriteService.getUserFavorites(userID);
        
        result.put("favorites", favorites);
        result.put("total", favorites.size());
        
        return result;
    }

    /**
     * 删除收藏
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteFavorite(@RequestParam("favoriteID") Integer favoriteID) {
        Map<String, Object> result = new HashMap<>();
        String message = userFavoriteService.deleteFavorite(favoriteID);
        
        if (message.contains("成功")) {
            result.put("success", true);
            result.put("message", message);
        } else {
            result.put("success", false);
            result.put("message", message);
        }
        
        return result;
    }
} 