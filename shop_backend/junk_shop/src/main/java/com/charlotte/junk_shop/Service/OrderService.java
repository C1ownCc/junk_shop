package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.Order;

import java.util.Map;

public interface OrderService {

    String createOrder(Order order);
    Map<String, Object> findOrders (Integer buyerID, Integer sellerID, String status, String itemName, int size, int page);
    int updateOrderStatus(Order order);
    String deleteOrderByID(int orderID);
}
