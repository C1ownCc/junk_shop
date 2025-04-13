package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    // 检查服务状态
    @GetMapping("/check")
    public Map<String, Object> checkService() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("time", new java.util.Date().toString());
        return result;
    }

    // 结算购物车
    @PostMapping("/checkout")
    public Map<String, Object> checkoutCart(@RequestBody Map<String, Object> requestData) {
        int userId = Integer.parseInt(requestData.get("userId").toString());
        
        @SuppressWarnings("unchecked")
        List<Integer> cartItemIds = (List<Integer>) requestData.get("cartItemIds");
        
        String recipientName = (String) requestData.get("recipientName");
        String address = (String) requestData.get("address");
        String phoneNumber = (String) requestData.get("phoneNumber");
        String message = requestData.get("message") != null ? (String) requestData.get("message") : "";

        System.out.println("接收到购物车结算请求: userId=" + userId 
                + ", 选中商品数量=" + (cartItemIds != null ? cartItemIds.size() : 0)
                + ", 收件人=" + recipientName);
        
        return shoppingCartService.checkoutCart(userId, cartItemIds, recipientName, address, phoneNumber, message);
    }
} 