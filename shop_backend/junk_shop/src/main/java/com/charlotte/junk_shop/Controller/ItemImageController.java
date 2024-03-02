package com.charlotte.junk_shop.Controller;

import com.charlotte.junk_shop.Pojo.Item_image;
import com.charlotte.junk_shop.Service.ItemImageService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/")
public class ItemImageController {

    @Autowired
    private ItemImageService itemImageService;

    @PostMapping("/userUploadImages")
    public String uploadImages(@RequestParam("file") MultipartFile file, @RequestParam("itemID") int itemID) {
        return itemImageService.insertImage(file, itemID);
    }

    @DeleteMapping("/deleteImageById")
    public String deleteImageById(@Param("imageID") int imageID){
        int res1 = itemImageService.deleteImageById(imageID);

        if (res1 > 0){
            return "delete success";
        }else {
            return "delete error";
        }
    }

    @GetMapping("/getItemImagesByItemID")
    public List<Item_image> findImagesByItemID(@Param("itemID") int itemID){
        return itemImageService.findImagesByItemID(itemID);
    }
}
