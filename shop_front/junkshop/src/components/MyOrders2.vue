<template>
  <div class="orders-container">
    <div class="content-wrapper">
      <!-- 头部搜索区域 -->
      <div class="search-section">
        <h2 class="page-title">我的出售订单</h2>
        
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
                <span class="value">{{ formatDate(order.createdAt) }}</span>
              </div>
              <div class="date-item">
                <span class="label">更新时间</span>
                <span class="value">{{ formatDate(order.completedAt) }}</span>
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
        width="700px"
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
                <span class="label">订单状态</span>
                <el-tag :type="getStatusType(orderSelected.orderStatus)">{{ orderSelected.orderStatus }}</el-tag>
              </div>
              <div class="info-item">
                <span class="label">下单时间</span>
                <span class="value">{{ orderSelected.createdAt }}</span>
              </div>
              <div class="info-item">
                <span class="label">总价</span>
                <span class="value price">¥{{ orderSelected.price }}</span>
              </div>
<<<<<<< HEAD
              <div class="info-item">
                <span class="label">订单状态</span>
                <el-tag>{{ orderSelected.orderStatus }}</el-tag>
              </div>
              <div class="info-item">
                <span class="label">下单时间</span>
                <span class="value">{{ formatDate(orderSelected.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">更新时间</span>
                <span class="value">{{ formatDate(orderSelected.completedAt) }}</span>
              </div>
=======
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
            </div>
          </div>

          <!-- 商品详情信息 -->
          <div class="info-section">
            <h4 class="section-title">商品信息</h4>
            <el-table :data="orderDetails" style="width: 100%" border>
              <el-table-column label="商品图片" width="100" align="center">
                <template #default="{ row }">
                  <div class="product-image">
                    <img :src="row.itemImage" v-if="row.itemImage" alt="商品图片" />
                    <el-empty v-else description="暂无图片" :image-size="40" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="itemName" label="商品名称" min-width="180" />
              <el-table-column prop="itemPrice" label="单价" width="100">
                <template #default="{ row }">
                  <span class="item-price">¥{{ row.itemPrice }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="80" align="center" />
              <el-table-column label="小计" width="120">
                <template #default="{ row }">
                  <span class="item-subtotal">¥{{ (row.itemPrice * row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
            </el-table>
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
              type="primary"
              v-if="changeStatus === '已支付'"
              @click="sellerChangeStatus(1)"
            >接单</el-button>
            <el-button
              type="primary"
              v-else-if="changeStatus === '待发货'"
              @click="sellerChangeStatus(2)"
            >发货</el-button>
            <el-button
              type="primary"
              v-else-if="changeStatus === '申请退款中'"
              @click="sellerChangeStatus(3)"
            >同意退款</el-button>
            <el-button
              type="primary"
              v-if="changeStatus === '申请退款中'"
              @click="sellerChangeStatus(1)"
            >拒绝退款</el-button>
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
const searchStatus = ref("全部");
const searchQuery = ref("");

const detailsVisible = ref(false);
const orderSelected = ref({});
const changeStatus = ref("");
const orderDetails = ref([]);

const sellerChangeStatus = async (value) => {
  if (value === 1) {
    orderSelected.value.orderStatus = "待发货";
    changeStatus.value = "待发货";
  } else if (value === 2) {
    orderSelected.value.orderStatus = "已发货";
    changeStatus.value = "已发货";
  } else if (value === 3) {
    // 卖家同意退款，订单状态变为已取消
    ElMessageBox.confirm(
      "确认同意退款吗？同意后订单将被取消，资金将返还给买家，且商品库存将恢复！",
      "退款确认",
      {
        confirmButtonText: "确认退款",
        cancelButtonText: "取消",
        type: "warning",
      }
    ).then(async () => {
      orderSelected.value.orderStatus = "已取消";
      changeStatus.value = "已取消";
      console.log("准备提交退款请求：", orderSelected.value);
      
      try {
        const res = await axios.put(
          "http://localhost:8080/updateOrderStatus",
          orderSelected.value
        );
        console.log("退款请求返回结果：", res.data);
        
        if (res.data === "status updated") {
          ElMessage.success("退款成功！资金已返还给买家，商品库存已恢复。");
          detailsVisible.value = false;
          // 刷新订单列表
          getOrders(user.value.userID);
        } else {
          ElMessage.error("退款失败：" + res.data);
        }
      } catch (err) {
        console.error("退款请求错误:", err);
        ElMessage.error("退款请求发送失败！");
      }
    }).catch(() => {
      ElMessage.info("已取消退款操作");
    });
    return; // 添加return，防止执行后续代码
  }
  
  // 其他状态更新处理
  try {
    console.log("准备提交状态更新请求：", orderSelected.value);
    const res = await axios.put(
      "http://localhost:8080/updateOrderStatus",
      orderSelected.value
    );
    console.log("状态更新请求返回结果：", res.data);
    
    if (res.data === "status updated") {
      ElMessage.success("订单状态更新成功");
      getOrders(user.value.userID);
    } else {
      ElMessage.error("订单状态更新失败!");
    }
  } catch (err) {
    console.error("订单状态更新请求错误:", err);
    ElMessage.error("订单状态更新请求出错!");
  }
};

const viewDetails = async (value) => {
  orderSelected.value = value;
  changeStatus.value = value.orderStatus;
  detailsVisible.value = true;
  
  // 获取订单详情
  try {
    const res = await axios.get(`http://localhost:8080/orderDetails/${value.orderID}`);
    console.log("获取订单详情API响应:", res);
    
    if (res.data && res.data.length > 0) {
      orderDetails.value = res.data;
    } else {
      console.log("订单详情为空，创建默认订单详情");
      
      // 获取完整的商品信息，包括图片
      try {
        const itemRes = await axios.get(`http://localhost:8080/getItemById`, {
          params: { id: value.itemID }
        });
        console.log("获取商品详情:", itemRes.data);
        
        // 当订单详情为空时，创建默认详情项，使用商品的图片
        orderDetails.value = [{
          itemID: value.itemID,
          orderID: value.orderID,
          itemName: value.itemName,
          itemPrice: value.price,
          quantity: 1,
          itemImage: itemRes.data && itemRes.data.images && itemRes.data.images.length > 0 
            ? itemRes.data.images[0].imageURL 
            : ''
        }];
      } catch (itemError) {
        console.error("获取商品详情失败:", itemError);
        // 使用默认值创建订单详情
        orderDetails.value = [{
          itemID: value.itemID,
          orderID: value.orderID,
          itemName: value.itemName,
          itemPrice: value.price,
          quantity: 1,
          itemImage: value.itemImage || ''
        }];
      }
    }
  } catch (error) {
    console.error("获取订单详情失败:", error);
    ElMessage.error("获取订单详情失败");
    
    // 创建默认订单详情作为后备
    orderDetails.value = [{
      itemID: value.itemID,
      orderID: value.orderID,
      itemName: value.itemName,
      itemPrice: value.price,
      quantity: 1, 
      itemImage: value.itemImage || ''
    }];
  }
};

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

onMounted(getUserInfo);

const getOrders = async (id) => {
  // console.log(user);
  try {
    const res = await axios.get("http://localhost:8080/getOrders", {
      params: {
        sellerID: id,
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

watch(searchStatus, getUserInfo);

<<<<<<< HEAD
// 格式化日期显示
const formatDate = (dateStr) => {
  if (!dateStr) return '暂无数据';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '日期格式错误';
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error('日期格式化错误', error);
    return '日期格式错误';
  }
=======
// 获取订单状态对应的类型
const getStatusType = (status) => {
  const statusMap = {
    '已支付': 'success',
    '待发货': 'warning',
    '已发货': 'info',
    '已完成': 'success',
    '申请退款中': 'danger',
    '已取消': 'info',
    '待支付': 'warning'
  };
  return statusMap[status] || 'info';
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
};
</script>

<style scoped>
.orders-container {
  padding: 32px;
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
}

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
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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

/* 响应式设计 */
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

/* 订单详情对话框样式 */
.order-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.dialog-content {
  padding: 20px;
}

.info-section {
  margin-bottom: 24px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  padding: 12px 16px;
  background-color: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 16px;
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
  font-size: 14px;
  color: #64748b;
}

.info-item .value {
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
}

.info-item .price {
  color: #ef4444;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

/* 商品图片样式 */
.product-image {
  width: 60px;
  height: 60px;
  overflow: hidden;
  border-radius: 4px;
  margin: 0 auto;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-price {
  color: #64748b;
  font-weight: 500;
}

.item-subtotal {
  color: #ef4444;
  font-weight: 600;
}

/* 调整表格样式 */
:deep(.el-table) {
  --el-table-border-color: #e5e7eb;
  --el-table-header-bg-color: #f8fafc;
  margin: 0;
}

:deep(.el-table th) {
  font-weight: 600;
  color: #475569;
}

:deep(.el-table--border) {
  border: none;
  border-radius: 0;
}

:deep(.el-table__inner-wrapper) {
  border-bottom: none;
}
</style>