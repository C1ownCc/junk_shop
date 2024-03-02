<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <div class="user-profile">
          <img :src="avatar" alt="用户头像" class="user-avatar" />
          <span class="user-nickname">{{ nickname }}</span>
          <!-- <el-icon><ArrowDown /></el-icon> -->
        </div>

        <el-menu
          background-color="#f0f0f0"
          text-color="#333"
          active-text-color="#409EFF"
          class="header-menu"
          :default-active="activeIndex"
          mode="horizontal"
          :ellipsis="false"
          @select="handleMenuSelect"
        >
          <el-menu-item index="1">用户管理</el-menu-item>
          <el-menu-item index="2">商品管理</el-menu-item>
          <el-menu-item index="3">订单管理</el-menu-item>
          <el-menu-item index="4">管理员消息</el-menu-item>
          <el-menu-item index="5">公告</el-menu-item>
          <!-- 根据需要添加更多菜单项 -->
        </el-menu>
        <div class="logout-icon">
          <el-icon class="logout-icon-in"
            ><House @click="this.$router.push('home')"
          /></el-icon>
          <el-icon class="logout-icon-in"
            ><SwitchButton @click="logout"
          /></el-icon>
        </div>
      </el-header>
      <el-main>
        <div v-if="activeIndex === '1'" style="width: 150%">
          <AdminUserManager />
        </div>
        <div v-if="activeIndex === '2'" style="width: 150%">
          <AdminItemsManager />
        </div>
        <div v-if="activeIndex === '3'" style="width: 125%">
          <AdminOrdersManager />
        </div>
        <div v-if="activeIndex === '4'" style="width: 150%">
          <AdminMessageManager />
        </div>
        <div v-if="activeIndex === '5'">
          <AdminAnnouncementManager />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
// import VueMasonry from 'vue-masonry-css';
// 引入 vue-masonry-css
import AdminUserManager from "../components/AdminUserManager.vue";
import AdminItemsManager from "../components/AdminItemsManager.vue";
import AdminOrdersManager from "../components/AdminOrdersManager.vue";
import AdminMessageManager from "../components/AdminMessageManager.vue";
import AdminAnnouncementManager from "../components/AdminAnnouncementManager.vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { ElMessageBox } from "element-plus";

const router = useRouter(); // 获取 router 实例
const nickname = ref("");
const avatar = ref("");
const activeIndex = ref("1");
const handleMenuSelect = (index) => {
  activeIndex.value = index; // 根据选中的菜单项更新变量
};

const logout = () => {
  ElMessageBox.confirm("确认退出登陆吗？", "Warning", {
    confirmButtonText: "确认退出",
    cancelButtonText: "再逛一会儿",
    type: "warning",
  })
    .then(() => {
      router.push("login");
      localStorage.clear();
    })
    .catch(() => {});
};

const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      console.error(
        "无法获取用户信息，用户未登录或 localStorage 中无用户数据。"
      );
      return;
    }

    const response = await axios.get(
      "http://192.168.1.112:8080/getUserByUsername",
      {
        params: { username: userInfo.username },
      }
    );
    // console.log(response.data.nickName);
    nickname.value = response.data.nickName;
    avatar.value = response.data.avatar;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

onMounted(getUserInfo);
</script>

<style>
.el-header {
  background-color: #f0f0f0;
  padding: 0 20px;
  line-height: 60px;
  border-radius: 10px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
  z-index: 1000;
  display: flex;
  position: relative;
  align-items: center;
  justify-content: space-between; /* 调整为两端对齐 */
}

.user-profile {
  display: flex;
  align-items: center;
  position: absolute;
  left: 20px; /* 与左侧边界的距离 */
}

.header-menu {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  box-shadow: none;
  z-index: 500; /* 低于 user-profile 和 logout-icon 的层级 */
}

.logout-icon {
  position: absolute;
  right: 20px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.logout-icon-in:hover {
  transform: scale(1.2);
  color: #409eff; /* 鼠标悬停时的颜色，根据需要调整 */
}

.el-main {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-nickname {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 40px;
}

.el-icon-switch-button {
  font-size: 20px;
  cursor: pointer;
}

html {
  overflow-y: scroll;
}

.orders-container {
  display: flex;
  justify-content: space-between;
}
</style>
