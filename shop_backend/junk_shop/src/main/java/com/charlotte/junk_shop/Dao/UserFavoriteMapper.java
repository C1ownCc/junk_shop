package com.charlotte.junk_shop.Dao;

import com.charlotte.junk_shop.Pojo.UserFavorite;
import com.charlotte.junk_shop.Pojo.UserFavoriteWithDetails;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    
    // 添加收藏
    @Insert("INSERT INTO userfavorites (UserID, ItemID) VALUES (#{userID}, #{itemID})")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteID")
    int addFavorite(UserFavorite favorite);
    
    // 取消收藏
    @Delete("DELETE FROM userfavorites WHERE UserID = #{userID} AND ItemID = #{itemID}")
    int removeFavorite(@Param("userID") Integer userID, @Param("itemID") Integer itemID);
    
    // 检查是否已收藏
    @Select("SELECT COUNT(*) FROM userfavorites WHERE UserID = #{userID} AND ItemID = #{itemID}")
    int checkFavorite(@Param("userID") Integer userID, @Param("itemID") Integer itemID);
    
    // 获取用户收藏列表
    @Select("SELECT f.*, i.Name, i.Description, i.Price, i.Category, i.Condition, i.Quantity, i.Status, i.SellerID " +
            "FROM userfavorites f " +
            "JOIN items i ON f.ItemID = i.ItemID " +
            "WHERE f.UserID = #{userID} " +
            "ORDER BY f.CreatedAt DESC")
    @Results({
            @Result(property = "favoriteID", column = "FavoriteID"),
            @Result(property = "userID", column = "UserID"),
            @Result(property = "itemID", column = "ItemID"),
            @Result(property = "createdAt", column = "CreatedAt"),
            @Result(property = "name", column = "Name"),
            @Result(property = "description", column = "Description"),
            @Result(property = "price", column = "Price"),
            @Result(property = "category", column = "Category"),
            @Result(property = "condition", column = "Condition"),
            @Result(property = "quantity", column = "Quantity"),
            @Result(property = "status", column = "Status"),
            @Result(property = "sellerID", column = "SellerID"),
            @Result(property = "images", column = "ItemID", 
                    javaType = List.class, 
                    many = @Many(select = "com.charlotte.junk_shop.Dao.ItemImageMapper.findImagesByItemID"))
    })
    List<UserFavoriteWithDetails> getUserFavorites(@Param("userID") Integer userID);
    
    // 删除收藏
    @Delete("DELETE FROM userfavorites WHERE FavoriteID = #{favoriteID}")
    int deleteFavorite(@Param("favoriteID") Integer favoriteID);
} 