<template>
  <div class="chat-container">
    <!-- 左侧用户列表 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>消息列表</h3>
        <div class="online-status">
          <span class="status-dot"></span>
          在线
        </div>
      </div>
      
      <div class="user-list">
        <div
          v-for="chatUser in users"
          :key="chatUser.userID"
          class="user-item"
          @click="selectUser(chatUser)"
          :class="{ 'is-active': selectedUser && selectedUser.userID === chatUser.userID }"
        >
          <el-avatar :src="chatUser.avatar" class="user-avatar"></el-avatar>
          <div class="user-info">
            <div class="user-name">{{ chatUser.username }}</div>
            <div class="last-message">{{ chatUser.lastMessage }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 聊天头部 -->
      <div class="chat-header" v-if="selectedUser">
        <div class="chat-title">
          <span class="selected-user-name">{{ selectedUser.username }}</span>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messageContainer">
        <div v-if="selectedUser && selectedUser.userID === user.userID" class="self-chat-notice">
          <el-empty description="无法与自己聊天" :image-size="100">
            <template #description>
              <p>这是您自己的聊天窗口</p>
              <p>请选择其他用户进行交谈</p>
            </template>
          </el-empty>
        </div>

        <template v-else>
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-wrapper"
            :class="{ 'is-user': message.fromUserID === user.userID }"
          >
            <div class="message-bubble">
              <div class="message-content">{{ message.content }}</div>
            </div>
          </div>
        </template>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area" v-if="selectedUser && selectedUser.userID !== user.userID">
        <div class="input-box">
          <el-input
            v-model="currentMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            resize="none"
            @keyup.enter.native="sendMessage"
          />
          <el-button 
            type="primary" 
            class="send-button" 
            @click="sendMessage"
            :disabled="!currentMessage.trim()"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, onUnmounted, nextTick } from "vue";
import axios from "axios";
import { ElMessage, ElNotification } from "element-plus";
import websocket from '../utils/websocket';

// 定义 props
const props = defineProps({
  sellerID: {
    type: Number,
    default: 0
  }
});

const ws = ref(null);
const users = ref([]); // 用户列表
const messages = ref([]); // 消息列表
const currentMessage = ref('');
const selectedUser = ref(null); // 选定的用户
const user = ref(null); // 当前用户
let isConnected = false;
const messageContainer = ref(null); // 初始化消息容器引用

// 获取对话用户列表
const fetchUsersFromMessages = async () => {
  try {
    const response = await axios.get("http://localhost:8080/getAllUsers");
    users.value = response.data;
  } catch (error) {
    console.error("获取用户列表失败", error);
  }
};

// 初始化WebSocket连接
const connectWebSocket = () => {
  websocket.init((data) => {
    try {
      const receivedMsg = JSON.parse(data);
      console.log('收到消息:', receivedMsg);
      
      // 如果消息是发给当前用户的，或者是当前用户发送的
      if (receivedMsg.toUserID === user.value.userID || 
          receivedMsg.fromUserID === user.value.userID) {
        // 如果当前正在与消息相关的用户聊天，则更新消息列表
        if (selectedUser.value && 
           (receivedMsg.fromUserID === selectedUser.value.userID || 
            receivedMsg.toUserID === selectedUser.value.userID)) {
          loadMessages(selectedUser.value.userID);
        } else {
          // 否则显示通知
          showNotification(receivedMsg);
        }
      }
    } catch (error) {
      console.error('处理消息失败:', error);
    }
  });
};

// 发送消息
const sendMessage = () => {
  if (!currentMessage.value.trim() || !selectedUser.value) {
    return;
  }

  if (user.value.userID === selectedUser.value.userID) {
    ElMessage.error("不能和自己聊天！");
    return;
  }

  try {
    const msg = {
      fromUserID: user.value.userID,
      toUserID: selectedUser.value.userID,
      content: currentMessage.value.trim(),
      timestamp: Date.now()
    };

    if (websocket.send(JSON.stringify(msg))) {
      currentMessage.value = '';
    } else {
      ElMessage.error('发送消息失败，请重试');
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    ElMessage.error('发送消息失败，请重试');
  }
};

// 获取对话用户列表
const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
};

const fetchUserInfo = async (userID) => {
  try {
    const response = await axios.get("http://localhost:8080/getUserByUserID", {
      params: { userID },
    });
    return response.data;
  } catch (error) {
    console.error("获取用户信息失败", error);
    return null;
  }
};

