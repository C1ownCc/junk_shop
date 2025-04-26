package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Service.OrderService;
import com.charlotte.junk_shop.Dao.OrderDetailMapper;
import com.charlotte.junk_shop.Pojo.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.annotation.Resource;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @PostMapping("/userCreateOrder")
    public String createOrder(@RequestBody Order order){
        return orderService.createOrder(order);
    }
    
    @PostMapping("/userCreatePendingOrder")
    public String createPendingOrder(@RequestBody Order order){
        return orderService.createPendingOrder(order);
    }
    
    @PostMapping("/payOrder/{orderID}")
    public String payOrder(@PathVariable int orderID){
        return orderService.payOrder(orderID);
    }
    
    @PostMapping("/payOrderWithWallet")
    public String payOrderWithWallet(@RequestParam int orderID, @RequestParam int buyerID){
        return orderService.payOrderWithWallet(orderID, buyerID);
    }

    @GetMapping("/getOrders")
    public Map<String, Object> findOrders(
            @RequestParam(value = "buyerID", required = false) Integer buyerID,
            @RequestParam(value = "sellerID", required = false) Integer sellerID,
            @RequestParam(value = "status", required = false, defaultValue = "全部") String status,
            @RequestParam(value = "itemName", required = false, defaultValue = "") String itemName,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page
    ){
        System.out.println("接收到订单查询请求: buyerID=" + buyerID + ", sellerID=" + sellerID 
                + ", status=" + status + ", itemName=" + itemName 
                + ", size=" + size + ", page=" + page);
        
        if ("".equals(itemName)){
            itemName = null;
        }
        
        // 允许查询所有订单（管理员权限）
        Map<String, Object> result = orderService.findOrders(buyerID, sellerID, status, itemName, size, page);
        System.out.println("查询结果: " + (result != null ? 
                "订单数量=" + (result.get("total") != null ? result.get("total") : "null") + 
                ", 订单列表=" + (result.get("orders") != null ? 
                        ((List)result.get("orders")).size() + "条" : "null") 
                : "null"));
        
        return result;
    }

    @PutMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestBody Order order){
        System.out.println("接收到订单状态更新请求: orderID=" + order.getOrderID() + 
                       ", newStatus=" + order.getOrderStatus());
        
        int res = orderService.updateOrderStatus(order);
        if (res > 0){
            return "status updated";
        }else{
            return "order update error";
        }
    }

    @DeleteMapping("/deleteOrderByID")
    public String deleteOrderByID(@RequestParam("orderID") int orderID){
        return orderService.deleteOrderByID(orderID);
    }

    @GetMapping("/orderDetails/{orderId}")
    public List<OrderDetail> getOrderDetails(@PathVariable("orderId") int orderId) {
        System.out.println("接收到获取订单详情请求，订单ID: " + orderId);
        List<OrderDetail> details = orderDetailMapper.getOrderDetailsByOrderId(orderId);
        System.out.println("查询到订单详情数量: " + (details != null ? details.size() : 0));
        return details;
    }
}
