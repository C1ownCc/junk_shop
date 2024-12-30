<template>
  <div class="page-container">
    <div class="personal-info-card">
      <div class="card-header">
        <h2 class="section-title">个人信息</h2>
        <p class="subtitle">管理您的个人资料和账户信息</p>
      </div>

      <el-form :model="user" label-width="120px" class="info-form">
        <div class="avatar-section">
          <el-form-item label="头像">
            <div class="avatar-wrapper">
              <el-avatar
                :src="user.avatar"
                :size="100"
                @click="showEditAvatarDialog"
                class="user-avatar"
              />
              <div class="avatar-edit" @click="showEditAvatarDialog">
                <el-icon><EditPen /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
          </el-form-item>
        </div>

        <div class="form-grid">
          <el-form-item label="昵称">
            <el-input v-model="user.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="user.email" placeholder="请输入邮箱" />
          </el-form-item>

          <el-form-item label="手机号">
            <el-input v-model="user.phone" placeholder="请输入手机号" />
          </el-form-item>

          <el-form-item label="密码">
            <el-input 
              v-model="user.password" 
              type="password" 
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
        </div>

        <div class="divider">收货信息</div>

        <div class="form-grid">
          <el-form-item label="收件人姓名">
            <el-input v-model="user.recipientName" placeholder="请输入收件人姓名" />
          </el-form-item>

          <el-form-item label="收件地址" class="full-width">
            <el-input v-model="user.address" placeholder="请输入详细地址" />
          </el-form-item>
        </div>

        <div class="divider">账户余额</div>

        <div class="wallet-section">
          <el-form-item label="钱包余额">
            <div class="balance-display">
              <span class="currency">¥</span>
              <span class="amount">{{ user.walletBalance }}</span>
            </div>
          </el-form-item>

          <div class="recharge-section">
            <el-radio-group v-model="money" class="amount-selector">
              <el-radio-button :label="20.0">¥20</el-radio-button>
              <el-radio-button :label="50.0">¥50</el-radio-button>
              <el-radio-button :label="100.0">¥100</el-radio-button>
            </el-radio-group>
            <el-button type="primary" @click="setMoney" class="recharge-btn">
              <el-icon><Plus /></el-icon>充值
            </el-button>
          </div>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSubmit" class="submit-btn">
            保存更改
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="isAvatarDialogVisible"
      title="更换头像"
      width="400px"
      class="avatar-dialog"
    >
      <div class="avatar-upload-container">
        <el-upload
          class="avatar-uploader"
          :http-request="handleAvatarUpload"
          :show-file-list="false"
          :on-change="handleAvatarChange1"
          :before-upload="beforeAvatarUpload"
        >
          <div class="upload-area">
            <el-avatar
              v-if="uploadAvatarPreview"
              :src="uploadAvatarPreview"
              :size="120"
              class="preview-avatar"
            />
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span>点击上传</span>
            </div>
          </div>
        </el-upload>
        <p class="upload-tip">支持 JPG、PNG 格式，文件小于 2MB</p>
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
    const res = await axios.get("http://localhost:8080/getUserByUsername", {
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
        "http://localhost:8080/changeUserWalletBalance",
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
.page-container {
  min-height: 100vh;
  padding: 40px;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
}

.personal-info-card {
  width: 800px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.card-header {
  margin-bottom: 40px;
  text-align: center;
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  display: inline-block;
}

.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.avatar-edit {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  background: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 1;
}

.avatar-wrapper:hover .avatar-edit {
  opacity: 1;
  bottom: -5px;
}

.avatar-edit .el-icon {
  font-size: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.full-width {
  grid-column: span 2;
}

.divider {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 32px 0 24px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
}

.wallet-section {
  background: #f8fafc;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
}

.balance-display {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.currency {
  font-size: 16px;
  color: #059669;
}

.amount {
  font-size: 32px;
  font-weight: 600;
  color: #059669;
}

.recharge-section {
  margin-top: 16px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.amount-selector {
  flex-grow: 1;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.submit-btn {
  width: 200px;
  height: 40px;
}

/* 头像上传对话框样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.upload-area {
  width: 200px;
  height: 200px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #60a5fa;
  background: #f0f9ff;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.upload-icon {
  font-size: 24px;
}

.upload-tip {
  margin-top: 16px;
  color: #64748b;
  font-size: 12px;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-input__wrapper) {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

:deep(.el-radio-button__inner) {
  border-radius: 8px;
}

:deep(.el-dialog) {
  border-radius: 16px;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .personal-info-card {
    width: 100%;
    padding: 24px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}

.el-form-item {
  margin-bottom: 24px;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-input__wrapper) {
  background-color: #fff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #94a3b8;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

:deep(.el-input__inner) {
  color: #1e293b;
  font-size: 14px;
  height: 24px;
  line-height: 24px;
}

:deep(.el-input__inner::placeholder) {
  color: #94a3b8;
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
  color: #475569;
  font-weight: 500;
  font-size: 14px;
  padding-right: 24px;
}

/* 单选按钮组样式 */
:deep(.el-radio-button__inner) {
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  padding: 0 20px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #e2e8f0;
  background-color: #fff;
  color: #475569;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #3b82f6;
  border-color: #3b82f6;
  box-shadow: none;
}

/* 提交按钮样式 */
.submit-btn {
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  border: none;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.3);
}

/* 钱包充值按钮 */
.recharge-btn {
  height: 40px;
  padding: 0 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  border: none;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}

.recharge-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.3);
}

/* 调整表单布局 */
.form-grid {
  gap: 32px;
  padding: 0 16px;
}

/* 钱包区域样式优化 */
.wallet-section {
  background: linear-gradient(to right, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  padding: 32px;
}

.balance-display {
  background: white;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 分隔线样式优化 */
.divider {
  font-size: 18px;
  color: #0f172a;
  padding-bottom: 12px;
  margin: 40px 0 32px;
  border-bottom: 2px solid #e2e8f0;
}
</style>
