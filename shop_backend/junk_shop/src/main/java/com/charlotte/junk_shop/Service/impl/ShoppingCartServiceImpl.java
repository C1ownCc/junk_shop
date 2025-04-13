package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.CartItemMapper;
import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Dao.ShoppingCartMapper;
import com.charlotte.junk_shop.Pojo.CartItem;
import com.charlotte.junk_shop.Pojo.CartItemWithDetails;
import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Pojo.ShoppingCart;
import com.charlotte.junk_shop.Service.OrderService;
import com.charlotte.junk_shop.Service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private OrderService orderService;

    @Override
    public ShoppingCart createCartForUser(int userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserID(userId);
        shoppingCartMapper.createCart(cart);
        return cart;
    }

    @Override
    public ShoppingCart getOrCreateCart(int userId) {
        ShoppingCart cart = shoppingCartMapper.findCartByUserId(userId);
        if (cart == null) {
            cart = createCartForUser(userId);
        }
        return cart;
    }

    @Override
    public Map<String, Object> addItemToCart(int userId, int itemId, int quantity) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取或创建购物车
            ShoppingCart cart = getOrCreateCart(userId);
            
            // 检查商品库存
            int currentStock = itemMapper.getItemQuantity(itemId);
            if (currentStock < quantity) {
                result.put("success", false);
                result.put("message", "商品库存不足，当前库存: " + currentStock);
                return result;
            }
            
            // 检查该商品是否已在购物车中
            CartItem existingItem = cartItemMapper.checkItemInCart(cart.getCartID(), itemId);
            
            if (existingItem != null) {
                // 如果已存在则检查库存是否足够
                int newQuantity = existingItem.getQuantity() + quantity;
                if (currentStock < newQuantity) {
                    result.put("success", false);
                    result.put("message", "商品库存不足，当前库存: " + currentStock);
                    return result;
                }
                
                // 更新数量
                cartItemMapper.updateCartItemQuantity(existingItem.getCartItemID(), newQuantity);
                result.put("message", "商品数量已更新");
            } else {
                // 否则添加新商品
                CartItem cartItem = new CartItem();
                cartItem.setCartID(cart.getCartID());
                cartItem.setItemID(itemId);
                cartItem.setQuantity(quantity);
                cartItemMapper.addItemToCart(cartItem);
                result.put("message", "商品已加入购物车");
            }
            
            result.put("success", true);
            result.put("cartItemCount", cartItemMapper.getCartItemCount(cart.getCartID()));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getCartContents(int userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取购物车
            ShoppingCart cart = shoppingCartMapper.findCartByUserId(userId);
            
            if (cart == null) {
                result.put("items", List.of());
                result.put("totalItems", 0);
                result.put("success", true);
                return result;
            }
            
            // 获取购物车商品
            List<CartItemWithDetails> items = cartItemMapper.getCartItemsWithDetails(cart.getCartID());
            
            // 检查每个商品的库存并更新可购买数量
            for (CartItemWithDetails item : items) {
                int currentStock = itemMapper.getItemQuantity(item.getItemID());
                item.setAvailableQuantity(Math.min(currentStock, item.getQuantity()));
            }
            
            result.put("items", items);
            result.put("totalItems", items.size());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取购物车失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> updateItemQuantity(int cartItemId, int quantity) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取购物车项
            CartItemWithDetails cartItem = cartItemMapper.getCartItemById(cartItemId);
            if (cartItem == null) {
                result.put("success", false);
                result.put("message", "购物车项不存在");
                return result;
            }
            
            // 检查库存
            int currentStock = itemMapper.getItemQuantity(cartItem.getItemID());
            if (currentStock < quantity) {
                result.put("success", false);
                result.put("message", "商品库存不足，当前库存: " + currentStock);
                return result;
            }
            
            int updated = cartItemMapper.updateCartItemQuantity(cartItemId, quantity);
            result.put("success", updated > 0);
            result.put("message", updated > 0 ? "数量已更新" : "更新失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> removeItemFromCart(int cartItemId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int removed = cartItemMapper.removeItemFromCart(cartItemId);
            result.put("success", removed > 0);
            result.put("message", removed > 0 ? "商品已移除" : "移除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "移除失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> clearCart(int userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ShoppingCart cart = shoppingCartMapper.findCartByUserId(userId);
            
            if (cart != null) {
                int cleared = cartItemMapper.clearCart(cart.getCartID());
                result.put("success", true);
                result.put("itemsRemoved", cleared);
                result.put("message", "购物车已清空");
            } else {
                result.put("success", true);
                result.put("message", "购物车为空");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清空购物车失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> checkoutCart(int userId, List<Integer> cartItemIds, String recipientName, String address, String phoneNumber) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取购物车
            ShoppingCart cart = shoppingCartMapper.findCartByUserId(userId);
            if (cart == null) {
                result.put("success", false);
                result.put("message", "购物车不存在");
                return result;
            }
            
            // 获取选中的购物车项
            List<CartItemWithDetails> selectedItems;
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                // 如果未指定，则获取所有购物车项
                selectedItems = cartItemMapper.getCartItemsWithDetails(cart.getCartID());
            } else {
                // 否则只获取指定的购物车项
                selectedItems = cartItemMapper.getSelectedCartItems(cartItemIds);
            }
            
            if (selectedItems.isEmpty()) {
                result.put("success", false);
                result.put("message", "未选中任何商品");
                return result;
            }
            
            // 检查库存并创建订单
            List<String> orderIds = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            
            for (CartItemWithDetails item : selectedItems) {
                // 检查库存是否足够
                int currentStock = itemMapper.getItemQuantity(item.getItemID());
                if (currentStock < item.getQuantity()) {
                    errorMessages.add(item.getItemName() + " 库存不足，当前库存: " + currentStock);
                    continue;
                }
                
                // 创建订单
                Order order = new Order();
                order.setBuyerID(userId);
                order.setItemID(item.getItemID());
                order.setSellerID(item.getSellerID());
                order.setItemName(item.getItemName());
                order.setQuantity(item.getQuantity());
                order.setPrice(item.getPrice() * item.getQuantity()); // 总价 = 单价 * 数量
                order.setRecipientName(recipientName);
                order.setAddress(address);
                order.setPhoneNumber(phoneNumber);
                
                // 创建未支付订单
                String createResult = orderService.createPendingOrder(order);
                if (createResult.contains("error")) {
                    errorMessages.add("创建 " + item.getItemName() + " 的订单失败: " + createResult);
                } else {
                    orderIds.add(String.valueOf(order.getOrderID()));
                    
                    // 从购物车中移除已下单的商品
                    cartItemMapper.removeItemFromCart(item.getCartItemID());
                }
            }
            
            result.put("success", !orderIds.isEmpty());
            result.put("orderIds", orderIds);
            
            if (!errorMessages.isEmpty()) {
                result.put("errors", errorMessages);
                result.put("message", "部分商品下单失败，请查看详细信息");
            } else if (!orderIds.isEmpty()) {
                result.put("message", "订单创建成功，请尽快支付");
            } else {
                result.put("message", "所有商品下单失败");
            }
            
            result.put("ordersCreated", !orderIds.isEmpty());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "结算失败: " + e.getMessage());
        }
        
        return result;
    }
} 