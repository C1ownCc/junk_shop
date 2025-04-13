<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <div class="user-profile">
          <img :src="avatar" alt="用户头像" class="user-avatar" />
          <!-- <span class="user-nickname">用户昵称</span> -->
          <!-- <el-icon><ArrowDown /></el-icon> -->
          <el-dropdown>
            <span class="user-nickname">
              {{ nickname }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="drawer = true">公告</el-dropdown-item>
                <div v-if="userRole === '0'">
                  <el-dropdown-item @click="this.$router.push('admin')"
                    >管理员界面</el-dropdown-item
                  >
                </div>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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
          <el-menu-item index="1">商品市场</el-menu-item>
          <el-menu-item index="2">我的商品</el-menu-item>
          <el-menu-item index="3">个人信息</el-menu-item>
          <el-menu-item index="4">我的订单</el-menu-item>
          <el-menu-item index="5">我的消息</el-menu-item>
          <el-menu-item index="8">购物车</el-menu-item>
          <el-menu-item index="7">AI客服</el-menu-item>
          <!-- 根据需要添加更多菜单项 -->
        </el-menu>
        <div class="logout-icon">
          <!-- <el-icon class="logout-icon-in"
            ><House @click="this.$router.push('home')"
          /></el-icon> -->
          <el-icon class="logout-icon-in"
            ><SwitchButton @click="logout"
          /></el-icon>
        </div>
      </el-header>
      <el-main>
        <!-- ---------------------------------1--------------------------------- -->
        <div v-if="activeIndex === '1'">
          <ItemDisplay
            class="ItemDisplay"
            @update-nav="handleNavigation"
            @get-item-from-display="getItemFromDisplay"
          />
        </div>
        <!-- ---------------------------------2--------------------------------- -->
        <div v-if="activeIndex === '2'" class="shop-container">
          <PersonalShop />
        </div>

        <!-- ---------------------------------3--------------------------------- -->
        <div v-if="activeIndex === '3'">
          <PersonalInfo />
        </div>

        <!-- ---------------------------------4--------------------------------- -->
        <div v-if="activeIndex === '4'" class="orders-container">
          <div class="orders-header">
            <h2 class="orders-title">订单管理</h2>
            <el-switch
              v-model="switchOrder"
              class="order-switch"
              style="--el-switch-on-color: #409EFF; --el-switch-off-color: #67c23a"
              :active-text="'购买订单'"
              :inactive-text="'出售订单'"
              inline-prompt
              size="large"
            />
          </div>
          <div class="orders-content">
            <transition name="fade" mode="out-in">
              <MyOrders v-if="switchOrder" />
              <MyOrders2 v-else />
            </transition>
          </div>
        </div>

        <!-- ---------------------------------5--------------------------------- -->
        <div v-if="activeIndex === '5'">
          <chatOnline :sellerID="sellerID" :key="sellerID" />
        </div>

        <!-- ---------------------------------6--------------------------------- -->
        <div v-if="activeIndex === '6'">
          <ShopInfo
            @back="activeIndex = '1'"
            @chat="handleChat"
            @order="activeIndex = '4'"
            :selectedItem="selectedItem"
          />
        </div>
        <div v-if="activeIndex === '7'">
          <AIChat />
        </div>
        <!-- ---------------------------------8 购物车--------------------------------- -->
        <div v-if="activeIndex === '8'">
          <ShoppingCart
            @update-nav="handleNavigation"
            @view-item="viewItemFromCart"
            @on-checkout="handleCartCheckout"
          />
        </div>
        <!-- ---------------------------------9 购物车结算--------------------------------- -->
        <div v-if="activeIndex === '9'">
          <CartCheckout
            :cartItems="checkoutItems"
            @update-nav="handleNavigation"
            @checkout-complete="handleCheckoutComplete"
          />
        </div>
      </el-main>
    </el-container>
    <el-drawer 
      v-model="drawer" 
      title="系统公告" 
      size="50%"
      :destroy-on-close="false"
    >
      <div class="announcement-container">
        <div class="announcement-header">
          <h2>系统公告</h2>
          <span class="announcement-time">更新时间: {{ formatDate(new Date()) }}</span>
        </div>
        <div class="announcement-content">
          <vue-markdown 
            :source="content" 
            :html="true" 
            :breaks="true" 
            :linkify="true"
            class="custom-markdown"
          ></vue-markdown>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import ItemDisplay from "../components/ItemDisplay.vue";
import PersonalShop from "../components/PersonalShop.vue";
import PersonalInfo from "../components/PersonalInfo.vue";
import MyOrders from "../components/MyOrders.vue";
import MyOrders2 from "../components/MyOrders2.vue";
import ShopInfo from "../components/ShopInfo.vue";
import chatOnline from "../components/chatOnline.vue";
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox } from "element-plus";
import AIChat from '../components/AiChat.vue';
import VueMarkdown from 'vue3-markdown-it';
import 'github-markdown-css/github-markdown.css';
import { DArrowLeft, DArrowRight } from '@element-plus/icons-vue';
import ShoppingCart from '../components/ShoppingCart.vue';
import CartCheckout from '../components/CartCheckout.vue';

