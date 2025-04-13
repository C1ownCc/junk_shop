package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.CartItem;
import com.charlotte.junk_shop.Pojo.CartItemWithDetails;
import com.charlotte.junk_shop.Pojo.ShoppingCart;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {
    
    // 为用户创建购物车
    ShoppingCart createCartForUser(int userId);
    
    // 获取用户购物车（如果不存在则创建）
    ShoppingCart getOrCreateCart(int userId);
    
    // 添加商品到购物车
    Map<String, Object> addItemToCart(int userId, int itemId, int quantity);
    
    // 获取购物车内容
    Map<String, Object> getCartContents(int userId);
    
    // 更新购物车商品数量
    Map<String, Object> updateItemQuantity(int cartItemId, int quantity);
    
    // 从购物车移除商品
    Map<String, Object> removeItemFromCart(int cartItemId);
    
    // 清空购物车
    Map<String, Object> clearCart(int userId);
    
    // 批量结算购物车商品
    Map<String, Object> checkoutCart(int userId, List<Integer> cartItemIds, String recipientName, String address, String phoneNumber);
} 