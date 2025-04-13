<template>
  <div class="itemDisplay">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品..."
        class="search-input"
        clearable
        @clear="getItems"
        @keyup.enter="getItems"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 分类筛选区域 -->
    <div class="filter-section">
      <div class="filter-group">
        <div class="filter-label">分类:</div>
        <el-radio-group v-model="category" class="filter-options">
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

      <div class="filter-group">
        <div class="filter-label">成色:</div>
        <el-radio-group v-model="condition" class="filter-options">
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

    <!-- 商品展示区域 -->
    <div class="products-section">
      <div class="waterfall-container">
        <div v-for="item in items.values" 
             :key="item.itemID" 
             class="waterfall-item"
             @click="chooseItem(item)">
          <div class="product-card">
            <div class="product-image">
              <img v-if="item.images[0].imageURL" 
                   :src="item.images[0].imageURL" 
                   alt="商品图片" />
              <el-empty v-else 
                       description="暂无图片" 
                       :image-size="50" />
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ item.name }}</h3>
              <p class="product-desc">{{ item.description }}</p>
              <div class="product-meta">
                <el-tag class="product-price">￥{{ item.price }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="totalItems === 0" class="empty-state">
        <el-empty description="暂无商品" />
      </div>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[20, 40, 60, 80, 100]"
        :total="totalItems"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
        background
        :hide-on-single-page="false"
      />
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from "element-plus";
import { ref, reactive, onMounted, watch } from "vue";
import axios from "axios";

const emits = defineEmits(["update-nav", "get-item-from-display"]);
// 商品列表数据
const category = ref("全部");
const condition = ref("全部");
const items = reactive({ values: [] }); // 初始化为包含 values 的对象
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
      "http://localhost:8080/userFindItemsByStatus",
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
    
    console.log("商品数据:", res.data); // 添加日志，查看返回的商品数据
    
    if (res.data && res.data.items) {
      items.values = res.data.items;
      if (res.data.total > 0) {
        totalItems.value = res.data.total;
      } else {
        totalItems.value = 0;
      }
    } else {
      items.values = [];
      totalItems.value = 0;
    }
  } catch (err) {
    console.error("请求出错！", err);
    ElMessage.error("获取商品数据失败");
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

//点击跳转情
const chooseItem = (data) => {
  emits("get-item-from-display", data);
  emits("update-nav", "6");
};

watch(category, getItems);
watch(condition, getItems);
onMounted(getItems);
</script>

<style scoped>
.itemDisplay {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索区域样式 */
.search-section {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.search-input {
  width: 400px;
  max-width: 90%;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  padding: 8px 12px;
}

:deep(.el-input__inner) {
  height: 40px;
  font-size: 14px;
}

:deep(.el-input__prefix) {
  margin-right: 8px;
  color: #94a3b8;
}

/* 分类筛选区域样式 */
.filter-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-group {
  margin-bottom: 16px;
}

.filter-group:last-child {
  margin-bottom: 0;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  margin-bottom: 12px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-radio-button__inner) {
  border-radius: 6px !important;
  padding: 8px 16px;
  height: auto;
  line-height: 1.4;
  font-size: 13px;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 6px !important;
}

:deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 6px !important;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #3b82f6;
  border-color: #3b82f6;
  box-shadow: -1px 0 0 0 #3b82f6;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .filter-section {
    padding: 16px;
  }

  .filter-options {
    gap: 6px;
  }

  :deep(.el-radio-button__inner) {
    padding: 6px 12px;
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .search-input {
    width: 90%;
  }

  .filter-section {
    padding: 12px;
  }
}

/* 商品展示区域 */
.products-section {
  margin-top: 20px;
  padding: 16px;
  background: white;
  border-radius: 12px;
}

.waterfall-container {
  column-count: 5;
  column-gap: 8px;
  width: 100%;
}

.waterfall-item {
  break-inside: avoid;
  margin-bottom: 8px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  display: block;
}

.product-image img {
  width: 100%;
  height: auto; /* 让图片保持原始比例 */
  display: block;
}

.product-info {
  padding: 8px;
}

.product-title {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 500;
}

.product-desc {
  font-size: 12px;
  color: #666;
  margin: 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  margin-top: 8px;
}

.product-price {
  margin-right: 8px;
}

/* 响应式布局 */
@media (max-width: 1400px) {
  .waterfall-container { column-count: 4; }
}

@media (max-width: 1200px) {
  .waterfall-container { column-count: 3; }
}

@media (max-width: 900px) {
  .waterfall-container { column-count: 2; }
}

@media (max-width: 600px) {
  .waterfall-container { column-count: 1; }
}

/* 空状态样式 */
.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 分页样式保持不变 */
.pagination-wrapper {
  margin-top: 24px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

/* 自定义分页样式 */
:deep(.el-pagination) {
  --el-pagination-bg-color: #f8fafc;
  --el-pagination-hover-color: #3b82f6;
  --el-pagination-button-color: #64748b;
  --el-pagination-button-bg-color: #fff;
  --el-pagination-button-disabled-color: #cbd5e1;
  --el-pagination-button-disabled-bg-color: #f1f5f9;
  --el-pagination-border-radius: 8px;
  
  padding: 8px 16px;
  font-weight: 500;
}

:deep(.el-pagination .el-select .el-input) {
  width: 120px;
}

:deep(.el-pagination .el-pagination__total) {
  margin-right: 16px;
}

:deep(.el-pagination .el-pagination__sizes) {
  margin-right: 16px;
}

:deep(.el-pagination button) {
  min-width: 32px;
  height: 32px;
  border-radius: 6px;
}

:deep(.el-pagination .el-pager li) {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  border-radius: 6px;
  margin: 0 4px;
  font-weight: 500;
}

:deep(.el-pagination .el-pager li.active) {
  background-color: var(--el-pagination-hover-color);
  color: white;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .pagination-wrapper {
    padding: 12px 8px;
  }
  
  :deep(.el-pagination) {
    padding: 4px 8px;
  }
}
</style>
