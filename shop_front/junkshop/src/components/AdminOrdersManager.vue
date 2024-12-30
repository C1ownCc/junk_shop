<template>
  <div class="orders-management">
    <!-- 头部筛选区 -->
    <div class="header-section">
      <div class="filter-section">
        <el-radio-group v-model="searchStatus" class="status-filter">
          <el-radio-button label="全部" />
          <el-radio-button label="已支付" />
          <el-radio-button label="待发货" />
          <el-radio-button label="已发货" />
          <el-radio-button label="已完成" />
          <el-radio-button label="申请退款中" />
          <el-radio-button label="已取消" />
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

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table 
        :data="orders" 
        style="width: 100%"
        :header-cell-style="{ background: '#f8fafc', color: '#475569' }"
        border
      >
        <el-table-column prop="orderID" label="订单ID" width="100" />
        <el-table-column prop="buyerID" label="买家ID" width="100" />
        <el-table-column prop="sellerID" label="卖家ID" width="100" />
        <el-table-column prop="itemID" label="物品ID" width="100" />
        <el-table-column prop="itemName" label="商品名称" width="150" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.orderStatus)">
              {{ scope.row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recipientName" label="收件人昵称" width="120" />
        <el-table-column prop="address" label="订单地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phoneNumber" label="联系电话" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="completedAt" label="完成时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.completedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
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
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { watch, reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
const searchQuery = ref("");
const searchStatus = ref("全部");

const handleDelete = (id) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该商品吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      console.log(id);
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
};

const orders = reactive([]);
const total = ref(0);
const getOrders = async () => {
  // console.log(user);
  try {
    const res = await axios.get("http://localhost:8080/getOrders", {
      params: {
        itemName: searchQuery.value,
        status: searchStatus.value,
        size: pageSize.value,
        page: currentPage.value - 1,
      },
    });
    orders.splice(0, orders.length, ...res.data.orders);
    if (res.data.total > 0) {
      total.value = res.data.total;
    }
    console.log("res--------------", orders.values);
  } catch (error) {
    ElMessage.error("获取订单列表失败!", error);
  }
};
onMounted(getOrders);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getItems();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getItems();
};

watch(searchStatus, getOrders);

//搜索
const onSearch = async () => {};

const getStatusType = (status) => {
  const statusMap = {
    '已支付': 'success',
    '待发货': 'warning',
    '已发货': 'info',
    '已完成': 'success',
    '申请退款中': 'danger',
    '已取消': 'info'
  };
  return statusMap[status] || '';
};

// 添加时间格式化函数
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};
</script>

<style scoped>
.orders-management {
  padding: 24px;
}

.header-section {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-section {
  width: 100%;
  overflow-x: auto;
}

.status-filter {
  display: flex;
  gap: 8px;
}

:deep(.el-radio-button__inner) {
  padding: 8px 16px;
  border-radius: 6px;
}

.search-section {
  width: 100%;
  max-width: 400px;
}

.search-input {
  width: 100%;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

:deep(.el-table) {
  --el-table-border-color: #e2e8f0;
  --el-table-header-bg-color: #f8fafc;
}

.action-buttons {
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .orders-management {
    padding: 16px;
  }

  .status-filter {
    flex-wrap: wrap;
  }

  .search-section {
    max-width: 100%;
  }
}

/* 状态标签样式 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 8px;
}

/* 添加时间列的样式 */
:deep(.el-table .cell) {
  font-family: Consolas, Monaco, monospace;
  color: #475569;
}
</style>