const router = useRouter();

// 基础数据
const activeIndex = ref("1");
const userRole = ref(null);
const nickname = ref("");
const avatar = ref("");
const switchOrder = ref(true);
const drawer = ref(false);
const content = ref("");
const selectedItem = ref(null);
const sellerID = ref(0);
const checkoutItems = ref([]);
const submitting = ref(false);

// 数据处理方法
const getItemFromDisplay = (data) => {
  selectedItem.value = data;
};

const handleChat = (id) => {
  sellerID.value = id;
  activeIndex.value = '5';
};

const handleMenuSelect = (index) => {
  activeIndex.value = index;
};

const handleNavigation = (newNav) => {
  activeIndex.value = newNav;
};

// 从购物车查看商品详情
const viewItemFromCart = (item) => {
  selectedItem.value = item;
  activeIndex.value = '6';
};

// 处理购物车结算
const handleCartCheckout = (items) => {
  if (items && items.length > 0) {
    // 先将商品信息保存
    submitting.value = true;
    
    // 获取所有商品的完整信息
    Promise.all(items.map(async (item) => {
      try {
        const response = await axios.get(`http://localhost:8080/getItemById`, {
          params: { id: item.itemID }
        });
        if (response.data) {
          // 合并商品信息
          return {
            ...item,
            sellerID: response.data.sellerID,
            description: response.data.description || '',
            desc: response.data.description || '',
            // 保留购物车的数量和ID
            quantity: item.quantity,
            cartItemID: item.cartItemID
          };
        }
        return item;
      } catch (error) {
        console.error(`获取商品ID ${item.itemID} 的详情失败`, error);
        return item;
      }
    }))
    .then(completeItems => {
      // 更新结算商品列表为完整信息
      checkoutItems.value = completeItems;
      // 跳转到结算页面
      activeIndex.value = '9';
      submitting.value = false;
    })
    .catch(error => {
      console.error("获取商品详情失败", error);
      ElMessage.error("结算处理失败，请重试");
      submitting.value = false;
    });
  } else {
    ElMessage.warning("请选择要结算的商品");
  }
};

// 结算完成处理
const handleCheckoutComplete = () => {
  // 清空结算商品列表
  checkoutItems.value = [];
  // 跳转到订单页面
  activeIndex.value = '4';
  // 切换到购买订单视图
  switchOrder.value = true;
  // 显示成功消息
  ElMessage.success("订单支付成功，请在订单页面查看");
};

const formatDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// API 调用方法
const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      console.error("无法获取用户信息，用户未登录或 localStorage 中无用户数据。");
      return;
    }
    const response = await axios.get("http://localhost:8080/getUserByUsername", {
      params: { username: userInfo.username },
    });
    nickname.value = response.data.nickName;
    avatar.value = response.data.avatar;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

const getAnnouncement = async () => {
  try {
    const response = await axios.get("http://localhost:8080/getContent");
    content.value = response.data;
  } catch (error) {
    console.error("获取公告失败", error);
  }
};

const logout = () => {
  ElMessageBox.confirm("确认退出登陆吗？", "Warning", {
    confirmButtonText: "确认退出",
    cancelButtonText: "再逛一会儿",
    type: "warning",
  })
    .then(() => {
      router.push('login');
      localStorage.clear();
    })
    .catch(() => {});
};

// 监听器
watch(activeIndex, () => {
  window.scrollTo(0, 0);
});

// 生命周期钩子
onMounted(async () => {
  await getUserInfo();
  await getAnnouncement();
  userRole.value = localStorage.getItem('role');
});
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
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-menu {
  border: none;
  background: transparent !important;
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

:deep(.el-menu-item:hover) {
  color: #3b82f6;
  background-color: #f1f5f9;
}

.logout-icon {
  display: flex;
  gap: 8px;
}

.logout-icon-in {
  padding: 8px;
  border-radius: 8px;
  font-size: 20px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-icon-in:hover {
  background-color: #f1f5f9;
  transform: translateY(-1px);
}

.el-main {
  padding: 88px 24px 24px;
  min-height: calc(100vh - 64px);
}

/* 订单管理区域样式 */
.orders-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.orders-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

/* 公告抽屉样式 */
.announcement-container {
  padding: 24px;
}

.announcement-header {
  margin-bottom: 24px;
}

.announcement-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.announcement-time {
  color: #64748b;
  font-size: 14px;
}

.announcement-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-header {
    padding: 0 16px;
  }

  .user-nickname span {
    display: none;
  }

  :deep(.el-menu-item) {
    padding: 0 12px;
  }

  .el-main {
    padding: 80px 16px 16px;
  }
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
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

.shop-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
</style>
