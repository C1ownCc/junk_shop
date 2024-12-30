package com.charlotte.junk_shop.Controller;
import com.charlotte.junk_shop.Pojo.User;
import com.charlotte.junk_shop.Service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    String fileAccessUrl = "";

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User loggedInUser = userService.UserLogin(user.getUsername(), user.getPasswordHash());
        if (loggedInUser != null) {
            return loggedInUser.getRole() == 0 ? "Admin Logged In" : "User Logged In";
        } else {
            return "Login Failed";
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setRole(1);
        user.setAvatar("https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100");
        if (userService.isUserUnique(user)) {
            int result = userService.UserRegister(user);
            return result > 0 ? "Registration Successful" : "Registration Failed";
        } else {
            return "Username, Phone Number, or Email already exists";
        }
    }

    @GetMapping("/getAllUsers")
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/getAllUsersPaginated")
    public Map<String, Object> getAllUsersPaginated(@RequestParam("page") int page, @RequestParam("size") int size) {
        return userService.findAllUsersWithPagination(page, size);
    }


    @DeleteMapping("/deleteUserById")
    public String deleteUserById(@RequestParam int id) {
        int result = userService.deleteUserById(id);
        return result > 0 ? "User Deleted" : "User Not Found";
    }

    @PutMapping("/adminUpdateUserById")
    public String adminUpdateUserById(@RequestBody User user){
        user.setUpdatedAt(new Date());
//        System.out.println(new Date());
        int result = userService.adminUpdateUserById(user);
        return result > 0 ? "User Updated" : "User Update error";
    }

//    @PostMapping("/adminInsertUser")
//    public String adminInsertUser(@RequestBody User user){
//        user.setWalletBalance(0.0);
//        user.setCreatedAt(new Date());
//        user.setUpdatedAt(new Date());
//        if(user.getAvatar().equals("")){
//            user.setAvatar("https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100");
//        }
//        int result = userService.adminInsertUser(user);
//        return result > 0 ? "User Inserted" : "User Insert error";
//    }

    @PostMapping("/adminInsertUser")
    public String adminInsertUser(
            @RequestParam("username") String username,
            @RequestParam("passwordHash") String passwordHash,
            @RequestParam("nickName") String nickName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("walletBalance") double walletBalance,
            @RequestParam("recipientName") String recipientName,
            @RequestParam("address") String address,
            @RequestParam("role") int role,
            @RequestParam("avatar") String avatar,
            @RequestParam("avatarFile") MultipartFile avatarFile) {

        // 创建 User 对象
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setNickName(nickName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
//        user.setWalletBalance(walletBalance);
        user.setRecipientName(recipientName);
        user.setAddress(address);
        user.setRole(role);
        String avatarUrl = "";
        // 处理头像文件
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 保存文件到服务器，并获取文件URL
            avatarUrl = saveFile(avatarFile);
            user.setAvatar(avatarUrl);
        } else {
            user.setAvatar("https://thirdqq.qlogo.cn/g?b=qq&nk=1678147275&s=100");
        }

        user.setWalletBalance(0.0);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        int result = userService.adminInsertUser(user);

        if (result > 0){
            return "User Inserted";
        }else{
            deleteFile(fileAccessUrl);
            return "User Insert error";
        }
    }

    // 方法来保存文件并返回文件的URL
    private String saveFile(MultipartFile file) {
        try {
            // 本地存储路径
            String directoryPath = "/Users/chenjm/Desktop/coder/project/junk_shop/shop_front/junkshop/public/uploads";
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;

            // 保存文件到本地
            Path path = Paths.get(directoryPath, newFilename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件的访问URL，根据您的服务配置可能需要调整
            fileAccessUrl = "/Users/chenjm/Desktop/coder/project/junk_shop/shop_front/junkshop/public/uploads" + newFilename;
            String avatarUrl = "http://localhost:5173/uploads/" + newFilename;
            return avatarUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteFile(String avatarUrl) {
        try {
            // 构造文件路径
            Path file = Paths.get(avatarUrl);
//            System.out.println(file);
            Files.deleteIfExists(file);
        } catch (IOException e) {
//            System.out.println("----------------------------");
            e.printStackTrace();
            // 处理文件删除失败的情况
        }
    }

    @GetMapping("/getUserByUsername")
    public User getUserByUsername(@RequestParam String username) {
        return userService.findUserByUsername(username);
    }

    @PostMapping("/updateAvatar")
    public String updateAvatar(@Param("userID") int id,@Param("avatarFile") MultipartFile avatarFile){
        int result;
        String avatar;
        // 处理头像文件
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 保存文件到服务器，并获取文件URL
            avatar = saveFile(avatarFile);
            result = userService.updateAvatar(id, avatar);
        }else {
            return "avatarFile or id not found";
        }

        if (result > 0){
            return "avatar updated";
        }else{
            deleteFile(fileAccessUrl);
            return "avatar update error";
        }
    }

    @GetMapping("/findUserByUsername")
    public Map<String, Object> findUserByUsername(
            @RequestParam String username,
            @RequestParam int page,
            @RequestParam int size
    ){
        return userService.findUserByUsernameWithPagination(username, page, size);
    }

    @PutMapping("/userUpdateUserInfo")
    public String userUpdateUserInfo(@RequestBody User user){
        user.setUpdatedAt(new Date());
//        System.out.println(new Date());
        int result = userService.userUpdateUserInfo(user);
        return result > 0 ? "User Updated" : "User Update error";
    }

    @PutMapping("/changeUserWalletBalance")
    public String changeUserWalletBalance(@RequestBody User user){
        return userService.changeUserWalletBalance(user.getUserID(),user.getWalletBalance());
    }

    @GetMapping("/getMessageUsers")
    public List<User> getMessageUsers(@RequestParam List<Integer> userIDs){
        return userService.getMessageUser(userIDs);
    }

    @GetMapping("/getUserByUserID")
    public User getUserByUserID(@RequestParam int userID){
        return userService.getMessageUser(userID);
    }
}
