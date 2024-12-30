<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <div class="user-profile">
          <img :src="avatar" alt="用户头像" class="user-avatar" />
          <span class="user-nickname">{{ nickname }}</span>
        </div>

        <el-menu
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
        </el-menu>

        <div class="action-buttons">
          <el-button 
            type="primary" 
            link 
            @click="this.$router.push('home')"
            class="action-btn"
          >
            <el-icon><House /></el-icon>
          </el-button>
          <el-button 
            type="danger" 
            link 
            @click="logout"
            class="action-btn"
          >
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
      </el-header>

      <el-main>
        <div class="content-wrapper">
          <div v-if="activeIndex === '1'" class="manager-container">
            <AdminUserManager />
          </div>
          <div v-if="activeIndex === '2'" class="manager-container">
            <AdminItemsManager />
          </div>
          <div v-if="activeIndex === '3'" class="manager-container">
            <AdminOrdersManager />
          </div>
          <div v-if="activeIndex === '4'" class="manager-container">
            <AdminMessageManager />
          </div>
          <div v-if="activeIndex === '5'" class="manager-container">
            <AdminAnnouncementManager />
          </div>
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
      "http://localhost:8080/getUserByUsername",
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

<style scoped>
.common-layout {
  min-height: 100vh;
  background-color: #f8fafc;
}

.el-header {
  height: 64px;
  background-color: #fff;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-nickname {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.header-menu {
  border: none;
  background: transparent;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu-item) {
  font-size: 14px;
  height: 64px;
  line-height: 64px;
  padding: 0 20px;
  color: #64748b;
}

:deep(.el-menu-item.is-active) {
  color: #3b82f6;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: #f1f5f9;
  transform: translateY(-1px);
}

.el-main {
  padding: 88px 24px 24px;
  min-height: calc(100vh - 64px);
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
}

.manager-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-header {
    padding: 0 16px;
  }

  .user-nickname {
    display: none;
  }

  :deep(.el-menu-item) {
    padding: 0 12px;
  }

  .el-main {
    padding: 80px 16px 16px;
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
