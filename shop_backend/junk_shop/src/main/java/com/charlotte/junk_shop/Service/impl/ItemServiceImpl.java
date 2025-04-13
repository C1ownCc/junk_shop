package com.charlotte.junk_shop.Service.impl;
import com.charlotte.junk_shop.Dao.ItemMapper;
import com.charlotte.junk_shop.Pojo.Item;
import com.charlotte.junk_shop.Pojo.ItemWithImages;
import com.charlotte.junk_shop.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void insertItem(Item item) {
        itemMapper.insertItem(item);
    }

    @Override
    public Map<String, Object> findAllItemsWithPagination(int page, int size) {
        int offset = page * size;
        List<Item> items = itemMapper.findAllItemsWithPagination(offset, size);
        int total = itemMapper.countItems();

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        return response;
    }

    @Override
    public Map<String, Object> findItemsWithSearch(String name,String status, int page, int size) {
        int offset = page * size;
        if (status.equals("全部")){
            status = null;
        }
        List<Item> items = itemMapper.findItemByName(name,status, size, offset);
        Integer total = itemMapper.countFindItemByName(name, status);
        if (total == null){
            total = 0;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        return response;
    }

    @Override
    public int deleteItemById(int id) {
        return itemMapper.deleteItemById(id);
    }

    @Override
    public Map<String, Object> findAllSellerItems(int page, int size, int sellerID) {
        int offset = page * size;
        List<ItemWithImages> items = itemMapper.findAllSellerItems(offset, size, sellerID);
        int total = itemMapper.findSellerAllCount(sellerID);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        return response;
    }

    @Override
    public Map<String, Object> userFindItemByName(int sellerID, String name, int page, int size) {
        int offset = page * size;
        List<ItemWithImages> items = itemMapper.userFindItemByName(sellerID, name, size, offset);
        Integer total = itemMapper.findSellerAllCountByName(sellerID, name);
        if (total == null){
            total = 0;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        return response;
    }

    @Override
    public int updateItemByItemID(Item item) {
        item.setUpdatedAt(new Date());
        return itemMapper.updateItemByItemID(item);
    }

    @Override
    public int updateItemStatus(Item item) {
        String status = item.getStatus();
        int itemID = item.getItemID();

        int result = 0; // 初始化一个结果变量

        switch (status) {
            case "审核通过":
                item.setUpdatedAt(new Date());
                // 获取当前商品库存数量
                int quantity = itemMapper.getItemQuantity(itemID);
                // 根据库存量判断状态
                if (quantity <= 0) {
                    item.setStatus("已售出");
                } else {
                    item.setStatus("已上架");
                }
                result = itemMapper.updateItemStatus(item);
                break;
                
            case "已上架":
                item.setUpdatedAt(new Date());
                // 获取当前商品库存数量
                int quantityForListed = itemMapper.getItemQuantity(itemID);
                // 根据库存量判断状态
                if (quantityForListed <= 0) {
                    item.setStatus("已售出");
                } else {
                    item.setStatus("已上架");
                }
                result = itemMapper.updateItemStatus(item);
                break;

            case "待审核":
                item.setUpdatedAt(new Date());
                item.setStatus("待审核");
                result = itemMapper.updateItemStatus(item);
                break;

            case "审核不通过":
                item.setUpdatedAt(new Date());
                item.setStatus("审核不通过");
                result = itemMapper.updateItemStatus(item);
                break;

            case "已售出":
                item.setUpdatedAt(new Date());
                item.setStatus("已售出");
                result = itemMapper.updateItemStatus(item);
                break;

            case "下架":
                item.setUpdatedAt(new Date());
                item.setStatus("已下架");
                result = itemMapper.updateItemStatus(item);
                break;
            default:
                // 如果status不匹配任何情况，result保持为0
        }

        return result; // 返回结果
    }

    @Override
    public Map<String, Object> findItemsByStatus(String status, String name, String category, String condition, int page, int size) {
        if (category.equals("全部")){
            category = null;
        }
        if (condition.equals("全部")){
            condition = null;
        }
        int offset = page * size;
        // 获取商品 ID 列表
        List<Integer> itemIDs = itemMapper.findDistinctItemIDsByStatus(status, name, category, condition, size, offset);

        // 如果没有商品 ID，则直接返回空列表
        if (itemIDs.isEmpty()) {
            return null;
        }
        List<ItemWithImages> items = itemMapper.findItemsByStatus(itemIDs);
        int total = itemMapper.findAllItemsCountByStatus(name,category, condition);
        Map<String, Object> res = new HashMap<>();
        res.put("items", items);
        res.put("total", total);

        return res;
    }

    @Override
    public String changeStatus(int item_id, String status) {
        int res = itemMapper.changeStatus(item_id, status);
        if (res > 0){
            return "status changed";
        }
        return "status change error";
    }

    @Override
    public String updateQuantityAndStatus(int item_id, int quantity, String status) {
        // 根据库存量自动判断状态
        if (quantity <= 0) {
            // 如果库存为0，自动设置为"已售出"
            status = "已售出";
        } else if ("已售出".equals(status) && quantity > 0) {
            // 如果状态为已售出但库存大于0，则自动更正为"已上架"
            status = "已上架";
        }
        
        int res = itemMapper.updateQuantityAndStatus(item_id, quantity, status);
        if (res > 0){
            return "status changed";
        }
        return "status change error";
    }

    @Override
    public ItemWithImages getItemById(int itemId) {
        // 调用Mapper方法获取包含图片信息的商品详情
        return itemMapper.findItemById(itemId);
    }

}
