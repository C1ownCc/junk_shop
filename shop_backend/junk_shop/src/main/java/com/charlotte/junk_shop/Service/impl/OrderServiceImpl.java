package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Dao.OrderMapper;
import com.charlotte.junk_shop.Dao.UserMapper;
import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Pojo.User;
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
            return "pending order created:" + order.getOrderID();
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
    @Transactional
    public String payOrderWithWallet(int orderID, int buyerID) {
        // 根据订单ID获取订单信息
        Order order = orderMapper.selectById(orderID);
        if (order == null) {
            return "order not found";
        }
        
        if (!"待支付".equals(order.getOrderStatus())) {
            return "order status error: " + order.getOrderStatus();
        }
        
        // 检查用户余额是否足够
        User buyer = userMapper.findUserById(buyerID);
        if (buyer == null) {
            return "buyer not found";
        }
        
        if (buyer.getWalletBalance() < order.getPrice()) {
            return "insufficient balance";
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
            
            // 扣除买家钱包余额
            userMapper.sellerGiveFee(buyerID, order.getPrice());
            
            // 处理资金
            userMapper.adminGetFee(adminFee);
            userMapper.sellerGetFee(order.getSellerID(), sellerAmount);
            
            return "order paid successfully with wallet";
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
        
        // 如果两个ID都为0，说明是管理员查询所有订单
        if (buyerId == 0 && sellerId == 0) {
            System.out.println("管理员查询所有订单");
        }
        
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
            System.err.println("订单不存在: orderID=" + order.getOrderID());
            return 0;
        }

        System.out.println("订单状态更新: 订单ID=" + order.getOrderID() + 
                        ", 原状态=" + originalOrder.getOrderStatus() + 
                        ", 新状态=" + order.getOrderStatus());

        // 计算1%的金额
        double adminFee = originalOrder.getPrice() * 0.01; // 1% 手续费
        // 将adminFee的小数部分向上取整至两位小数
        adminFee = Math.ceil(adminFee * 100.0) / 100.0; // 向上取整并保留两位小数
        if (originalOrder.getPrice() < 1){
            adminFee = 0;
        }
        // 计算99%的金额
        double sellerAmount = originalOrder.getPrice() - adminFee; // 99%给卖家

        // 处理退款和恢复库存的情况
        if (order.getOrderStatus().equals("已取消")){
            System.out.println("订单将被取消: orderID=" + order.getOrderID());
            
            // 如果订单已支付或待发货或申请退款中并取消，需要退款
            if ("已支付".equals(originalOrder.getOrderStatus()) || 
                "待发货".equals(originalOrder.getOrderStatus()) || 
                "申请退款中".equals(originalOrder.getOrderStatus())) {
                
                System.out.println("触发退款流程: 原状态=" + originalOrder.getOrderStatus());
                
                // 无论支付方式，都处理平台和卖家资金，并退款给买家
                System.out.println("退还平台手续费: " + adminFee);
                userMapper.adminGiveFee(adminFee);
                
                System.out.println("退还卖家金额: 卖家ID=" + originalOrder.getSellerID() + 
                                ", 金额=" + sellerAmount);
                userMapper.sellerGiveFee(originalOrder.getSellerID(), sellerAmount);
                
                // 退款给买家
                System.out.println("退款给买家: 买家ID=" + originalOrder.getBuyerID() + 
                                ", 退款金额=" + originalOrder.getPrice());
                userMapper.sellerGetFee(originalOrder.getBuyerID(), originalOrder.getPrice());
                
                // 恢复库存
                int currentQuantity = itemMapper.getItemQuantity(originalOrder.getItemID());
                int newQuantity = currentQuantity + originalOrder.getQuantity();
                
                System.out.println("恢复商品库存: 商品ID=" + originalOrder.getItemID() + 
                                  ", 当前库存=" + currentQuantity + 
                                  ", 订单数量=" + originalOrder.getQuantity() + 
                                  ", 新库存=" + newQuantity);
                
                // 更新商品库存和状态
                int updateResult = itemMapper.updateQuantityAndStatus(originalOrder.getItemID(), newQuantity, "已上架");
                System.out.println("库存更新结果: " + (updateResult > 0 ? "成功" : "失败"));
            } else {
                System.out.println("订单为" + originalOrder.getOrderStatus() + "状态，不需要退款和恢复库存");
            }
        }
        
        int result = orderMapper.updateOrderStatus(order.getOrderID(), order.getOrderStatus());
        System.out.println("订单状态更新结果: " + (result > 0 ? "成功" : "失败"));
        return result;
    }

    @Override
    public String deleteOrderByID(int orderID) {
        return orderMapper.deleteOrderByID(orderID) > 0 ? "order deleted" : "delete error";
    }

    @Override
    public Order getOrderById(int orderID) {
        return orderMapper.selectById(orderID);
    }
}
