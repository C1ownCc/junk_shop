<template>
  <div class="personal-info-card">
    <el-form :model="user" label-width="100px" v-if="true">
      <el-form-item label="头像">
        <el-avatar
          :src="user.avatar"
          size="large"
          @click="showEditAvatarDialog"
        ></el-avatar>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="user.nickname"></el-input>
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="user.email">123</el-input>
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="user.phone"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="user.password" type="password"></el-input>
      </el-form-item>
      <el-form-item label="收件人姓名">
        <el-input v-model="user.recipientName"></el-input>
      </el-form-item>
      <el-form-item label="收件地址">
        <el-input v-model="user.address"></el-input>
      </el-form-item>
      <el-form-item label="钱包余额">
        <el-input v-model="user.walletBalance" disabled></el-input>
      </el-form-item>
      <!-- 可以继续添加其他个人信息字段 -->
      <el-form-item>
        <el-button type="primary" @click="onSubmit">更新信息</el-button>
        <el-button type="primary" @click="setMoney">充值</el-button>
        <el-radio-group v-model="money" class="selectMoney">
          <el-radio-button :label="20.0" />
          <el-radio-button :label="50.0" />
          <el-radio-button :label="100.0" />
        </el-radio-group>
      </el-form-item>
    </el-form>

    <el-dialog
      v-model="isAvatarDialogVisible"
      title="编辑头像"
      class="avatarDialog"
    >
      <div class="avatar-container">
        <el-upload
          class="avatar-upload"
          :http-request="handleAvatarUpload"
          :show-file-list="false"
          :on-change="handleAvatarChange1"
          :before-upload="beforeAvatarUpload"
        >
          <el-avatar
            v-if="uploadAvatarPreview"
            :src="uploadAvatarPreview"
            class="avatar"
          />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <span slot="footer" class="dialog-footer">
          <p>点击上方图片选择上传头像</p>
          <el-button @click="isAvatarDialogVisible = false">取消</el-button>
        </span>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";

const user = reactive({
  userID: "",
  avatar: "",
  nickname: "",
  email: "",
  phone: "",
  password: "",
  recipientName: "",
  address: "",
  walletBalance: "",
  avatarFile: null,
});

const editUserInfo = reactive({
  userID: "",
  nickName: "",
  email: "",
  phoneNumber: "",
  passwordHash: "",
  recipientName: "",
  address: "",
  updatedAt: "",
});

const isAvatarDialogVisible = ref(false); // 编辑头像弹窗可见性
const uploadAvatarPreview = ref(""); //头像预览

const getUserInfo = async () => {
  try {
    const userInStorage = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInStorage || !userInStorage.username) {
      console.error(
        "无法获取用户信息，用户未登录或 localStorage 中无用户数据。"
      );
      return;
    }
    const res = await axios.get("http://192.168.1.112:8080/getUserByUsername", {
      params: {
        username: userInStorage.username,
      },
    });
    user.userID = res.data.userID;
    user.avatar = res.data.avatar;
    user.nickname = res.data.nickName;
    user.email = res.data.email;
    user.phone = res.data.phoneNumber;
    user.recipientName = res.data.recipientName;
    user.address = res.data.address;
    user.walletBalance = res.data.walletBalance;
    user.password = res.data.passwordHash;
    editUserInfo.updatedAt = res.data.updatedAt;
    // console.log(user);
  } catch (err) {
    console.log("获取用户信息失败！", err);
  }
};

