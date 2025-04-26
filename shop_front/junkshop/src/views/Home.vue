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
          <el-menu-item index="9">我的收藏</el-menu-item>
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
            @order="handleOrderSubmit"
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
        
        <!-- ---------------------------------9 我的收藏 --------------------------------- -->
        <div v-if="activeIndex === '9'">
          <MyFavorites
            @update-nav="handleNavigation"
            @get-item-from-display="getItemFromDisplay"
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
import { ref, watch, onMounted, nextTick } from "vue";
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
import MyFavorites from '../components/MyFavorites.vue';

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
const pendingPaymentOrder = ref(null);
const showPendingPayment = ref(false);

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

const handleNavigation = (newNav, params) => {
  activeIndex.value = newNav;

  // 如果有传递参数，且切换到订单页面，可以在这里处理
  if (newNav === '4' && params) {
    // 处理订单页面的特殊参数
  }
};

// 从购物车查看商品详情
const viewItemFromCart = (item) => {
  // 调整字段名，确保与ShopInfo组件匹配
  selectedItem.value = {
    ...item,
    name: item.itemName || item.name, // 适配不同来源的商品名称
    status: item.status || '已上架', // 添加默认状态
  };
  activeIndex.value = '6';
};

// 处理购物车结算
const handleCartCheckout = (items) => {
  // 这里可以处理结算逻辑，比如跳转到结算页面
  // 暂时实现为打开第一个商品的购买对话框
  if (items && items.length > 0) {
    // 调整字段名，确保与ShopInfo组件匹配
    selectedItem.value = {
      ...items[0],
      name: items[0].itemName || items[0].name, // 适配不同来源的商品名称
      status: items[0].status || '已上架', // 添加默认状态
    };
    activeIndex.value = '6';
    // 通知子组件打开购买对话框，这里需要子组件提供相应接口
  }
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

// 处理订单提交
const handleOrderSubmit = (eventData) => {
  // 判断是否有订单支付数据传递过来
  if (eventData && eventData.showPayment && eventData.order) {
    // 保存到待支付订单，切换到订单页面后会使用
    pendingPaymentOrder.value = eventData.order;
    showPendingPayment.value = true;
  }
  // 切换到订单页面
  activeIndex.value = '4';
};

// 监听活动菜单项变化
watch(activeIndex, (newValue) => {
  // 当切换到订单页面时，检查是否有待支付订单需要显示
  if (newValue === '4' && pendingPaymentOrder.value && showPendingPayment.value) {
    // 在下一个tick触发，确保MyOrders组件已挂载
    nextTick(() => {
      // 这里我们通过事件总线或refs来触发MyOrders组件中的方法
      // 由于我们没有直接访问子组件的方法，所以使用localStorage作为中介
      localStorage.setItem('pendingPaymentOrder', JSON.stringify(pendingPaymentOrder.value));
      localStorage.setItem('showPendingPayment', 'true');
      
      // 清除状态，防止重复显示
      pendingPaymentOrder.value = null;
      showPendingPayment.value = false;
      
      // 刷新订单页面(可选，根据项目需求)
      window.dispatchEvent(new CustomEvent('refresh-orders'));
    });
  }
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
