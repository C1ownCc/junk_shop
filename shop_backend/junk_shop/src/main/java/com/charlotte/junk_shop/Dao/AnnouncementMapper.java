package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT Content FROM announcements")
    String getContent();

    @Update("UPDATE announcements SET Content = #{content}")
    int changeContent(String content);
}
