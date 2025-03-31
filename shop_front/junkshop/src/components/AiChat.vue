<template>
  <div class="ai-chat-page">
    <div class="chat-container">
      <!-- 头部区域 -->
      <div class="chat-header">
        <div class="header-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 智能客服</span>
        </div>
        <div class="model-info">
          模型：glm-4-flash
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messageContainer">
        <!-- 欢迎消息，仅在没有消息时显示 -->
        <div class="welcome-message" v-if="messages.length === 0">
          <h2>欢迎使用 AI 助手</h2>
          <p>我可以帮助您解答问题，请随时向我提问。</p>
        </div>

        <div v-for="msg in messages" 
             :key="msg.id" 
             :class="['message', msg.sender]">
          <div class="message-avatar">
            <el-avatar :size="36" :icon="msg.sender === 'ai' ? 'ChatDotRound' : 'User'" />
          </div>
          <div class="message-wrapper">
            <div class="message-sender">{{ msg.sender === 'ai' ? 'AI 助手' : '我' }}</div>
            <div class="message-content"
                 v-html="msg.sender === 'ai' ? renderMarkdown(msg.text) : msg.text">
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区域 -->
      <div class="chat-footer">
        <div class="chat-tip">
          <el-icon><InfoFilled /></el-icon>
          <span>提示：聊天记录在刷新后不会保存</span>
        </div>
        
        <div class="chat-input">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            resize="none"
          />
          <el-button 
            type="primary" 
            :disabled="!userInput.trim()" 
            @click="sendMessage"
            class="send-button"
          >
            发送
            <el-icon class="el-icon--right"><Position /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, defineComponent } from 'vue';
import axios from 'axios';
import { ElCard, ElInput, ElButton, ElMessage } from 'element-plus';
import { marked } from 'marked';

export default defineComponent({
  name: 'AiChat',
  components: {
    ElCard,
    ElInput,
    ElButton,
  },
  setup() {
    const userInput = ref('');
    const messages = ref([]);
    const messageContainer = ref(null);
    const isRequesting = ref(false);
    const lastRequestTime = ref(0);
    const REQUEST_COOLDOWN = 2000; // 设置2秒的冷却时间

    const renderMarkdown = (text) => {
      return marked(text);
    };

    const sendMessage = async () => {
      if (!userInput.value.trim() || isRequesting.value) return;

      // 检查是否在冷却时间内
      const now = Date.now();
      if (now - lastRequestTime.value < REQUEST_COOLDOWN) {
        ElMessage.warning('请求太频繁，请稍后再试');
        return;
      }

      const userMessage = { id: Date.now(), sender: 'user', text: userInput.value };
      messages.value.push(userMessage);

      const promptMessage = userInput.value;
      userInput.value = '';
      isRequesting.value = true;
      lastRequestTime.value = now;

      await scrollToBottom();

      const aiMessage = { id: Date.now(), sender: 'ai', text: '正在思考中...' };
      messages.value.push(aiMessage);

      try {
        const response = await axios.post(
          'https://open.bigmodel.cn/api/paas/v4/chat/completions',
          {
            model: 'glm-4-flash',
            messages: [
              { role: 'system', content: '你是一个校园二手交易平台的客服，帮助用户解答问题。' },
              { role: 'user', content: promptMessage },
            ],
            max_tokens: 1024,
            temperature: 0.95,
            top_p: 0.7,
            stream: false,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: 'f59c1436c8ce5694b042ec36a27e1763.vrINvFxVjPBM75i8',
            },
            timeout: 30000, // 设置30秒超时
          }
        );

        const fullText = response.data.choices[0].message.content;
        let currentText = '';
        const typingSpeed = 50;

        for (let i = 0; i < fullText.length; i++) {
          await new Promise((resolve) => setTimeout(resolve, typingSpeed));
          currentText += fullText[i];
          messages.value = messages.value.map((msg) =>
            msg.id === aiMessage.id ? { ...msg, text: currentText } : msg
          );
        }
      } catch (error) {
        console.error('请求失败：', error);
        let errorMessage = '抱歉，发生了一些错误。';
        
        if (error.response) {
          switch (error.response.status) {
            case 429:
              errorMessage = '请求频率过高，请稍后再试。';
              break;
            case 401:
              errorMessage = 'API密钥无效或已过期。';
              break;
            case 503:
              errorMessage = '服务暂时不可用，请稍后重试。';
              break;
            default:
              errorMessage = `服务器返回错误：${error.response.status}`;
          }
        } else if (error.code === 'ECONNABORTED') {
          errorMessage = '请求超时，请检查网络连接。';
        } else {
          errorMessage = '网络错误，请检查您的网络连接。';
        }

        ElMessage.error(errorMessage);
        messages.value = messages.value.map((msg) =>
          msg.id === aiMessage.id ? { ...msg, text: errorMessage } : msg
        );
      } finally {
        isRequesting.value = false;
        await scrollToBottom();
      }
    };

    const scrollToBottom = async () => {
      await nextTick();
      if (messageContainer.value) {
        const container = messageContainer.value.$el || messageContainer.value;
        container.scrollTop = container.scrollHeight;
      }
    };

    onMounted(() => {
      scrollToBottom();
    });

    return {
      userInput,
      messages,
      messageContainer,
      sendMessage,
      renderMarkdown, // 添加 Markdown 渲染方法
    };
  },
});
</script>
  
<style scoped>
.ai-chat-page {
  height: calc(100vh - 80px);
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.chat-container {
  width: 1000px;
  height: 100%;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部样式 */
.chat-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.model-info {
  font-size: 14px;
  color: #909399;
}

/* 消息区域样式 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f9fafc;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.welcome-message h2 {
  font-size: 24px;
  margin-bottom: 12px;
  color: #303133;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-wrapper {
  flex: 1;
  max-width: 80%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.message.user .message-wrapper {
  align-items: flex-end;
}

.message-sender {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
  padding: 0 4px;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  display: inline-block;
  word-break: break-word;
}

.message.user .message-content {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.ai .message-content {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 代码块样式 */
.message.ai .message-content :deep(pre) {
  background: #282c34;
  padding: 16px;
  border-radius: 8px;
  margin: 12px 0;
}

.message.ai .message-content :deep(code) {
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}

/* 底部区域样式 */
.chat-footer {
  padding: 20px;
  background: white;
  border-top: 1px solid #ebeef5;
}

.chat-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
}

.chat-input {
  display: flex;
  gap: 12px;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.send-button {
  align-self: flex-end;
  height: 40px;
  padding: 0 24px;
  font-size: 14px;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .chat-container {
    width: 90%;
  }
}

@media (max-width: 768px) {
  .ai-chat-page {
    padding: 10px;
  }
  
  .message-wrapper {
    max-width: 85%;
  }
}
</style>