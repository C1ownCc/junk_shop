<template>
  <div class="itemDisplay">
    <div class="search-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品..."
        class="search-input"
        clearable
        @clear="getItems"
        @keyup.enter="getItems"
      >
        <template #suffix>
          <el-icon class="search-icon" size="150%" @click="getItems">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>
    <div class="radio-selection">
      <div class="radio-selection-in">
        <p>分类:</p>
        <el-radio-group v-model="category" class="elRadio">
          <el-radio-button label="全部" />
          <el-radio-button label="服饰鞋帽" />
          <el-radio-button label="家居用品" />
          <el-radio-button label="电子数码" />
          <el-radio-button label="美妆护肤" />
          <el-radio-button label="食品生鲜" />
          <el-radio-button label="图书音像" />
          <el-radio-button label="儿童玩具" />
          <el-radio-button label="运动户外" />
          <el-radio-button label="汽车用品" />
          <el-radio-button label="医疗保健" />
          <el-radio-button label="工艺礼品" />
          <el-radio-button label="虚拟物品" />
        </el-radio-group>
      </div>

      <div class="radio-selection-in">
        <p>成色:</p>
        <el-radio-group v-model="condition" class="elRadio">
          <el-radio-button label="全部" />
          <el-radio-button label="全新" />
          <el-radio-button label="99新" />
          <el-radio-button label="95新" />
          <el-radio-button label="9成新" />
          <el-radio-button label="8成新" />
          <el-radio-button label="6成新" />
        </el-radio-group>
      </div>
    </div>

    <div class="masonry">
      <div
        v-for="item in items.values"
        :key="item.itemID"
        class="masonry-item"
        @click="activeIndex = '6'"
        @select="handleMenuSelect"
      >
        <!-- 动态内容或 <ItemDisplay> 组件 -->
        <el-card class="item-display" shadow="always" @click="chooseItem(item)">
          <div class="image-container" v-if="item.images[0].imageURL != null">
            <img :src="item.images[0].imageURL" alt="商品图片" />
          </div>
          <div class="image-container" v-else>
            <el-empty description="此商品暂无图片" :image-size="50" />
          </div>
          <div class="info-container">
            <h3>{{ item.name }}</h3>
            <p>
              <el-text truncated>{{ item.description }}</el-text>
            </p>
            <el-tag>￥{{ item.price }}</el-tag>
          </div>
        </el-card>
      </div>
    </div>
    <div v-if="totalItems === 0">
      <el-empty description="暂无商品" />
    </div>
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 40, 60, 80, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalItems"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from "element-plus";
import { ref, reactive, defineEmits, onMounted, watch } from "vue";
import axios from "axios";
const emit = defineEmits(["updateNav"]);

// 商品列表数据
const category = ref("全部");
const condition = ref("全部");
const items = reactive([]);
const totalItems = ref(0);
//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(20);
//搜索栏数据
const searchQuery = ref("");
const getItems = async () => {
  try {
    const res = await axios.get(
      "http://192.168.1.112:8080/userFindItemsByStatus",
      {
        params: {
          status: "已上架",
          name: searchQuery.value,
          category: category.value,
          condition: condition.value,
          page: currentPage.value - 1,
          size: pageSize.value,
        },
      }
    );
    items.values = res.data.items;
    if (res.data.total > 0) {
      totalItems.value = res.data.total;
    } else {
      totalItems.value = 0;
    }
  } catch (err) {
    ElMessage.error("请求出错！", err);
  }
};

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getItems();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getItems();
};

//点击跳转详情
const chooseItem = (data) => {
  emit("updateNav", "6");
  emit("getItemFromDisplay", data);
};

watch(category, getItems);
watch(condition, getItems);
onMounted(getItems);
</script>

<style scoped>
.item-display {
  border-radius: 10px;
  box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3); /* 保持原有阴影 */
  overflow: hidden;
  background-color: white;
  width: 200px; /* 固定宽度 */
  height: auto; /* 高度自适应内容 */
  border-bottom: 4px solid #dcdcdc; /* 加粗的底部边框，颜色可以根据实际效果调整 */
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
}

.image-container img {
  width: 100%;
  height: auto;
  display: block;
}

.info-container {
  padding: 10px;
  text-align: center; /* 信息容器中的文本居中对齐 */
}

.info-container h3 {
  margin: 0;
  color: #333;
  font-weight: 500; /* 字体稍微加粗 */
}

.info-container p {
  margin: 5px 0;
  color: #666;
}

.radio-selection {
  display: flex;
  flex-direction: column;
}

.radio-selection-in {
  display: flex;
}

.elRadio {
  margin: 5px;
}

.item-display:hover {
  transform: translateY(-5px); /* 鼠标悬停时轻微上移 */
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2); /* 鼠标悬停时阴影更加深入 */
  border-bottom: 4px solid #bcbcbc; /* 鼠标悬停时底部边框颜色加深 */
}

.search-container {
  margin: 20px 0; /* Add some vertical spacing */
  display: flex;
  justify-content: center;
}

.search-input {
  overflow: hidden;
  width: 100%; /* Full width */
  max-width: 800px; /* Maximum width, can adjust as needed */
  height: 50px; /* Taller input field */
  border-radius: 20px; /* Rounded corners like the item cards */
  box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3); /* Shadow effect like item cards */
  transition: box-shadow 0.3s; /* Smooth transition for shadow effect */
}

.search-input .el-input__inner {
  border-radius: 20px; /* Rounded corners for the inner input */
  height: 50px; /* Match outer height for alignment */
  line-height: 50px; /* Center the text vertically */
  padding-left: 20px; /* Spacious padding on the left */
  font-size: 18px; /* Larger font size for better readability */
}

.search-input .el-input__icon {
  line-height: 50px; /* Center the icon vertically */
}

.search-input:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2); /* Deeper shadow on hover */
}

.search-input .el-button {
  border-radius: 0 10px 10px 0; /* Rounded corners on the right side */
}

.search-icon {
  cursor: pointer;
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.masonry {
  column-count: 5; /* 默认列数 */
  column-gap: 30px; /* 列间距 */
}

@media (max-width: 1100px) {
  .masonry {
    column-count: 3; /* 屏幕宽度在 1100px 以下时的列数 */
  }
}

@media (max-width: 700px) {
  .masonry {
    column-count: 2; /* 屏幕宽度在 700px 以下时的列数 */
  }
}

@media (max-width: 500px) {
  .masonry {
    column-count: 1; /* 屏幕宽度在 500px 以下时的列数 */
  }
}

.masonry-item {
  break-inside: avoid-column; /* 防止列中间断开 */
  margin-bottom: 30px; /* 设置项目之间的垂直间距 */
}

.itemDisplay {
  min-width: 800px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 根据需要添加一些顶部外边距 */
}
</style>
