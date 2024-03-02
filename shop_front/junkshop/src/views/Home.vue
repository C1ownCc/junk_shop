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
            @getItemFromDisplay="getItemFromDisplay"
            index="5"
          />
        </div>
        <!-- ---------------------------------2--------------------------------- -->
        <div v-if="activeIndex === '2'">
          <PersonalShop />
        </div>

        <!-- ---------------------------------3--------------------------------- -->
        <div v-if="activeIndex === '3'">
          <PersonalInfo />
        </div>

        <!-- ---------------------------------4--------------------------------- -->
        <div v-if="activeIndex === '4'" class="orders-container">
          <el-icon
            ><Switch class="icons" @click="switchOrder = !switchOrder"
          /></el-icon>

          <div class="orders-column" v-if="switchOrder">
            <MyOrders />
          </div>
          <div class="orders-column" v-else>
            <MyOrders2 />
          </div>
        </div>

        <!-- ---------------------------------5--------------------------------- -->
        <div v-if="activeIndex === '5'">
          <chatOnline :sellerID="sellerID" />
        </div>

        <!-- ---------------------------------6--------------------------------- -->
        <div v-if="activeIndex === '6'">
          <ShopInfo
            @back="activeIndex = '1'"
            @chat="activeIndex = '5'"
            @order="activeIndex = '4'"
            :selectedItem="selectedItem"
            @sellerID="getSellerID"
          />
        </div>
      </el-main>
    </el-container>
    <el-drawer v-model="drawer" title="公告" :with-header="false">
      <vue-markdown :source="content"></vue-markdown>
    </el-drawer>
  </div>
</template>

<script setup lang="js">

import { ref, watch ,onMounted} from "vue";
import ItemDisplay from "../components/ItemDisplay.vue"; // 确保路径正确
import PersonalShop from "../components/PersonalShop.vue";
import PersonalInfo from "../components/PersonalInfo.vue";
import MyOrders from "../components/MyOrders.vue";
import MyOrders2 from "../components/MyOrders2.vue";
import ShopInfo from "../components/ShopInfo.vue";
import chatOnline from "../components/chatOnline.vue";
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox } from "element-plus";
import VueMarkdown from 'vue3-markdown-it';
const router = useRouter(); // 获取 router 实例
//------------------------------------data-----------------------------------

//高亮导航
const activeIndex = ref("1");
const userRole = ref(null);
const nickname = ref("");
const avatar = ref("");
const switchOrder = ref(true);
const drawer = ref(false);


//组件传递选择的商品数据
const selectedItem = ref(null);
const getItemFromDisplay = (data) => {
  selectedItem.value = data;
}

const sellerID = ref(0);
const getSellerID = (value) =>{
  sellerID.value = value;
}

//------------------------------------------监听-----------------------------------------
watch(activeIndex, () => {
  window.scrollTo(0, 0);
});
//------------------------------------------方法-----------------------------------------
// 菜单高亮切换
const handleMenuSelect = (index) => {
  activeIndex.value = index;
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

//退出登录
const logout = () =>{
  ElMessageBox.confirm(
    "确认退出登陆吗？","Warning",{
      confirmButtonText: "确认退出",
    cancelButtonText: "再逛一会儿",
    type: "warning",
    }
  ).then(()=>{
    router.push('login');
    localStorage.clear();
  }).catch(()=>{});

}

const handleNavigation = (newNav) => {
  activeIndex.value = newNav
}
const content = ref("");
const getAnnouncement = async()=>{
  try {
    const response = await axios.get("http://192.168.1.112:8080/getContent");
    // console.log(response.data);
    content.value = response.data;
  } catch (error) {
    console.error("获取公告失败", error);
  }
}
onMounted(getAnnouncement);
onMounted(() => {
  userRole.value = localStorage.getItem('role');
  // console.log(userRole.value);
});
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
  align-items: center;
  justify-content: space-between;
  position: fixed; /* 固定位置 */
  top: 0; /* 顶部对齐 */
  left: 0; /* 左侧对齐 */
  right: 0; /* 右侧对齐 */
  width: 100%; /* 横向填满 */
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
  margin-top: 50px;
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 根据需要调整 ItemDisplay 组件的样式 */
.item-display {
  /* 可能需要的样式 */
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

.orders-column {
  flex: 1; /* 两边列占满可用空间 */
}

.column-title {
  font-size: 1.5em;
  margin-bottom: 50px; /* 与下面组件的间距 */
}

.divider {
  width: 1px; /* 分隔线的宽度 */
  background-color: #e0e0e0; /* 分隔线的颜色 */
  margin: 0 20px; /* 与两边组件的间距 */
}

.orders-header {
  background-color: #eef1f6;
  padding: 10px 0;
  border-bottom: 1px solid #d3dce6;
  margin-bottom: 30px;
}

.grid-content {
  text-align: center;
  padding-left: 20px;
  padding-right: 0px;
  width: 100px;
}

.orders-row {
  justify-content: space-evenly;
}

.icons {
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.icons:hover {
  color: #409eff; /* 鼠标悬停时的颜色，根据需要调整 */
}
</style>
