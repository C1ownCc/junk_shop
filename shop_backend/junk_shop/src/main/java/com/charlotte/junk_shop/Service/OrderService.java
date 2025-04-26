package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    String createOrder(Order order);
    
    String createPendingOrder(Order order);
    
    String payOrder(int orderID);
    
    String payOrderWithWallet(int orderID, int buyerID);
    
    Map<String, Object> findOrders (Integer buyerID, Integer sellerID, String status, String itemName, int size, int page);
    
    int updateOrderStatus(Order order);
    
    String deleteOrderByID(int orderID);
    
    Order getOrderById(int orderID);
}
