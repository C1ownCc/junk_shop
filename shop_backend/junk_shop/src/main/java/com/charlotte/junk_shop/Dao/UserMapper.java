package com.charlotte.junk_shop.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charlotte.junk_shop.Pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE Username = #{username} AND PasswordHash = #{password}")
    User UserLogin(@Param("username") String username,@Param("password") String password);

    @Select("SELECT * FROM users WHERE UserID = #{id}")
    User findUserById(long id);

    @Insert("INSERT INTO users (username, nickName, email, passwordHash, phoneNumber, recipientName, address, createdAt, updatedAt, role, avatar) " +
            "VALUES (#{username}, #{nickName}, #{email}, #{passwordHash}, #{phoneNumber}, #{recipientName}, #{address}, #{createdAt}, #{updatedAt}, #{role}, #{avatar})")
    int UserRegister(User user);

    @Select("SELECT * FROM users WHERE username = #{username} OR phoneNumber = #{phoneNumber} OR email = #{email}")
    User findByUsernameOrPhoneNumberOrEmail(@Param("username") String username, @Param("phoneNumber") String phoneNumber, @Param("email") String email);

    @Select("SELECT * FROM users")
    List<User> findAllUsers();

    @Select("SELECT * FROM users LIMIT #{limit} OFFSET #{offset}")
    List<User> findAllUsersWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM users")
    int countUsers();

    @Delete("DELETE  FROM users WHERE UserID = #{id}")
    int deleteUserById(@Param("id") long id);

    @Update("UPDATE users SET username=#{username}, nickName= #{nickName},  email=#{email}, passwordHash=#{passwordHash}, phoneNumber=#{phoneNumber}, recipientName=#{recipientName}, address=#{address}, updatedAt=#{updatedAt}, role=#{role}, avatar=#{avatar}, walletBalance=#{walletBalance} " +
                        "WHERE UserID = #{userID}")
    int adminUpdateUserById(User user);

    @Insert("INSERT INTO users (username, nickName, passwordHash, email, phoneNumber, recipientName, address, createdAt, updatedAt, role, avatar)" +
            "VALUES (#{username}, #{nickName}, #{passwordHash}, #{email}, #{phoneNumber}, #{recipientName}, #{address}, #{createdAt}, #{updatedAt}, #{role}, #{avatar})")
    int adminInsertUser(User user);

    @Select("SELECT * FROM users WHERE Username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Update("UPDATE users SET avatar=#{avatar} WHERE UserID = #{userID}")
    int uploadAvatar(@Param("userID") long id,@Param("avatar") String avatar);

    @Select("SELECT * FROM users WHERE Username LIKE CONCAT('%', #{username}, '%') LIMIT #{limit} OFFSET #{offset}")
    List<User> findUserByUsernameWithPagination(@Param("username") String username,@Param("limit") int limit,@Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM users WHERE Username LIKE CONCAT('%', #{username}, '%') LIMIT #{limit} OFFSET #{offset}")
    Integer countUsersByUsernameWithPagination(@Param("username") String username,@Param("limit") int limit,@Param("offset") int offset);

    @Update("UPDATE users SET nickName=#{nickName}, passwordHash=#{passwordHash}, email=#{email}, phoneNumber=#{phoneNumber}, recipientName=#{recipientName}, address=#{address}, updatedAt= #{updatedAt} WHERE UserID = #{userID}")
    int userUpdateUserInfo(User user);

    @Update("UPDATE users SET walletBalance=#{walletBalance} WHERE UserID = #{userID}")
    int changeUserWalletBalance(@Param("userID") int userID,@Param("walletBalance") double walletBalance);

    @Update("UPDATE users SET walletBalance= walletBalance + #{price} WHERE UserID = 1")
    void adminGetFee(@Param("price") double price);

    @Update("UPDATE users SET walletBalance= walletBalance - #{price} WHERE UserID = 1")
    void adminGiveFee(@Param("price") double price);

    @Update("UPDATE users SET walletBalance= walletBalance + #{walletBalance} WHERE UserID = #{userID}")
    void sellerGetFee(@Param("userID") int userID,@Param("walletBalance") double walletBalance );

    @Update("UPDATE users SET walletBalance= walletBalance - #{walletBalance} WHERE UserID = #{userID}")
    void sellerGiveFee(@Param("userID") int userID,@Param("walletBalance") double walletBalance );

    @Select("SELECT * FROM users WHERE UserID = #{userID}")
    User getMessageUser(int userID);
}
