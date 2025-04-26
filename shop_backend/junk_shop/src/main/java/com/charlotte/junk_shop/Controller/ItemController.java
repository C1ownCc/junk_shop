package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Item;
import com.charlotte.junk_shop.Pojo.ItemWithImages;
import com.charlotte.junk_shop.Service.ItemImageService;
import com.charlotte.junk_shop.Service.ItemService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemImageService itemImageService;

    @PostMapping("/userInsertItem")
    public ResponseEntity<?> insertItem1(@RequestBody Item item){
        item.setStatus("待审核");
        item.setCreatedAt(new Date());
        item.setUpdatedAt(new Date());

        itemService.insertItem(item);
        return ResponseEntity.ok(item.getItemID());
    }



    @GetMapping("/adminGetAllItems")
    public Map<String, Object> findAllItemsWithPagination(@RequestParam("page") int page, @RequestParam("size") int size){
        return itemService.findAllItemsWithPagination(page, size);
    }

    @GetMapping("/findItemsByName")
    public Map<String, Object> findItemsByName(
            @RequestParam String name,
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size
    ){
        return itemService.findItemsWithSearch(name,status, page, size);
    }

    @DeleteMapping("/deleteItemById")
    public String deleteItemById(@RequestParam int id){
        int res2 = itemImageService.deleteImageByItemID(id);
        int res1 = itemService.deleteItemById(id);

        return (res1 > 0) ? "item deleted" : "item delete error";
    }

    @GetMapping("/userGetAllSellerItems")
    public Map<String, Object> findItems(@RequestParam int page, @RequestParam int size, @RequestParam int sellerID){
        return itemService.findAllSellerItems(page, size, sellerID);
    }

    @GetMapping("/userFindItemByName")
    public Map<String, Object> userFindItemByName(
            @RequestParam int sellerID,
            @RequestParam String name,
            @RequestParam int page,
            @RequestParam int size
    ){
        return itemService.userFindItemByName(sellerID, name, page, size);
    }

    @PutMapping("/userUpdateItem")
    public String updateItemByItemID(@RequestBody Item item){

        int res = itemService.updateItemByItemID(item);
        if (res > 0){
            return "success";
        }else{
            return "error";
        }
    }

    @PutMapping("/adminUpdateStatus")
    public String updateItemStatus(@RequestBody Item item){
//        System.out.println(item);
        int res = itemService.updateItemStatus(item);
        if (res > 0){
            return "status updated";
        }else {
            return "status update error";
        }
    }

    @GetMapping("/userFindItemsByStatus")
    public Map<String, Object> findItemsByStatus(
            @RequestParam("status") String status, 
            @RequestParam("name") String name, 
            @RequestParam("category") String category, 
            @RequestParam("condition") String condition, 
            @RequestParam("page") int page, 
            @RequestParam("size") int size,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "sortType", required = false) String sortType
    ){
        // 检查是否有高级筛选参数
        if (minPrice != null || maxPrice != null || 
            (sortType != null && !sortType.equals("default"))) {
            
            return itemService.findItemsByStatus(
                status, name, category, condition, 
                minPrice, maxPrice, sortType, 
                page, size
            );
        } else {
            // 如果没有高级筛选参数，使用原来的方法
            return itemService.findItemsByStatus(status, name, category, condition, page, size);
        }
    }

    @GetMapping("/getItemById")
    public ItemWithImages getItemById(@RequestParam("id") int itemId){
        return itemService.getItemById(itemId);
    }

    @PutMapping("/userChangeStatus")
    public String changeStatus(@RequestBody Item item){
        return itemService.changeStatus(item.getItemID(), item.getStatus());
    }

    @PutMapping("/updateQuantityAndStatus")
    public String updateQuantityAndStatus(@RequestBody Item item){
        return itemService.updateQuantityAndStatus(item.getItemID(), item.getQuantity(), item.getStatus());
    }

    @PutMapping("/batchUpdateStatus")
    public ResponseEntity<?> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> itemIds = (List<Integer>) request.get("itemIds");
            String status = (String) request.get("status");
            
            if (itemIds == null || status == null) {
                return ResponseEntity.badRequest().body("商品ID列表和状态都不能为空");
            }
            
            Map<String, Object> result = itemService.batchUpdateStatus(itemIds, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("处理请求时发生错误: " + e.getMessage());
        }
    }
}
