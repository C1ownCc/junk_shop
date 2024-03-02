package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.AnnouncementMapper;
import com.charlotte.junk_shop.Service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public String getContent() {
        return announcementMapper.getContent();
    }

    @Override
    public int changeContent(String content) {
        return announcementMapper.changeContent(content);
    }


}
