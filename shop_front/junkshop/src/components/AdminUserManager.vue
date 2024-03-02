<template>
  <div class="user-management">
    <div class="manager_head">
      <el-button type="primary" @click="handleAdd">添加用户</el-button>
      <!-- <el-radio-group v-model="searchRole" class="elRadio">
        <el-radio-button label="全部" />
        <el-radio-button label="管理员" />
        <el-radio-button label="普通用户" />
      </el-radio-group> -->
      <el-input
        v-model="searchQuery"
        placeholder="请输入用户名以搜索用户"
        class="search-input"
        clearable
        @clear="onSearch"
        @keyup.enter="onSearch"
      >
        <template #suffix>
          <el-icon class="search-icon" size="150%" @click="onSearch">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>

    <el-table :data="users" style="width: 150%">
      <el-table-column prop="userID" label="用户ID"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="passwordHash" label="密码(加密)"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phoneNumber" label="电话号码"></el-table-column>
      <el-table-column prop="walletBalance" label="钱包余额"></el-table-column>
      <el-table-column
        prop="recipientName"
        label="收件人姓名"
      ></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间"></el-table-column>
      <el-table-column prop="updatedAt" label="更新时间"></el-table-column>
      <el-table-column prop="role" label="权限"></el-table-column>
      <el-table-column prop="avatar" label="头像">
        <template #default="scope">
          <img
            :src="scope.row.avatar"
            alt="用户头像"
            class="user-avatar"
            @click="showEditAvatarDialog(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-icon class="icon-setting"
            ><Setting @click="handleEdit(scope.row)"
          /></el-icon>
          <el-icon class="icon-delete"
            ><Delete @click="handleDelete(scope.row.userID)"
          /></el-icon>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[6, 12, 20, 40, 60, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalUsers"
      >
      </el-pagination>
    </div>
    <el-dialog
      title="编辑用户"
      v-model="editDialogVisible"
      width="30%"
      :before-close="handleEditDialogClose"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userID" disabled></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.passwordHash"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickName"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="editForm.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="钱包余额">
          <el-input v-model="editForm.walletBalance"></el-input>
        </el-form-item>
        <el-form-item label="收件人姓名">
          <el-input v-model="editForm.recipientName"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editForm.address"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <el-input v-model="editForm.role"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleUpdateUser">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="添加用户"
      v-model="insertDialogVisible"
      width="30%"
      :before-close="handleInsertDialogClose"
    >
      <el-form :model="insertForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="insertForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="insertForm.passwordHash"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="insertForm.nickName"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="insertForm.email"></el-input>
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="insertForm.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="收件人姓名">
          <el-input v-model="insertForm.recipientName"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="insertForm.address"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <el-input v-model="insertForm.role"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/updateAvatar"
            :on-change="handleAvatarChange"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
          >
            <img
              v-if="insertFormAvatarPreview"
              :src="insertFormAvatarPreview"
              class="avatar"
            />
            <i v-else class="el-icon-plus avatar-uploader-icon">
              <el-icon><CirclePlus /></el-icon>
            </i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="insertDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleInsertUser">确 定</el-button>
      </span>
    </el-dialog>

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
import { reactive, onMounted, ref } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import axios from "axios";

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
//总数
const totalUsers = ref(0);
//用户数据
const users = reactive([]);
//查询数据
const searchQuery = ref("");

// const searchRole = ref("全部");

const editDialogVisible = ref(false);
const editForm = reactive({
  userID: "",
  username: "",
  nickName: "",
  passwordHash: "",
  email: "",
  phoneNumber: "",
  walletBalance: "",
  recipientName: "",
  address: "",
  role: "",
  avatar: "", // 这个用于存储头像的URL
  avatarFile: null, // 这个用于存储头像文件
});

const insertDialogVisible = ref(false);
const insertForm = reactive({
  userID: "",
  username: "",
  nickName: "",
  passwordHash: "",
  email: "",
  phoneNumber: "",
  walletBalance: "",
  recipientName: "",
  address: "",
  role: "",
  avatar: "", // 这个用于存储头像的URL
  avatarFile: null, // 这个用于存储头像文件
});

const isAvatarDialogVisible = ref(false); // 编辑头像弹窗可见性
const currentEditUser = ref({}); // 当前正在编辑的用户
const uploadAvatarPreview = ref(""); //头像预览

//搜索
const onSearch = async () => {
  if (searchQuery.value === null || searchQuery.value === "") {
    getUsers();
  } else {
    try {
      const response = await axios.get(
        "http://192.168.1.112:8080/findUserByUsername",
        {
          params: {
            username: searchQuery.value,
            page: currentPage.value - 1, // 分页索引从0开始
            size: pageSize.value,
          },
        }
      );
      users.splice(0, users.length, ...response.data.users); // 更新用户列表
      // console.log(response.data);
      totalUsers.value = response.data.total; // 更新总用户数
    } catch (err) {
      console.log("查询用户错误！", err);
    }
  }
};

// 显示编辑头像的弹窗
const showEditAvatarDialog = (user) => {
  // console.log(user);
  currentEditUser.userID = user.userID;
  isAvatarDialogVisible.value = true;
  uploadAvatarPreview.value = user.avatar;
};

// 处理头像上传
const handleAvatarUpload = async () => {
  if (!currentEditUser.userID || !currentEditUser.avatarFile) {
    alert("用户ID或头像文件缺失");
    return;
  }

  let formData = new FormData();
  formData.append("id", currentEditUser.userID);
  formData.append("avatarFile", currentEditUser.avatarFile);

  try {
    const response = await axios.post(
      "http://192.168.1.112:8080/updateAvatar",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );

    if (response.data === "avatar updated") {
      // 假设后端响应包含 success 字段表示上传成功
      ElMessage.success("头像更新成功");

      isAvatarDialogVisible.value = false;
      getUsers(); // 如果需要，重新获取用户列表以更新UI
    } else if (response.data === "avatarFile or id not found") {
      // 处理后端返回的失败情况
      alert("头像或id未获取！");
    } else if (response.data === "avatar update error") {
      // 处理后端返回的失败情况
      alert("头像更新失败！error1", response.data);
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
    currentEditUser.avatarFile = file.raw;

    // 创建一个 URL 对象用于预览
    uploadAvatarPreview.value = URL.createObjectURL(file.raw);
  } else {
    // 如果没有文件被选择，清除预览
    uploadAvatarPreview.value = "";
  }
};

// 获取所有用户请求（带分页）
const getUsers = async () => {
  try {
    const response = await axios.get(
      "http://192.168.1.112:8080/getAllUsersPaginated",
      {
        params: {
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );

    users.splice(0, users.length, ...response.data.users); // 更新用户列表
    // console.log(response.data);
    totalUsers.value = response.data.total; // 更新总用户数
  } catch (error) {
    console.error("Error fetching users:", error);
  }
};

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getUsers();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getUsers();
};

//组件加载时调用
onMounted(getUsers);

const handleAdd = () => {
  // 添加用户逻辑
  insertDialogVisible.value = true;
};

// 打开编辑弹窗
const handleEdit = (user) => {
  editForm.userID = user.userID;
  editForm.username = user.username;
  editForm.nickName = user.nickName;
  editForm.passwordHash = user.passwordHash;
  editForm.email = user.email;
  editForm.phoneNumber = user.phoneNumber;
  editForm.walletBalance = user.walletBalance;
  editForm.recipientName = user.recipientName;
  editForm.address = user.address;
  editForm.role = user.role;
  editForm.avatar = user.avatar;

  editDialogVisible.value = true;
  // console.log(editDialogVisible.value);
};

// 更新用户信息
const handleUpdateUser = async () => {
  try {
    const response = await axios.put(
      "http://192.168.1.112:8080/adminUpdateUserById",
      editForm
    );
    if (response.data === "User Updated") {
      getUsers();
      ElMessage.success("用户信息更新成功！");
      editDialogVisible.value = false;
    } else {
      alert("更新失败：" + response.data);
    }
  } catch (error) {
    alert("请求错误:", error);
    console.error("更新失败:", error);
  }
};

//添加用户
const handleInsertUser = async () => {
  if (!insertForm.username) {
    ElMessage.error("用户名不能为空");
    return;
  }
  if (!insertForm.walletBalance) {
    insertForm.walletBalance = 0.0; // 或者其他合理的默认值
  }
  const formData = new FormData();
  Object.keys(insertForm).forEach((key) => {
    if (key === "avatarFile" && insertForm[key] != null) {
      formData.append("avatarFile", insertForm[key]); // 确保使用正确的字段名
    } else {
      formData.append(key, insertForm[key]);
    }
  });

  try {
    const response = await axios.post(
      "http://192.168.1.112:8080/adminInsertUser",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    if (response.data === "User Inserted") {
      getUsers();
      ElMessage.success("用户信息添加成功！");

      insertForm.userID = "";
      insertForm.username = "";
      insertForm.nickName = "";
      insertForm.passwordHash = "";
      insertForm.email = "";
      insertForm.phoneNumber = "";
      insertForm.walletBalance = "";
      insertForm.recipientName = "";
      insertForm.address = "";
      insertForm.role = "";
      insertForm.avatar = "";
      insertForm.avatarFile = null;
      insertDialogVisible.value = false;
    } else if (response.data === "User Insert error") {
      alert("用户信息添加失败！");
    } else {
      alert("添加失败：" + response.data);
    }
  } catch (error) {
    alert("请求错误:", error);
    console.error("添加失败:", error);
  }
};

const beforeAvatarUpload = (file) => {
  const isJPGorPNG = file.type === "image/jpeg" || file.type === "image/png";
  if (!isJPGorPNG) {
    this.$message.error("上传头像图片只能是 JPG/PNG 格式!");
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    this.$message.error("上传头像图片大小不能超过 2MB!");
    return false;
  }
  return isJPGorPNG && isLt2M;
};

const insertFormAvatarPreview = ref(""); // 用于头像预览的响应式变量

const handleAvatarChange = (file, fileList) => {
  if (file && file.raw) {
    // 存储文件对象
    insertForm.avatarFile = file.raw;

    // 创建一个 URL 对象用于预览
    insertFormAvatarPreview.value = URL.createObjectURL(file.raw);
  } else {
    // 如果没有文件被选择，清除预览
    insertFormAvatarPreview.value = "";
  }
  console.log(insertForm);
};

// 关闭编辑弹窗前的处理
const handleEditDialogClose = () => {
  editDialogVisible.value = false;
};

const handleInsertDialogClose = () => {
  insertDialogVisible.value = false;
};

//根据id删除用户
const handleDelete = async (userId) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该用户吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await axios.delete(
          "http://192.168.1.112:8080/deleteUserById?id=" + userId
        );
        if (response.data === "User Deleted") {
          // 删除成功后从 users 中移除该用户
          getUsers();
          ElMessage.success("用户删除成功！");
        } else if (response.data === "User Not Found") {
          // 处理后端返回的删除失败情况
          alert("用户不存在！");
        } else {
          alert("删除失败！");
        }
      } catch (error) {
        alert("请求错误:", error);
        console.error("删除失败:", error);
        // 可以显示一些错误信息
      }
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
};
</script>

<style scoped>
.user-management {
  margin: 20px;
  max-width: 150%;
}

.user-avatar {
  width: 50px; /* 调整为合适的尺寸 */
  height: 50px; /* 调整为合适的尺寸 */
  object-fit: cover; /* 保持图片比例 */
}

.icon-setting,
.icon-delete {
  cursor: pointer; /* 鼠标悬停时变成手形 */
  color: #000; /* 默认颜色 */
  font-size: 20px; /* 图标大小 */
  margin-right: 10px; /* 图标间距 */
}

.icon-setting:hover,
.icon-delete:hover {
  color: #42b983; /* 鼠标悬停时的颜色 */
  transform: scale(1.2); /* 鼠标悬停时变大 */
}

.pagination-wrapper {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 根据需要添加一些顶部外边距 */
}

.avatar-uploader {
  cursor: pointer;
  display: block;
  width: 100px;
  height: 100px;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.user-avatar {
  cursor: pointer;
}
.avatar-container {
  text-align: center;
}
.upload-placeholder {
  background-color: #f2f2f2;
  border: 2px dashed #ccc;
  padding: 20px;
  cursor: pointer;
}

.avatar-container {
  /* max-width: 200px; 设置容器的宽度 */
  height: 200px; /* 设置容器的高度 */
  position: relative; /* 如果需要在容器内部居中显示图片，可以使用相对定位 */
}

.search-input {
  margin-right: 10%;
  margin-left: 5%;
  width: 50%;
}
.manager_head {
  display: flex;
  width: 100%;
}

.elRadio {
  margin-left: 20px;
}
</style>
