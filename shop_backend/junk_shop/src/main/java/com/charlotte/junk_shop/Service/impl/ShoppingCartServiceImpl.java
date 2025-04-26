package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.CartItemMapper;
import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Dao.OrderDetailMapper;
import com.charlotte.junk_shop.Dao.ShoppingCartMapper;
import com.charlotte.junk_shop.Pojo.CartItem;
import com.charlotte.junk_shop.Pojo.CartItemWithDetails;
import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Pojo.OrderDetail;
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
    
    @Autowired
    private OrderDetailMapper orderDetailMapper;

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
    public Map<String, Object> checkoutCart(int userId, List<Integer> cartItemIds, String recipientName, String address, String phoneNumber, String message) {
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
            
            // 检查库存并按卖家分组创建订单
            List<Integer> orderIds = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            Map<Integer, List<OrderDetail>> orderDetails = new HashMap<>(); // 保存每个订单的详情
            
            // 按卖家ID分组商品
            Map<Integer, List<CartItemWithDetails>> sellerGroups = new HashMap<>();
            
            for (CartItemWithDetails item : selectedItems) {
                // 检查库存是否足够
                int currentStock = itemMapper.getItemQuantity(item.getItemID());
                if (currentStock < item.getQuantity()) {
                    errorMessages.add(item.getItemName() + " 库存不足，当前库存: " + currentStock);
                    continue;
                }
                
                // 按卖家ID分组
                sellerGroups.computeIfAbsent(item.getSellerID(), k -> new ArrayList<>()).add(item);
            }
            
            System.out.println("按卖家分组后共有 " + sellerGroups.size() + " 个卖家的商品");
            
            // 为每个卖家创建一个订单
            for (Map.Entry<Integer, List<CartItemWithDetails>> entry : sellerGroups.entrySet()) {
                int sellerId = entry.getKey();
                List<CartItemWithDetails> sellerItems = entry.getValue();
                
                // 计算该卖家所有商品的总价
                double totalPrice = 0;
                for (CartItemWithDetails item : sellerItems) {
                    totalPrice += item.getPrice() * item.getQuantity();
                }
                
                // 修改：订单的主要商品可以是任意一个，不一定是第一个
                String itemsDescription = sellerItems.size() == 1 ? 
                        sellerItems.get(0).getItemName() : 
                        sellerItems.get(0).getItemName() + " 等多件商品";
                
                // 创建订单
                Order order = new Order();
                order.setBuyerID(userId);
                order.setItemID(sellerItems.get(0).getItemID()); // 需要保留一个itemID
                order.setSellerID(sellerId);
                order.setItemName(itemsDescription); // 订单标题
                order.setQuantity(sellerItems.stream().mapToInt(CartItemWithDetails::getQuantity).sum()); // 总商品数量
                order.setPrice(totalPrice); // 总价
                order.setRecipientName(recipientName);
                order.setAddress(address);
                order.setPhoneNumber(phoneNumber);
                order.setMessage(message);
                
                System.out.println("为卖家 " + sellerId + " 创建订单，包含 " + sellerItems.size() + 
                        " 种商品，总共 " + order.getQuantity() + " 件，总价: " + totalPrice);
                
                // 创建未支付订单
                String createResult = orderService.createPendingOrder(order);
                if (createResult.contains("error")) {
                    errorMessages.add("创建 " + order.getItemName() + " 的订单失败: " + createResult);
                } else {
                    // 获取创建的订单ID
                    int orderId = order.getOrderID();
                    System.out.println("创建订单成功，订单ID: " + orderId);
                    
                    // 在数据库中再次查询确认订单ID
                    Order createdOrder = orderService.getOrderById(orderId);
                    if (createdOrder != null) {
                        orderId = createdOrder.getOrderID();
                        System.out.println("数据库中确认的订单ID: " + orderId);
                    } else {
                        System.err.println("无法在数据库中找到刚创建的订单，ID: " + orderId);
                    }
                    
                    orderIds.add(orderId);
                    
                    // 创建订单详情
                    List<OrderDetail> details = new ArrayList<>();
                    for (CartItemWithDetails item : sellerItems) {
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderID(orderId);
                        detail.setItemID(item.getItemID());
                        detail.setItemName(item.getItemName());
                        detail.setItemPrice(item.getPrice());
                        detail.setQuantity(item.getQuantity());
                        // 设置商品图片（如果有）
                        if (item.getImages() != null && !item.getImages().isEmpty()) {
                            detail.setItemImage(item.getImages().get(0).getImageURL());
                        }
                        details.add(detail);
                    }
                    
                    // 批量插入订单详情
                    if (!details.isEmpty()) {
                        try {
                            int insertCount = orderDetailMapper.batchInsertOrderDetails(details);
                            System.out.println("为订单 " + orderId + " 创建了 " + insertCount + " 条详情记录");
                            orderDetails.put(orderId, details);
                        } catch (Exception e) {
                            System.err.println("批量保存订单详情失败: " + e.getMessage());
                            e.printStackTrace();
                            
                            // 尝试单条插入
                            System.out.println("尝试单条插入订单详情...");
                            int successCount = 0;
                            for (OrderDetail detail : details) {
                                try {
                                    int insertResult = orderDetailMapper.insertOrderDetail(detail);
                                    if (insertResult > 0) {
                                        successCount++;
                                    }
                                } catch (Exception ex) {
                                    System.err.println("单条插入订单详情失败: " + ex.getMessage());
                                }
                            }
                            
                            if (successCount > 0) {
                                System.out.println("单条插入成功 " + successCount + " 条详情记录");
                                orderDetails.put(orderId, details);
                            }
                        }
                    }
                    
                    // 从购物车中移除已下单的商品
                    for (CartItemWithDetails item : sellerItems) {
                        cartItemMapper.removeItemFromCart(item.getCartItemID());
                    }
                }
            }
            
            result.put("success", !orderIds.isEmpty());
            result.put("orderIds", orderIds);
            result.put("orderDetails", orderDetails);
            
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
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "结算失败: " + e.getMessage());
        }
        
        return result;
    }
} 