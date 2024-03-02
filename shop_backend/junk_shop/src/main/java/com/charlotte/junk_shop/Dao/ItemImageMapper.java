package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.Item_image;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ItemImageMapper extends BaseMapper<Item_image> {

    @Insert("INSERT INTO item_images(itemID,imageUrl) VALUES(#{itemID},#{imageURL})")
    int insertImage(Item_image item_image);

    @Delete("DELETE FROM item_images WHERE imageID=#{imageID}")
    int deleteImageById(@Param("imageID") int imageID);

    @Delete("DELETE FROM item_images WHERE itemID=#{itemID}")
    int deleteImageByItemID(@Param("itemID") int itemID);

    @Select("SELECT * FROM item_images WHERE ItemID=#{itemID}")
    List<Item_image> findImagesByItemID(@Param("itemID") int itemID);
}
