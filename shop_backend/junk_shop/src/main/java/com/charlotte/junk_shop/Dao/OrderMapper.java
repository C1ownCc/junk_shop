package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    int createOrder(Order order);

<<<<<<< HEAD
    // 根据ID查询订单
=======
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
    Order selectById(@Param("orderID") int orderID);

    List<Order> findOrders(@Param("buyerID") int buyerID, 
                          @Param("sellerID") int sellerID, 
                          @Param("status") String status, 
                          @Param("itemName") String itemName, 
                          @Param("offset") int offset, 
                          @Param("size") int size);
    
    int findOrdersCount(@Param("buyerID") int buyerID, 
                       @Param("sellerID") int sellerID, 
                       @Param("status") String status, 
                       @Param("itemName") String itemName);

    int updateOrderStatus(@Param("orderID") int orderID, @Param("status") String status);

    int deleteOrderByID(@Param("orderID") int orderID);
}
