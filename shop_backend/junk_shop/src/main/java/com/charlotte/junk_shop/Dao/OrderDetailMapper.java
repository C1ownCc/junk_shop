package com.charlotte.junk_shop.Dao;

import com.charlotte.junk_shop.Pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    
    // 批量插入订单详情
    int batchInsertOrderDetails(List<OrderDetail> orderDetails);
    
    // 单条插入订单详情
    int insertOrderDetail(OrderDetail orderDetail);
    
    // 根据订单ID获取订单详情
    List<OrderDetail> getOrderDetailsByOrderId(@Param("orderID") int orderID);
    
    // 根据订单ID删除订单详情
    int deleteOrderDetailsByOrderId(@Param("orderID") int orderID);
} 