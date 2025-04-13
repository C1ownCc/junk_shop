package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Dao.OrderMapper;
import com.charlotte.junk_shop.Dao.UserMapper;
import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    @Transactional
    public String createOrder(Order order) {
        order.setOrderStatus("已支付");
        order.setCreatedAt(new Date());
        order.setCompletedAt(new Date());
        // 计算1%的金额
        double adminFee = order.getPrice() * 0.01; // 1% 手续费
        // 将adminFee的小数部分向上取整至两位小数
        adminFee = Math.ceil(adminFee * 100.0) / 100.0; // 向上取整并保留两位小数
        if (order.getPrice() < 1){
            adminFee = 0;
        }
        // 计算99%的金额
        double sellerAmount = order.getPrice() - adminFee; // 99%给卖家
        int res = orderMapper.createOrder(order);
        if (res > 0){
            // 减少商品库存
            int currentQuantity = itemMapper.getItemQuantity(order.getItemID());
            int newQuantity = currentQuantity - order.getQuantity();
            // 更新商品库存和状态
            if (newQuantity <= 0) {
                itemMapper.updateQuantityAndStatus(order.getItemID(), 0, "已售出");
            } else {
                itemMapper.updateQuantityAndStatus(order.getItemID(), newQuantity, "已上架");
            }
            // 处理资金
            userMapper.adminGetFee(adminFee);
            userMapper.sellerGetFee(order.getSellerID(), sellerAmount);
            return "order created";
        }else{
            return "order create error";
        }
    }
    
    @Override
    @Transactional
    public String createPendingOrder(Order order) {
        order.setOrderStatus("待支付");
        order.setCreatedAt(new Date());
        order.setCompletedAt(new Date()); // 将CompletedAt设置为当前日期，而不是null
        
        int res = orderMapper.createOrder(order);
        if (res > 0){
            return "pending order created";
        }else{
            return "pending order create error";
        }
    }
    
    @Override
    @Transactional
    public String payOrder(int orderID) {
        // 根据订单ID获取订单信息
        Order order = orderMapper.selectById(orderID);
        if (order == null) {
            return "order not found";
        }
        
        if (!"待支付".equals(order.getOrderStatus())) {
            return "order status error: " + order.getOrderStatus();
        }
        
        // 检查商品库存
        int currentQuantity = itemMapper.getItemQuantity(order.getItemID());
        if (currentQuantity < order.getQuantity()) {
            return "insufficient inventory";
        }
        
        // 计算手续费
        double adminFee = order.getPrice() * 0.01; // 1% 手续费
        adminFee = Math.ceil(adminFee * 100.0) / 100.0; // 向上取整并保留两位小数
        if (order.getPrice() < 1) {
            adminFee = 0;
        }
        // 计算卖家所得
        double sellerAmount = order.getPrice() - adminFee;
        
        // 更新订单状态
        order.setOrderStatus("已支付");
        order.setCompletedAt(new Date());
        int updateResult = orderMapper.updateOrderStatus(order.getOrderID(), "已支付");
        
        if (updateResult > 0) {
            // 减少商品库存
            int newQuantity = currentQuantity - order.getQuantity();
            if (newQuantity <= 0) {
                itemMapper.updateQuantityAndStatus(order.getItemID(), 0, "已售出");
            } else {
                itemMapper.updateQuantityAndStatus(order.getItemID(), newQuantity, "已上架");
            }
            
            // 处理资金
            userMapper.adminGetFee(adminFee);
            userMapper.sellerGetFee(order.getSellerID(), sellerAmount);
            
            return "order paid successfully";
        } else {
            return "order payment failed";
        }
    }

    @Override
    public Map<String, Object> findOrders(Integer buyerID, Integer sellerID, String status, String itemName, int size, int page) {
        System.out.println("OrderServiceImpl.findOrders - 开始查询订单: buyerID=" + buyerID 
                + ", sellerID=" + sellerID + ", status=" + status);
        
        int offset = size * page;
        if ("全部".equals(status)){
            status = null;
            System.out.println("状态为'全部'，设置为null进行查询");
        }
        
        // 处理可能为null的buyerID和sellerID
        int buyerId = (buyerID == null) ? 0 : buyerID;
        int sellerId = (sellerID == null) ? 0 : sellerID;
        
        System.out.println("转换后的buyerId=" + buyerId + ", sellerId=" + sellerId);
        
        List<Order> orders = null;
        int total = 0;
        try {
            orders = orderMapper.findOrders(buyerId, sellerId, status, itemName, offset, size);
            total = orderMapper.findOrdersCount(buyerId, sellerId, status, itemName);
            System.out.println("查询到的订单数量: " + (orders != null ? orders.size() : "null") 
                    + ", 总记录数: " + total);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("查询订单时发生异常: " + e.getMessage());
        }
        
        Map<String, Object> res = new HashMap<>();
        res.put("orders", orders != null ? orders : new ArrayList<>());
        res.put("total", total);
        
        System.out.println("返回查询结果: orders=" + (orders != null ? orders.size() : "null") 
                + "条记录, total=" + total);
        return res;
    }

    @Override
    @Transactional
    public int updateOrderStatus(Order order){
        // 获取原订单信息
        Order originalOrder = orderMapper.selectById(order.getOrderID());
        if (originalOrder == null) {
            return 0;
        }

        // 计算1%的金额
        double adminFee = originalOrder.getPrice() * 0.01; // 1% 手续费
        // 将adminFee的小数部分向上取整至两位小数
        adminFee = Math.ceil(adminFee * 100.0) / 100.0; // 向上取整并保留两位小数
        if (originalOrder.getPrice() < 1){
            adminFee = 0;
        }
        // 计算99%的金额
        double sellerAmount = originalOrder.getPrice() - adminFee; // 99%给卖家

        if (order.getOrderStatus().equals("已取消")){
            // 如果订单已支付并取消，需要退款
            if ("已支付".equals(originalOrder.getOrderStatus())) {
                // 退还资金
                userMapper.adminGiveFee(adminFee);
                userMapper.sellerGiveFee(originalOrder.getSellerID(), sellerAmount);
                userMapper.sellerGetFee(originalOrder.getBuyerID(), originalOrder.getPrice());
                
                // 恢复库存
                int currentQuantity = itemMapper.getItemQuantity(originalOrder.getItemID());
                int newQuantity = currentQuantity + originalOrder.getQuantity();
                itemMapper.updateQuantityAndStatus(originalOrder.getItemID(), newQuantity, "已上架");
            } 
            // 如果订单状态为待支付并取消，不需要进行退款操作，只需要更新状态
        }
        
        return orderMapper.updateOrderStatus(order.getOrderID(), order.getOrderStatus());
    }

    @Override
    public String deleteOrderByID(int orderID) {
        int res = orderMapper.deleteOrderByID(orderID);
        if (res > 0){
            return "order deleted";
        }else{
            return "order delete error";
        }
    }
}
