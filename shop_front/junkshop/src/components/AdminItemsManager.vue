<template>
  <div class="admin-container">
    <div class="content-wrapper">
      <!-- 头部筛选区 -->
      <div class="header-section">
        <div class="filter-section">
          <el-radio-group v-model="searchStatus" class="status-filter">
            <el-radio-button label="全部" />
            <el-radio-button label="待审核" />
            <el-radio-button label="审核通过" />
            <el-radio-button label="审核不通过" />
            <el-radio-button label="已上架" />
            <el-radio-button label="已售出" />
            <el-radio-button label="下架" />
          </el-radio-group>
        </div>
        
        <div class="search-section">
          <el-input
            v-model="searchQuery"
            placeholder="请输入商品名以搜索商品"
            class="search-input"
            clearable
            @clear="onSearch"
            @keyup.enter="onSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div class="batch-actions" v-if="selectedItems.length > 0">
        <el-alert
          title="已选择商品"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <span>已选择 {{ selectedItems.length }} 个商品</span>
            <div class="batch-buttons">
              <el-button type="success" size="small" @click="batchUpdateStatus('approved')">批量批准</el-button>
              <el-button type="danger" size="small" @click="batchUpdateStatus('rejected')">批量拒绝</el-button>
              <el-button type="info" size="small" @click="clearSelection">取消选择</el-button>
            </div>
          </template>
        </el-alert>
      </div>

      <!-- 表格区域 -->
      <div class="table-container">
        <el-table 
          :data="items" 
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#475569' }"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column prop="itemID" label="物品ID" width="80" />
          <el-table-column prop="sellerID" label="商家ID" width="80" />
          <el-table-column prop="name" label="名称" width="150" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="category" label="类别" width="100" />
          <el-table-column prop="condition" label="成色" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  link
                  @click="handleReview(scope.row)"
                >
                  <el-icon><ZoomIn /></el-icon>
                </el-button>
                <el-button 
                  type="danger" 
                  link
                  @click="handleDelete(scope.row.itemID)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页器 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 20, 40, 60, 100]"
          :total="totalItems"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>

      <!-- 商品图片对话框 -->
      <el-dialog 
        v-model="dialogFormVisible" 
        title="商品图片"
        width="600px"
        class="review-dialog"
      >
        <div class="images-container">
          <div v-if="itemImages.length > 0" class="images-grid">
            <div v-for="image in itemImages" :key="image.imageID" class="image-item">
              <el-image 
                :src="image.imageURL"
                fit="cover"
                :preview-src-list="[image.imageURL]"
              />
            </div>
          </div>
          <el-empty v-else description="此商品未上传相应图片" />
        </div>

        <div class="review-actions">
          <el-radio-group v-model="changeStatus" size="large" class="status-options">
            <el-radio-button label="待审核" />
            <el-radio-button label="审核通过" />
            <el-radio-button label="审核不通过" />
            <el-radio-button label="已上架" />
            <el-radio-button label="已售出" />
            <el-radio-button label="下架" />
          </el-radio-group>
          
          <div class="dialog-footer">
            <el-button @click="changeStatusClose">取 消</el-button>
            <el-button type="primary" @click="submitChangeStatus">
              提交审核结果
            </el-button>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
//总数
const totalItems = ref(0);
//商品列表
const items = reactive([{}]);
const dialogFormVisible = ref(false);
const itemImages = ref([]);
const changeStatus = ref("");
const itemIDWithStatus = ref("");
const searchQuery = ref("");
const searchStatus = ref("全部");
const selectedItems = ref([]);

const handleReview = async (item) => {
  itemIDWithStatus.value = item.itemID;
  dialogFormVisible.value = true;
  try {
    const res = await axios.get(
      "http://localhost:8080/getItemImagesByItemID",
      {
        params: {
          itemID: item.itemID,
        },
      }
    );
    itemImages.value = res.data;
  } catch (err) {
    ElMessage.error("请求错误！", err);
  }
};

