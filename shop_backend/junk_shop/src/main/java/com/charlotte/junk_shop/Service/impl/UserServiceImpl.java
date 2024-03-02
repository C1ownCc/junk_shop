package com.charlotte.junk_shop.Service.impl;

import com.charlotte.junk_shop.Dao.UserMapper;
import com.charlotte.junk_shop.Pojo.User;
import com.charlotte.junk_shop.Service.UserService;
import com.charlotte.junk_shop.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Override
    public User UserLogin(String username, String password) {
        // 加密密码并在数据库中检查用户
        String encryptedPassword = MD5Util.getMD5(password);
        return userMapper.UserLogin(username, encryptedPassword);
    }

    @Override
    public int UserRegister(User user) {
        if (isUserUnique(user)) {
            String encryptedPassword = MD5Util.getMD5(user.getPasswordHash());
            user.setPasswordHash(encryptedPassword);
            return userMapper.UserRegister(user);
        } else {
            return -1; // 返回 -1 表示用户不唯一，注册失败
        }
    }

    @Override
    public boolean isUserUnique(User user) {
        User existingUser = userMapper.findByUsernameOrPhoneNumberOrEmail(user.getUsername(), user.getPhoneNumber(), user.getEmail());
//        System.out.println("Existing user: " + existingUser); // 打印查询到的现有用户信息
        return existingUser == null;
    }

    @Override
    public List<User> findAllUsers() {
        return userMapper.findAllUsers();
    }

    @Override
    public Map<String, Object> findAllUsersWithPagination(int page, int size) {
        int offset = page * size;
        List<User> users = userMapper.findAllUsersWithPagination(offset, size);
        int total = userMapper.countUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("total", total);
        return response;
    }



    @Override
    public int deleteUserById(int id) {
        return userMapper.deleteUserById(id);
    }

    @Override
    public int adminUpdateUserById(User user) {
        // 从数据库获取当前用户的信息
        User existingUser = userMapper.findUserById(user.getUserID());
        // 如果前端传来的密码哈希与数据库中的不同，则用户修改了密码
        if (!user.getPasswordHash().equals(existingUser.getPasswordHash())) {
            // 对新密码进行加密
            String encryptedPassword = MD5Util.getMD5(user.getPasswordHash());
            user.setPasswordHash(encryptedPassword);
        }
        return userMapper.adminUpdateUserById(user);
    }

    @Override
    public int adminInsertUser(User user) {
        try{
            // 日志输出
//            System.out.println("Attempting to insert user: " + user);
            if (isUserUnique(user)) {
                String encryptedPassword = MD5Util.getMD5(user.getPasswordHash());
                user.setPasswordHash(encryptedPassword);
                return userMapper.adminInsertUser(user);

            } else {
                return -1; // 返回 -1 表示用户不唯一，注册失败
            }
        }catch (Exception e){
//            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return 0; // 出现异常时返回0
        }

    }

    @Override
    public User findUserByUsername(String username) {

        return userMapper.findUserByUsername(username);
    }

    @Override
    public int updateAvatar(long id, String avatar) {
        return userMapper.uploadAvatar(id, avatar);
    }

    @Override
    public Map<String, Object> findUserByUsernameWithPagination(String username, int page, int size) {
        int offset = page * size;
        List<User> users = userMapper.findUserByUsernameWithPagination(username, size, offset);
        Integer total = userMapper.countUsersByUsernameWithPagination(username, size, offset);
        if (total == null){
            total = 0;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("total", total);
        return response;
    }

    @Override
    public int userUpdateUserInfo(User user) {
        // 从数据库获取当前用户的信息
        User existingUser = userMapper.findUserById(user.getUserID());

        // 如果前端传来的密码哈希与数据库中的不同，则用户修改了密码
        if (!user.getPasswordHash().equals(existingUser.getPasswordHash())) {
            // 对新密码进行加密
            String encryptedPassword = MD5Util.getMD5(user.getPasswordHash());
            user.setPasswordHash(encryptedPassword);
        }
        return userMapper.userUpdateUserInfo(user);
    }

    @Override
    public String changeUserWalletBalance(int userID, double walletBalance) {
        int res = userMapper.changeUserWalletBalance(userID, walletBalance);
        if (res > 0){
            return "walletBalance changed";
        }else{
            return "walletBalance change error";

        }
    }

    @Override
    public List<User> getMessageUser(List<Integer> userIDs) {
        List<User> users = new ArrayList<>();
        for (int userID : userIDs){
            User user = userMapper.getMessageUser(userID);
            users.add(user);
        }
        return users;
    }

    @Override
    public User getMessageUser(int userID) {
        return userMapper.getMessageUser(userID);
    }


}
