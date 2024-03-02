package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.Item;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemService {
    //添加商品
    void insertItem(Item item);
    //管理员分页查询商品
    Map<String, Object> findAllItemsWithPagination(int page, int size);
    //搜索
    Map<String, Object> findItemsWithSearch(String name,String status, int page, int size);
    //删除
    int deleteItemById(int item_id);
    //用户查询所有商品
    Map<String, Object> findAllSellerItems(int page, int size, int sellerID);
    //用户搜索
    Map<String, Object> userFindItemByName(int sellerID, String name, int page, int size);
    //用户修改商品
    int updateItemByItemID(Item item);
    //管理员审核商品
    int updateItemStatus(Item item);
    //首页查询商品
    Map<String, Object> findItemsByStatus(String status, String name, String category, String condition, int limit, int offset);
    //变更商品状态
    String changeStatus(int item_id, String status);
}
