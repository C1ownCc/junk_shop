package com.charlotte.junk_shop.Dao;

import com.charlotte.junk_shop.Pojo.CartItem;
import com.charlotte.junk_shop.Pojo.CartItemWithDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {
    int addItemToCart(CartItem cartItem);
    
    CartItem checkItemInCart(@Param("cartID") int cartID, @Param("itemID") int itemID);
    
    List<CartItemWithDetails> getCartItemsWithDetails(@Param("cartID") int cartID);
    
    CartItemWithDetails getCartItemById(@Param("cartItemID") int cartItemID);
    
    List<CartItemWithDetails> getSelectedCartItems(@Param("cartItemIds") List<Integer> cartItemIds);
    
    int updateCartItemQuantity(@Param("cartItemID") int cartItemID, @Param("quantity") int quantity);
    
    int removeItemFromCart(@Param("cartItemID") int cartItemID);
    
    int clearCart(@Param("cartID") int cartID);
    
    int getCartItemCount(@Param("cartID") int cartID);
} 