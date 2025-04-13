package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.Item;
import com.charlotte.junk_shop.Pojo.ItemWithImages;
import com.charlotte.junk_shop.Pojo.Item_image;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {

    @Insert("insert INTO items" +
            "(sellerID, name, description, price, category, `condition`, quantity, status, createdAt, updatedAt) " +
            "values" +
            "(#{sellerID}, #{name}, #{description}, #{price}, #{category}, #{condition}, #{quantity}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "itemID")
    void insertItem(Item item);

    @Select("SELECT * FROM items ORDER BY createdAt DESC LIMIT #{limit} OFFSET #{offset}")
    List<Item> findAllItemsWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) from items")
    int countItems();


    List<Item> findItemByName(@Param("name") String name,@Param("status") String status, @Param("limit") int limit,@Param("offset") int offset);

    Integer countFindItemByName(@Param("name") String name,@Param("status") String status);

    @Delete("DELETE FROM items WHERE itemID = #{itemID}")
    int deleteItemById(@Param("itemID") int id);

    @Select("SELECT * FROM items WHERE sellerID = #{sellerID} ORDER BY createdAt DESC LIMIT #{limit} OFFSET #{offset}")
    @Results(
            {
                    @Result(property = "itemID", column = "itemID"),
                    @Result(property = "images", column = "itemID", many = @Many(select = "findImages")),
                    @Result(property = "sellerID", column = "sellerID"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "description", column = "description"),
                    @Result(property = "price", column = "price"),
                    @Result(property = "quantity", column = "quantity"),
                    @Result(property = "category", column = "category"),
                    @Result(property = "condition", column = "condition"),
                    @Result(property = "status", column = "status")
            }
    )
    List<ItemWithImages> findAllSellerItems(@Param("offset") int offset, @Param("limit") int limit, @Param("sellerID") int sellerID);

    @Select("SELECT * FROM items WHERE SellerID = #{sellerID} and Name LIKE CONCAT('%', #{name}, '%') ORDER BY createdAt DESC LIMIT #{limit} OFFSET #{offset}")
    @Results(
            {
                    @Result(property = "itemID", column = "itemID"),
                    @Result(property = "images", column = "itemID", many = @Many(select = "findImages")),
                    @Result(property = "sellerID", column = "sellerID"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "description", column = "description"),
                    @Result(property = "price", column = "price"),
                    @Result(property = "quantity", column = "quantity"),
                    @Result(property = "category", column = "category"),
                    @Result(property = "condition", column = "condition"),
                    @Result(property = "status", column = "status")
            }
    )
    List<ItemWithImages> userFindItemByName(@Param("sellerID") int sellerID, @Param("name") String name,@Param("limit") int limit,@Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM items WHERE SellerID = #{sellerID}")
    int findSellerAllCount(@Param("sellerID") int sellerID);

    @Select("SELECT COUNT(*) FROM items WHERE SellerID = #{sellerID} and Name LIKE CONCAT('%', #{name}, '%')")
    Integer findSellerAllCountByName(@Param("sellerID") int sellerID, @Param("name") String name);

    @Select("SELECT * FROM item_images WHERE itemID = #{itemID}")
    List<Item_image> findImages(@Param("itemID") int itemID);

    @Update("UPDATE items SET name = #{name},price = #{price},category = #{category},description = #{description},`condition` = #{condition},quantity = #{quantity},UpdatedAt = #{updatedAt} , Status='待审核' WHERE itemID = #{itemID}")
    int updateItemByItemID(Item item);

    @Update("UPDATE items SET Status = #{status}, UpdatedAt = #{updatedAt} WHERE itemID = #{itemID}")
    int updateItemStatus(Item item);

//    List<ItemWithImages> findItemsByStatus(
//            @Param("status") String status,
//            @Param("name") String name,
//            @Param("category") String category,
//            @Param("condition") String condition,
//            @Param("size") int size,
//            @Param("offset") int offset);

    List<Integer> findDistinctItemIDsByStatus(String status, String name, String category, String condition, int size, int offset);
    List<ItemWithImages> findItemsByStatus(List<Integer> itemIDs);

    int findAllItemsCountByStatus(String name,String category,String condition);

    @Update("UPDATE items SET Status = #{status}, UpdatedAt = now() WHERE itemID = #{itemID}")
    int changeStatus(@Param("itemID") int itemID,@Param("status") String status);

    @Update("UPDATE items SET Quantity = #{quantity}, Status = #{status}, UpdatedAt = now() WHERE itemID = #{itemID}")
    int updateQuantityAndStatus(@Param("itemID") int itemID, @Param("quantity") int quantity, @Param("status") String status);

    @Select("SELECT Quantity FROM items WHERE itemID = #{itemID}")
    int getItemQuantity(@Param("itemID") int itemID);

    @Select("SELECT * FROM items WHERE itemID = #{itemID}")
    @Results(
            {
                    @Result(property = "itemID", column = "itemID"),
                    @Result(property = "images", column = "itemID", many = @Many(select = "findImages")),
                    @Result(property = "sellerID", column = "sellerID"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "description", column = "description"),
                    @Result(property = "price", column = "price"),
                    @Result(property = "quantity", column = "quantity"),
                    @Result(property = "category", column = "category"),
                    @Result(property = "condition", column = "condition"),
                    @Result(property = "status", column = "status")
            }
    )
    ItemWithImages findItemById(@Param("itemID") int itemID);

    // 高级筛选方法
    List<Integer> findItemsByAdvancedFilter(
        @Param("status") String status, 
        @Param("name") String name, 
        @Param("category") String category, 
        @Param("condition") String condition,
        @Param("minPrice") Double minPrice, 
        @Param("maxPrice") Double maxPrice, 
        @Param("sortType") String sortType,
        @Param("size") int size, 
        @Param("offset") int offset);
    
    // 计算满足高级筛选条件的商品总数
    int countItemsByAdvancedFilter(
        @Param("status") String status, 
        @Param("name") String name, 
        @Param("category") String category, 
        @Param("condition") String condition,
        @Param("minPrice") Double minPrice, 
        @Param("maxPrice") Double maxPrice);

}
