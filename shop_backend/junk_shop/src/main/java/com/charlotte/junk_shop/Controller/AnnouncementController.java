package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Announcement;
import com.charlotte.junk_shop.Service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/getContent")
    public String getContent(){
        return announcementService.getContent();
    }

    @PutMapping("/changeContent")
    public String changeContent(@RequestBody Announcement announcement){
        int res = announcementService.changeContent(announcement.getContent());
        return res>0? "success":"error";
    }
}
