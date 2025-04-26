package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

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
        if (sellerID == null && buyerID == null){
            System.out.println("买家ID和卖家ID都为空，返回null");
            return null;
        }
        
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
        int res = orderService.updateOrderStatus(order);
        if (res > 0){
            return "status updated";
        }else{
            return "order update error";
        }
    }

    @DeleteMapping("/deleteOrderByID")
    public String deleteOrderByID(@RequestParam int orderID){
        return orderService.deleteOrderByID(orderID);
    }
}