const onSubmit = async () => {
  editUserInfo.userID = user.userID;
  editUserInfo.nickName = user.nickname;
  editUserInfo.email = user.email;
  editUserInfo.phoneNumber = user.phone;
  editUserInfo.passwordHash = user.password;
  editUserInfo.recipientName = user.recipientName;
  editUserInfo.address = user.address;

  console.log("提交的表单信息：", editUserInfo);
  try {
    const res = await axios.put(
      "http://localhost:8080/userUpdateUserInfo",
      editUserInfo
    );
    if (res.data === "User Updated") {
      getUserInfo();
      ElMessage.success("信息更新成功！");

      showEditAvatarDialog.value = false;
      editUserInfo.userID = "";
      editUserInfo.nickName = "";
      editUserInfo.email = "";
      editUserInfo.phoneNumber = "";
      editUserInfo.passwordHash = "";
      editUserInfo.recipientName = "";
      editUserInfo.address = "";
    } else {
      ElMessage.error("信息更新失败！");
    }
  } catch (err) {
    alert("请求错误:", err);
    console.log("更新失败！", err);
  }
};

onMounted(getUserInfo);

// 显示编辑头像的弹窗
const showEditAvatarDialog = () => {
  isAvatarDialogVisible.value = true;
  uploadAvatarPreview.value = user.avatar;
};

// 处理头像上传
const handleAvatarUpload = async () => {
  if (!user.userID || !user.avatarFile) {
    alert("用户ID或头像文件缺失");
    return;
  }

  let formData = new FormData();
  formData.append("id", user.userID);
  formData.append("avatarFile", user.avatarFile);

  try {
    const response = await axios.post(
      "http://localhost:8080/updateAvatar",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );

    if (response.data === "avatar updated") {
      // 假设后端响应包含 success 字段表示上传成功
      ElMessage.success("头像更新成功！");

      isAvatarDialogVisible.value = false;
      getUserInfo(); // 如果需要，重新获取用户列表以更新UI
    } else if (response.data === "avatarFile or id not found") {
      // 处理后端返回的失败情况
      ElMessage.success("头像或id未获取！");
    } else if (response.data === "avatar update error") {
      // 处理后端返回的失败情况
      ElMessage.success("头像更新失败！error1", response.data);
    } else {
      // 处理后端返回的失败情况
      alert("头像更新失败！error2");
    }
  } catch (err) {
    console.error("头像更新失败！", err);
    alert("头像更新失败！");
  }
};

// 处理头像选择
const handleAvatarChange1 = (file, fileList) => {
  if (file && file.raw) {
    // 存储文件对象
    user.avatarFile = file.raw;

    // 创建一个 URL 对象用于预览
    uploadAvatarPreview.value = URL.createObjectURL(file.raw);
  } else {
    // 如果没有文件被选择，清除预览
    uploadAvatarPreview.value = "";
  }
};

const beforeAvatarUpload = (file) => {
  const isJPGorPNG = file.type === "image/jpeg" || file.type === "image/png";
  if (!isJPGorPNG) {
    ElMessage.error("上传头像图片只能是 JPG/PNG 格式!！");

    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
    return false;
  }
  return isJPGorPNG && isLt2M;
};

const money = ref(20);
const changeMoney = ref({});
const setMoney = async () => {
  changeMoney.value.userID = user.userID;
  changeMoney.value.walletBalance = money.value + user.walletBalance;
  ElMessageBox.confirm("确定要充值吗？", "Warning", {
    confirmButtonText: "确认充值",
    cancelButtonText: "取消充值",
    type: "warning",
  }).then(async () => {
    try {
      const res = await axios.put(
        "http://192.168.1.112:8080/changeUserWalletBalance",
        changeMoney.value
      );
      if (res.data === "walletBalance changed") {
        ElMessage.success("充值成功！");
        getUserInfo();
      } else {
        ElMessage.error("充值失败！");
      }
    } catch (err) {
      ElMessage.error("充值请求出错！", err);
    }
  });
};
</script>

<style scoped>
.personal-info-card {
  max-width: 500px;
  margin: auto;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background: #fff;
  min-width: 500px;
  padding-left: 50px;
  padding-right: 100px;
}

.el-form-item {
  margin-bottom: 20px;
}

.selectMoney {
  margin-left: 20px;
}
</style>
