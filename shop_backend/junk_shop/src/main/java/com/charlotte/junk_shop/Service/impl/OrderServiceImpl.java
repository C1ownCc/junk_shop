package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Dao.OrderMapper;
import com.charlotte.junk_shop.Dao.UserMapper;
import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
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
            userMapper.adminGetFee(adminFee);
            userMapper.sellerGetFee(order.getSellerID(), sellerAmount);
            return "order created";
        }else{
            return "order create error";
        }

    }

    @Override
    public Map<String, Object> findOrders(Integer buyerID, Integer sellerID, String status, String itemName, int size, int page) {
        int offset = size * page;
        if (status.equals("全部")){
            status = null;
        }
        List<Order> orders = orderMapper.findOrders(buyerID, sellerID, status, itemName, offset, size);
        int total = orderMapper.findOrdersCount(buyerID, sellerID, status, itemName);
        Map<String, Object> res = new HashMap<>();
        res.put("orders", orders);
        res.put("total", total);
        return res;
    }

    @Override
    public int updateOrderStatus(Order order){

        // 计算1%的金额
        double adminFee = order.getPrice() * 0.01; // 1% 手续费
        // 将adminFee的小数部分向上取整至两位小数
        adminFee = Math.ceil(adminFee * 100.0) / 100.0; // 向上取整并保留两位小数
        if (order.getPrice() < 1){
            adminFee = 0;
        }
        // 计算99%的金额
        double sellerAmount = order.getPrice() - adminFee; // 99%给卖家

        if (order.getOrderStatus().equals("已取消")){
            itemMapper.changeStatus(order.getItemID(), "已上架");
            System.out.println("已修改商品状态为已上架");
            userMapper.adminGiveFee(adminFee);
            userMapper.sellerGiveFee(order.getSellerID(), sellerAmount);
            userMapper.sellerGetFee(order.getBuyerID(), order.getPrice());
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
