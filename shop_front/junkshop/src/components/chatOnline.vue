<template>
  <div class="chat-container">
    <!-- 用户列表 -->
    <div class="user-list">
      <!-- 用户列表项 -->
      <el-card
        v-for="user in users"
        :key="user.id"
        class="user-card"
        @click="selectUser(user)"
        :class="{ 'is-active': selectedUser === user }"
      >
        <div class="user-info">
          <el-avatar :src="user.avatar"></el-avatar>
          <div class="user-name">{{ user.username }}</div>
        </div>
        <div class="user-message">{{ user.lastMessage }}</div>
      </el-card>
    </div>
    <div class="divider"></div>
    <!-- 聊天窗口和输入框 -->
    <div class="chat-window">
      <el-card class="message-container">
        <!-- 聊天消息 -->
        <div
          v-for="message in messages"
          :key="message.id"
          class="message"
          :class="{ 'is-user': message.fromUserID === user.userID }"
        >
          <div class="message-content">{{ message.content }}</div>
        </div>
      </el-card>
      <!-- 输入框和发送按钮 -->
      <div class="input-container">
        <el-input
          v-model="currentMessage"
          type="textarea"
          placeholder="输入消息..."
          class="message-input"
          @keyup.enter="sendMessage"
          :rows="6"
        ></el-input>
        <div class="send-button">
          <el-button type="primary" @click="sendMessage"
            ><el-icon><Promotion /></el-icon>发送</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, defineProps } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import SockJS from "sockjs-client/dist/sockjs.min.js";
import { Stomp } from "@stomp/stompjs";

// 定义 props
const { sellerID } = defineProps({
  sellerID: {
    type: Number,
    default: 0,
  },
});

const ws = ref(null);
const users = ref([]); // 用户列表
const messages = ref([]); // 消息列表
const currentMessage = ref(""); // 当前输入的消息
const selectedUser = ref(null); // 选定的用户
const user = ref(null); // 当前用户
let isConnected = false;
// 获取对话用户列表
const fetchUsersFromMessages = async () => {
  try {
    const response = await axios.get("http://192.168.1.112:8080/getAllUsers");
    users.value = response.data;
  } catch (error) {
    console.error("获取用户列表失败", error);
  }
};

// 初始化WebSocket连接
const connectWebSocket = () => {
  // 使用SockJS创建连接
  const socket = new SockJS("http://192.168.1.112:8080/chat");
  ws.value = Stomp.over(socket);

  ws.value.connect(
    {},
    (frame) => {
      // ElMessage.success("在线聊天线路连接成功！");
      isConnected = true;
      // 订阅/topic/messages
      ws.value.subscribe("/topic/messages", (message) => {
        const receivedMsg = JSON.parse(message.body);
        messages.value.push(receivedMsg);
      });
    },
    (error) => {
      console.error("WebSocket连接错误: ", error);
      ElMessage.error("在线聊天线路连接错误！");
    }
  );
};

// 发送消息
const sendMessage = () => {
  if (user.value.userID === selectedUser.value.userID) {
    ElMessage.error("不能和自己聊天！");
    return;
  }
  if (currentMessage.value.trim()) {
    const msg = {
      fromUserID: user.value.userID, // 应替换为实际的用户ID
      toUserID: selectedUser.value.userID, // 假设selectedUser有id属性
      content: currentMessage.value,
    };
    ws.value.send("/app/sendMessage", {}, JSON.stringify(msg));
    currentMessage.value = "";
  }
};

onMounted(() => {
  connectWebSocket();
  fetchUsersFromMessages();
});

onBeforeUnmount(() => {
  if (isConnected === true) {
    ws.value.disconnect();
    isConnected = false;
    // ElMessage.success("在线聊天线路连接已关闭！");
  }
});

const selectUser = (user) => {
  selectedUser.value = user;

  // 假设这个API根据ToUserID返回与该用户的所有聊天记录
  loadMessages(user.userID); // 加载与选定用户的聊天记录
};

const loadMessages = async (value) => {
  try {
    const response = await axios.get("http://192.168.1.112:8080/getToMessage", {
      params: {
        fromUserID: user.value.userID,
        toUserID: value,
      },
    });
    messages.value = response.data;
  } catch (error) {
    console.error("加载聊天记录失败", error);
  }
};

//获取当前用户信息
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
    user.value = response.data;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }

  console.log("-------------------------------", sellerID);
  if (sellerID > 0) {
    const res = await axios.get("http://192.168.1.112:8080/getUserByUserID", {
      params: { userID: sellerID },
    });
    selectedUser.value = res.data;
    loadMessages(sellerID);
  }
};

onMounted(() => {
  getUserInfo(); // 现有逻辑
  fetchUsersFromMessages(); // 新添加的逻辑，替换原fetchUsers函数
});
</script>

<style scoped>
.chat-container {
  display: flex;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 外层阴影 */
  border-radius: 10px; /* 外层圆角 */
  overflow: hidden; /* 防止子元素超出边界 */
  height: 800px; /* 根据需要调整高度 */
  width: 1000px;
}

.user-list {
  flex: 1;
  overflow-y: auto;
  min-width: 200px; /* 用户列表最大宽度 */
}

.user-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  padding-right: 0;
  cursor: pointer;
  /* width: 180px; */
}

.user-card.is-active {
  background-color: #b1b2b4; /* 激活状态的背景色 */
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 10px;
}

.message-container {
  flex-grow: 1; /* 占据可用空间 */
  flex: 3;
  overflow-y: auto;
  /* padding: 20px; */
}

.message {
  display: flex;
  margin-bottom: 10px;
}

.message.is-user {
  justify-content: flex-end;
}

.message-content {
  margin-left: 10px;
  background-color: #f0f0f0;
  border-radius: 15px;
  padding: 10px;
}

.input-container {
  border-top: 1px solid #bebebe; /* 输入框上方的黑色分割线 */
  display: flex;
  padding: 10px;
  align-items: center;
  gap: 10px; /* 间隙 */
}

.input-container {
  /* margin-right: 10px; */
  flex: 1;
}

.divider {
  background-color: #bebebe; /* 黑色分割线 */
  width: 1px;
  height: 100%;
  margin: 0 0 0 0;
}

.chat-window {
  display: flex;
  flex-direction: column;
  width: 100%; /* 占据剩余空间 */
}

.send-button {
  position: relative; /* 设置为相对定位 */
  /* bottom: 250px; /* 距离底部10px */
  /* right: 500px; 距离右侧10px */
  display: flex; /* 设置为flex布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

/* 聊天容器样式 */
.chat-container {
  /* border: 1px solid #000; 外围黑色边框 */
  margin: 0 0 0 0;
  display: flex;
  height: 600px; /* 调整高度或根据需要设置为100% */
}

.user-list {
  width: 250px; /* 用户列表固定宽度 */
  overflow-y: auto; /* 可滚动 */
}
</style>