const handleDelete = (itemId) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该商品吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await axios.delete(
          "http://localhost:8080/deleteItemById?id=" + itemId
        );
        if (response.data === "item deleted") {
          // 删除成功后从 users 中移除该用户
          getItems();
          ElMessage.success("商品删除成功！");
        } else if (response.data === "item delete error") {
          // 处理后端返回的删除失败情况
          ElMessage.error("商品删除失败！");
        } else {
          ElMessage.error("请求错误！");
        }
      } catch (error) {
        ElMessage.error("请求错误:", error);
        console.error("请求错误:", error);
        // 可以显示一些错误信息
      }
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
  // 删除商品逻辑
};

// 获取所有用户请求（带分页）
const getItems = async () => {
  try {
    const response = await axios.get(
      "http://localhost:8080/adminGetAllItems",
      {
        params: {
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );
    items.splice(0, items.length, ...response.data.items); // 更新用户列表
    // console.log(response.data);
    totalItems.value = response.data.total; // 更新总用户数
  } catch (error) {
    console.error("Error fetching users:", error);
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

//搜索
const onSearch = async () => {
  console.log("搜索....", searchQuery.value);

  try {
    const response = await axios.get(
      "http://localhost:8080/findItemsByName",
      {
        params: {
          name: searchQuery.value,
          status: searchStatus.value,
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );
    items.splice(0, items.length, ...response.data.items); // 更新用户列表
    // console.log(response.data);
    totalItems.value = response.data.total; // 更新总用户数
  } catch (err) {
    console.log("查询用户错误！", err);
  }
};

watch(searchStatus, onSearch);

const changeStatusClose = () => {
  itemIDWithStatus.value = "";
  dialogFormVisible.value = false;
  changeStatus.value = "";
};

const submitChangeStatus = async () => {
  if (
    changeStatus.value === "" ||
    changeStatus.value === null ||
    itemIDWithStatus === "" ||
    itemIDWithStatus === null
  ) {
    ElMessage.error("请选择审核状态！");
    return;
  }
  try {
    const res = await axios.put("http://localhost:8080/adminUpdateStatus", {
      status: changeStatus.value,
      itemID: itemIDWithStatus.value,
    });
    if (res.data === "status updated") {
      ElMessage.success("审核状态已更新！");
      changeStatusClose();
      getItems();
    } else {
      ElMessage.error("审核状态更新失败！");
    }
  } catch (err) {
    ElMessage.error("请求错误！", err);
  }
};

//组件加载时调用
onMounted(getItems);

// 新增状态标签颜色判断函数
const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'info',
    '审核通过': 'success',
    '审核不通过': 'danger',
    '已上架': 'primary',
    '已售出': 'warning',
    '下架': ''
  };
  return statusMap[status] || '';
};

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection;
};

// 清除选择
const clearSelection = () => {
  selectedItems.value = [];
};

// 批量更新状态
const batchUpdateStatus = async (status) => {
  try {
    loading.value = true;
    const itemIds = selectedItems.value.map(item => item.itemID);
    
    const responses = await Promise.all(
      itemIds.map(itemId => 
        axios.put(`http://localhost:8080/adminUpdateStatus`, { status, itemID: itemId })
      )
    );
    
    // 检查所有请求是否成功
    const allSuccess = responses.every(resp => resp.status === 200);
    
    if (allSuccess) {
      ElMessage.success(`已成功更新${itemIds.length}个商品的状态`);
      getItems();
      clearSelection();
    } else {
      ElMessage.warning('部分商品状态更新失败，请刷新页面查看最新状态');
    }
  } catch (error) {
    console.error('批量更新商品状态失败:', error);
    ElMessage.error('批量更新商品状态失败，请重试');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  padding: 24px;
  background-color: #f8fafc;
}

.content-wrapper {
  max-width: 1600px;
  margin: 0 auto;
}

.header-section {
  margin-bottom: 24px;
}

.filter-section {
  margin-bottom: 16px;
  overflow-x: auto;
}

.status-filter {
  display: flex;
  gap: 8px;
}

.search-section {
  max-width: 400px;
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 图片查看对话框样式 */
.review-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.images-container {
  padding: 24px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.image-item {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.image-item :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.review-actions {
  padding: 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.status-options {
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
  }
  
  .search-section {
    max-width: 100%;
  }
  
  .status-filter {
    flex-wrap: wrap;
  }
}

/* 批量操作栏样式 */
.batch-actions {
  margin-bottom: 16px;
}

.batch-buttons {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}
</style>
