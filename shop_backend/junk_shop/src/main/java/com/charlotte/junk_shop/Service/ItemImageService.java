package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.Item_image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemImageService {
    String insertImage(MultipartFile file, int itemID);

    int deleteImageById(int id);

    int deleteImageByItemID(int id);

    List<Item_image> findImagesByItemID(int id);
}
