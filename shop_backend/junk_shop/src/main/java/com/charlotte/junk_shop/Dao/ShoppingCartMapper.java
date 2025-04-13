package com.charlotte.junk_shop.Dao;

import com.charlotte.junk_shop.Pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShoppingCartMapper {
    
    // 创建购物车
    int createCart(ShoppingCart cart);
    
    // 根据用户ID查找购物车
    ShoppingCart findCartByUserId(@Param("userID") int userID);
    
    // 更新购物车
    int updateCart(ShoppingCart cart);
    
    // 删除购物车
    int deleteCart(@Param("cartID") int cartID);
} 