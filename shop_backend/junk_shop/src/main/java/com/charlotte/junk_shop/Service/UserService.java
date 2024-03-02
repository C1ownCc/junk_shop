package com.charlotte.junk_shop.Service;

import com.charlotte.junk_shop.Pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    //用户登录
    User UserLogin(String username, String password);
    //
//    User findUserById(int id);
    //用户注册
    int UserRegister(User user);
    //判断注册时账号是否已存在
    boolean isUserUnique(User user);
    //获取全部用户信息
    List<User> findAllUsers();
    //分页查询全部
    Map<String, Object> findAllUsersWithPagination(int page, int size);
    //根据id删除用户
    int deleteUserById(int id);
    //根据id修改用户
    int adminUpdateUserById(User user);
    //管理员添加用户
    int adminInsertUser(User user);
    //根据username获取用户
    User findUserByUsername(String username);
    //更新头像
    int updateAvatar(long id, String avatar);
    //搜索
    Map<String, Object> findUserByUsernameWithPagination(String username, int page, int size);
    //根据id修改用户
    int userUpdateUserInfo(User user);
    //修改余额
    String changeUserWalletBalance(int userID, double walletBalance);

    List<User> getMessageUser(List<Integer> userIDs);

    User getMessageUser(int userID);
}
