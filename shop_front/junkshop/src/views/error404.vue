<template>
  <div class="error-page">
    <img
      src="../assets/imgs/error404.png"
      alt="Error Page Image"
      class="error-image"
    />
    <h1>404</h1>
    <p>
      抱歉/(ㄒoㄒ)/~~您要找的页面不存在或已经被外星人带走了，请点击下方按钮返回主页(●ˇ∀ˇ●)
    </p>
    <el-icon :size="30" class="home_icon"
      ><HomeFilled @click="goHome"
    /></el-icon>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goHome() {
  router.push("/");
}
</script>

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 0 20px;
}

.error-image {
  max-width: 30%;
  height: auto;
}

h1 {
  font-size: 5em;
  color: #555;
}

p {
  font-size: 1.5em;
  /* margin-bottom: 5px; */
}

.home_icon {
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.home_icon:hover {
  color: #409eff; /* 鼠标悬停时的颜色，根据需要调整 */
}
</style>
