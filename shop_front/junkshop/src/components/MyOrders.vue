<template>
  <div class="orders-container">
    <div class="content-wrapper">
      <!-- 头部搜索区域 -->
      <div class="search-section">
        <h2 class="page-title">我的购买订单</h2>
        
        <div class="search-controls">
          <el-input
            v-model="searchQuery"
            placeholder="搜索商品名称..."
            class="search-input"
            clearable
            @clear="getUserInfo"
            @keyup.enter="getUserInfo"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

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
      </div>

      <!-- 订单列表区域 -->
      <div class="orders-list" v-if="orders.length > 0">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.orderID }}</span>
            <el-tag :class="['status-tag', order.orderStatus]">
              {{ order.orderStatus }}
            </el-tag>
          </div>
          
          <div class="order-content">
            <div class="product-info">
              <h3 class="product-name">{{ order.itemName }}</h3>
              <span class="price">¥{{ order.price }}</span>
            </div>
            
            <div class="order-dates">
              <div class="date-item">
                <span class="label">下单时间</span>
                <span class="value">{{ order.createdAt }}</span>
              </div>
              <div class="date-item">
                <span class="label">更新时间</span>
                <span class="value">{{ order.completedAt }}</span>
              </div>
            </div>
          </div>

          <div class="order-actions">
            <el-button 
              type="primary" 
              plain
              size="small"
              @click="viewDetails(order)"
            >
              <el-icon><ZoomIn /></el-icon>
              查看详情
            </el-button>
            <el-button
              v-if="order.orderStatus === '已取消'"
              type="danger"
              plain
              size="small"
              @click="deleteOrder(order.orderID)"
            >
              <el-icon><Delete /></el-icon>
              删除订单
            </el-button>
          </div>
        </div>
      </div>

      <el-empty 
        v-else 
        description="暂无订单数据"
        :image-size="200"
      />

      <!-- 分页器 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 20, 40, 60, 100]"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>

      <!-- 订单详情对话框 -->
      <el-dialog 
        v-model="detailsVisible" 
        title="订单详情"
        width="600px"
        class="order-dialog"
      >
        <div class="dialog-content">
          <div class="info-section">
            <h4 class="section-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单号</span>
                <span class="value">{{ orderSelected.orderID }}</span>
              </div>
              <div class="info-item">
                <span class="label">商品名称</span>
                <span class="value">{{ orderSelected.itemName }}</span>
              </div>
              <div class="info-item">
                <span class="label">价格</span>
                <span class="value price">¥{{ orderSelected.price }}</span>
              </div>
              <div class="info-item">
                <span class="label">订单状态</span>
                <el-tag>{{ orderSelected.orderStatus }}</el-tag>
              </div>
            </div>
          </div>

          <div class="info-section">
            <h4 class="section-title">收货信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">收件人</span>
                <span class="value">{{ orderSelected.recipientName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话</span>
                <span class="value">{{ orderSelected.phoneNumber }}</span>
              </div>
              <div class="info-item full-width">
                <span class="label">收货地址</span>
                <span class="value">{{ orderSelected.address }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button
              v-if="orderSelected.orderStatus === '已发货'"
              type="primary"
              @click="sellerChangeStatus(1)"
            >确认收货</el-button>
            <el-button
              v-if="orderSelected.orderStatus === '已支付' || orderSelected.orderStatus === '待发货'"
              type="warning"
              @click="sellerChangeStatus(2)"
            >申请退款</el-button>
            <el-button @click="detailsVisible = false">关闭</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

//购买订单数据
const orders = reactive([]);
const total = ref(0);
const user = reactive({});

const detailsVisible = ref(false);
const orderSelected = ref({});
const viewDetails = (value) => {
  orderSelected.value = value;
  console.log(orderSelected.value);
  detailsVisible.value = true;
};

const searchStatus = ref("全部");
const searchQuery = ref("");
const deleteOrder = async (orderId) => {
  ElMessageBox.confirm("确认删除此订单吗？删除后将无法恢复！！！", "Warning", {
    confirmButtonText: "确认删除",
    cancelButtonText: "再考虑一下",
    type: "warning",
  }).then(async () => {
    try {
      const res = await axios.delete(
        "http://localhost:8080/deleteOrderByID?orderID=" + orderId
      );
      if (res.data === "order deleted") {
        ElMessage.success("订单删除成功！");
        getOrders(user.value.userID);
      } else {
        ElMessage.error("订单删除失败！");
      }
    } catch (err) {
      ElMessage.error("删除请求出错！", err);
    }
  });
};

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getUserInfo();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getUserInfo();
};

const sellerChangeStatus = async (value) => {
  if (value === 1) {
    ElMessageBox.confirm(
      "确认收货吗？确认收货后将无法更改订��状态！！！",
      "Warning",
      {
        confirmButtonText: "确认收货",
        cancelButtonText: "再考虑一下",
        type: "warning",
      }
    ).then(async () => {
      orderSelected.value.orderStatus = "已完成";
      try {
        const res = await axios.put(
          "http://localhost:8080/updateOrderStatus",
          orderSelected.value
        );
        if (res.data === "status updated" && orderSelected.value.orderStatus === "已完成") {
          ElMessage.success("确认收货成功！");
          detailsVisible.value = false;
        } else {
          ElMessage.error("请求失败！");
        }
      } catch (err) {
        ElMessage.error("请求发送失败！", err);
      }
    });
  } else if (value === 2) {
    ElMessageBox.confirm(
      "确认申请退款吗？申请后需要等待卖家确认！",
      "Warning",
      {
        confirmButtonText: "确认申请",
        cancelButtonText: "再考虑一下",
        type: "warning",
      }
    ).then(async () => {
      orderSelected.value.orderStatus = "申请退款中";
      try {
        const res = await axios.put(
          "http://localhost:8080/updateOrderStatus",
          orderSelected.value
        );
        if (res.data === "status updated" && orderSelected.value.orderStatus === "申请退款中") {
          ElMessage.success("申请退款成功！");
          detailsVisible.value = false;
        } else {
          ElMessage.error("请求失败！");
        }
      } catch (err) {
        ElMessage.error("请求发送失败！", err);
      }
    });
  }
};

//获取用户信息
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
      "http://localhost:8080/getUserByUsername",
      {
        params: { username: userInfo.username },
      }
    );
    user.value = response.data;
    getOrders(user.value.userID);
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

const getOrders = async (id) => {
  try {
    const res = await axios.get("http://localhost:8080/getOrders", {
      params: {
        buyerID: id,
        itemName: searchQuery.value,
        status: searchStatus.value,
        size: pageSize.value,
        page: currentPage.value - 1,
      },
    });
    orders.splice(0, orders.length, ...res.data.orders);
    total.value = res.data.total;
  } catch (error) {
    ElMessage.error("获取订单列表失败!", error);
  }
};
onMounted(getUserInfo);

watch(searchStatus, getUserInfo);
</script>

<style scoped>
.orders-container {
  padding: 32px;
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
}

/* 新增容器来控制内容宽度 */
.content-wrapper {
  width: 1200px;
  max-width: 100%;
}

.search-section {
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.search-controls {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-input {
  width: 320px;
}

.status-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.orders-list {
  display: grid;
  gap: 16px;
  margin-bottom: 24px;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.order-id {
  font-size: 14px;
  color: #64748b;
}

.status-tag {
  font-weight: 500;
}

.status-tag.已支付 { --el-tag-bg-color: #ecfdf5; --el-tag-text-color: #059669; }
.status-tag.待发货 { --el-tag-bg-color: #fff7ed; --el-tag-text-color: #ea580c; }
.status-tag.已发货 { --el-tag-bg-color: #eff6ff; --el-tag-text-color: #3b82f6; }
.status-tag.已完成 { --el-tag-bg-color: #f0fdf4; --el-tag-text-color: #16a34a; }
.status-tag.申请退款中 { --el-tag-bg-color: #fef2f2; --el-tag-text-color: #dc2626; }
.status-tag.已取消 { --el-tag-bg-color: #f4f4f5; --el-tag-text-color: #71717a; }

.order-content {
  margin-bottom: 16px;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #1e293b;
  margin: 0;
}

.price {
  font-size: 18px;
  font-weight: 600;
  color: #059669;
}

.order-dates {
  display: flex;
  gap: 24px;
}

.date-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date-item .label {
  font-size: 12px;
  color: #64748b;
}

.date-item .value {
  font-size: 14px;
  color: #334155;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 对话框样式 */
.dialog-content {
  padding: 0 16px;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: span 2;
}

.info-item .label {
  font-size: 13px;
  color: #64748b;
}

.info-item .value {
  font-size: 14px;
  color: #1e293b;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-radio-button__inner) {
  border-radius: 6px;
  padding: 8px 16px;
}

:deep(.el-dialog) {
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

:deep(.el-dialog__body) {
  padding: 24px 0;
}

:deep(.el-dialog__footer) {
  padding: 0 24px 24px;
}

/* 响应式设计调整 */
@media (max-width: 1280px) {
  .content-wrapper {
    width: 1000px;
  }
}

@media (max-width: 1024px) {
  .content-wrapper {
    width: 100%;
  }
  
  .search-controls {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .orders-container {
    padding: 16px;
  }
  
  .order-dates {
    flex-direction: column;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item.full-width {
    grid-column: auto;
  }
}
</style>