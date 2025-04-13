package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 添加商品到购物车
    @PostMapping("/add")
    public Map<String, Object> addItemToCart(
            @RequestParam("userId") int userId,
            @RequestParam("itemId") int itemId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        return shoppingCartService.addItemToCart(userId, itemId, quantity);
    }

    // 获取购物车内容
    @GetMapping("/contents")
    public Map<String, Object> getCartContents(@RequestParam("userId") int userId) {
        return shoppingCartService.getCartContents(userId);
    }

    // 更新购物车商品数量
    @PutMapping("/update")
    public Map<String, Object> updateItemQuantity(
            @RequestParam("cartItemId") int cartItemId,
            @RequestParam("quantity") int quantity) {
        return shoppingCartService.updateItemQuantity(cartItemId, quantity);
    }

    // 移除购物车商品
    @DeleteMapping("/remove")
    public Map<String, Object> removeItemFromCart(@RequestParam("cartItemId") int cartItemId) {
        return shoppingCartService.removeItemFromCart(cartItemId);
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public Map<String, Object> clearCart(@RequestParam("userId") int userId) {
        return shoppingCartService.clearCart(userId);
    }

    // 结算购物车
    @PostMapping("/checkout")
    public Map<String, Object> checkoutCart(
            @RequestParam("userId") int userId,
            @RequestParam("recipientName") String recipientName,
            @RequestParam("address") String address, 
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestBody(required = false) List<Integer> cartItemIds) {
        return shoppingCartService.checkoutCart(userId, cartItemIds, recipientName, address, phoneNumber);
    }
} 