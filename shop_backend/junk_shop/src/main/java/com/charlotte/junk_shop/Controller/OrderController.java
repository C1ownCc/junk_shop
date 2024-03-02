package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Order;
import com.charlotte.junk_shop.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/userCreateOrder")
    public String createOrder(@RequestBody Order order){
        return orderService.createOrder(order);
    }

    @GetMapping("/getOrders")
    public Map<String, Object> findOrders(
            @RequestParam(required = false) Integer buyerID,
            @RequestParam(required = false) Integer sellerID,
            @RequestParam String status,
            @RequestParam String itemName,
            @RequestParam int size,
            @RequestParam int page
    ){
        if (buyerID == null || buyerID < 0){
            buyerID = 0;
        }
        if (sellerID == null || sellerID < 0){
            sellerID = 0;
        }
        if (itemName == null){
            itemName = "";
        }


//        System.out.println("buyerID: " + buyerID + ", sellerID: " + sellerID + ", status: " + status + ", itemName: " + itemName + ", size: " + size + ", page: " + page);
        return orderService.findOrders(buyerID, sellerID, status, itemName, size, page);
    }

    @PutMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestBody Order order){
//        System.out.println(order);
        int res = orderService.updateOrderStatus(order);
        return res > 0 ? "status updated":"status update error";
    }

    @DeleteMapping("/deleteOrderByID")
    public String deleteOrderByID(@RequestParam int orderID){
        return orderService.deleteOrderByID(orderID);
    }
}
