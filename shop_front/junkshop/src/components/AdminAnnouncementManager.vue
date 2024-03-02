<template>
  <div class="announcement-editor">
    <textarea
      v-model="markdownInput"
      placeholder="输入公告内容(支持markdown格式)"
    ></textarea>
    <div></div>
    <button @click="submitAnnouncement">发布公告</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";

const markdownInput = ref("");

const submitAnnouncement = async () => {
  try {
    const res = await axios.put(
      "http://192.168.1.112:8080/changeContent?content=" + markdownInput.value
    );
    if (res.data === "success") {
      ElMessage.success("发布成功！");
    } else {
      ElMessage.error("发布失败！");
    }
  } catch (error) {
    console.error("获取公告失败", error);
  }
};
const getAnnouncement = async () => {
  try {
    const response = await axios.get("http://192.168.1.112:8080/getContent");
    // console.log(response.data);
    markdownInput.value = response.data;
  } catch (error) {
    console.error("获取公告失败", error);
  }
};
onMounted(getAnnouncement);
</script>

<style>
.announcement-editor textarea {
  width: 800px;
  height: 300px; /* 调整高度为 300px */
  margin-bottom: 20px;
}

.announcement-editor button {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #4caf50; /* 绿色背景 */
  color: white; /* 白色文本 */
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.announcement-editor button:hover {
  background-color: #45a049; /* 深绿色背景 */
}
</style>
