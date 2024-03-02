package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Insert("INSERT INTO orders" +
            "(BuyerID, ItemID, Price, OrderStatus, CreatedAt, CompletedAt, SellerID, ItemName, recipientName, address, phoneNumber) " +
            "values" +
            "(#{buyerID}, #{itemID}, #{price}, #{orderStatus}, #{createdAt}, #{completedAt}, #{sellerID}, #{itemName}, #{recipientName}, #{address}, #{phoneNumber})")
    int createOrder(Order order);

    List<Order> findOrders( int buyerID, int sellerID, String status, String itemName, int offset, int size);
    int findOrdersCount(int buyerID, int sellerID, String status, String itemName);

    @Update("UPDATE orders SET OrderStatus = #{status} , CompletedAt = now() WHERE OrderID = #{orderID}")
    int updateOrderStatus(@Param("orderID") int orderID,@Param("status") String status);

    @Delete("DELETE FROM orders WHERE OrderID = #{orderID} && OrderStatus = '已取消'")
    int deleteOrderByID(@Param("orderID") int orderID);
}
