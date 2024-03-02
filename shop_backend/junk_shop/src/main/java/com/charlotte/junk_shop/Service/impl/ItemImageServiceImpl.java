package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.ItemImageMapper;
import com.charlotte.junk_shop.Pojo.Item_image;
import com.charlotte.junk_shop.Service.ItemImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ItemImageServiceImpl implements ItemImageService {

    @Autowired
    private ItemImageMapper itemImageMapper;

    @Override
    public String insertImage(MultipartFile file, int itemID) {
        // 您的图片存储路径
        String directoryPath = "F:\\Project\\junk_shop\\shop_front\\junkshop\\src\\assets\\uploads";

        try {
            if (file != null && !file.isEmpty()) {
                // 使用 UUID 生成唯一文件名
                String originalFilename = file.getOriginalFilename();
                String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;

                // 构造图片保存的完整路径
                Path destination = Paths.get(directoryPath, newFilename);

                // 将图片保存到磁盘
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                // 生成访问 URL
                String imageURL = "http://localhost:8080/uploads/" + newFilename;

                // 保存图片信息到数据库
                Item_image itemImage = new Item_image();
                itemImage.setItemID(itemID);
                itemImage.setImageURL(imageURL);
                int res = itemImageMapper.insertImage(itemImage);
                if (res > 0){
                    return "Upload successful";
                } else {
                    return "Upload failed";
                }
            } else {
                return "No file provided";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Upload failed";
        }
    }

    @Override
    public int deleteImageById(int imageID) {
        return itemImageMapper.deleteImageById(imageID);
    }

    @Override
    public int deleteImageByItemID(int itemID){
        return itemImageMapper.deleteImageByItemID(itemID);
    }

    @Override
    public List<Item_image> findImagesByItemID(int id) {

        return itemImageMapper.findImagesByItemID(id);
    }
}