const showNotification = async (message) => {
  // 如果消息不是发给当前用户，或者当前聊天对象是消息发送者，直接返回
  if (
    message.toUserID !== user.value.userID || // 消息不是发给当前用户
    (selectedUser.value && selectedUser.value.userID === message.fromUserID) // 当前聊天对象是消息发送者
  ) {
    return;
  }

  // 根据 fromUserID 查找用户信息
  let sender = users.value.find((user) => user.userID === message.fromUserID);

  // 如果本地未找到用户信息，则通过后端获取
  if (!sender) {
    sender = await fetchUserInfo(message.fromUserID);
    if (sender) {
      users.value.push(sender); // 缓存用户信息到用户列表
    }
  }

  const senderName = sender ? sender.username : `用户ID: ${message.fromUserID}`;
  const truncatedMessage =
    message.content.length > 10
      ? `${message.content.slice(0, 10)}...`
      : message.content;

  ElNotification({
    title: `新消息来自：${senderName}`,
    message: truncatedMessage,
    type: "info",
    duration: 3000, // 3秒后自动消失
    position: "top-right",
    onClick: () => {
      // 切换到对应用户的聊天框
      if (sender) {
        selectUser(sender);
      }
    },
  });
};

// 添加消息发送的回车处理
const handleEnter = (e) => {
  if (e.ctrlKey && e.keyCode === 13) {
    sendMessage();
  }
};

onMounted(async () => {
  try {
    await getUserInfo();
    await fetchUsersFromMessages();
    connectWebSocket();
  } catch (error) {
    console.error('初始化失败:', error);
    ElMessage.error('初始化失败，请刷新页面');
  }
});

onBeforeUnmount(() => {
  if (ws.value && isConnected) {
    try {
      ws.value.disconnect(() => {
        console.log('WebSocket连接已关闭');
        isConnected = false;
      });
    } catch (error) {
      console.error('关闭WebSocket连接失败:', error);
    }
  }
});

const selectUser = (user) => {
  selectedUser.value = user;
  // 假设这个API根据ToUserID返回与该用户的所有聊天记录
  loadMessages(user.userID); // 加载与选定用户的聊天记录
};

const loadMessages = async (value) => {
  try {
    const response = await axios.get("http://localhost:8080/getToMessage", {
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
      console.error("无法获取用户信息，用户未登录或 localStorage 中无用户数据。");
      return;
    }

    const response = await axios.get(
      "http://localhost:8080/getUserByUsername",
      {
        params: { username: userInfo.username },
      }
    );
    user.value = response.data;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }

  console.log("-------------------------------", props.sellerID);
  if (props.sellerID > 0) {
    const res = await axios.get("http://localhost:8080/getUserByUserID", {
      params: { userID: props.sellerID },
    });
    selectedUser.value = res.data;
    loadMessages(props.sellerID);
  }
};

// 监听消息变化，自动滚动到底部
watch(messages, async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
});

// 组件卸载时关闭WebSocket连接
onUnmounted(() => {
  websocket.close();
});

</script>

<style scoped>
.chat-container {
  display: flex;
  width: 1200px;
  height: calc(100vh - 100px); /* 减去顶部和底部的间距 */
  margin: 20px auto;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

/* 侧边栏样式 */
.sidebar {
  width: 280px;
  background: #f8f9fa;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.online-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #67c23a;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
}

/* 用户列表样式 */
.user-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 4px;
}

.user-item:hover {
  background: #f0f2f5;
}

.user-item.is-active {
  background: #ecf5ff;
}

.user-avatar {
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.last-message {
  color: #909399;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 主聊天区域样式 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}

.selected-user-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f8f9fa;
}

.message-wrapper {
  display: flex;
  margin-bottom: 16px;
  padding: 0 10px;
}

.message-wrapper.is-user {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
}

.message-content {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-wrapper.is-user .message-content {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-wrapper:not(.is-user) .message-content {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 输入区域样式 */
.chat-input-area {
  padding: 16px;
  border-top: 1px solid #ebeef5;
  background: white;
}

.input-box {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.send-button {
  height: 82px; /* 与输入框高度匹配 */
  padding: 0 24px;
}

.send-button:disabled {
  cursor: not-allowed;
}

/* 自己聊天提示样式 */
.self-chat-notice {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chat-container {
    width: 95%;
    margin: 10px auto;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 250px;
  }
}
</style>
