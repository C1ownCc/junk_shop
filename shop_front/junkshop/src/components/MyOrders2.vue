<template>
  <div>
    <h2 class="column-title">我的出售订单</h2>
    <div class="manager_head">
      <el-radio-group v-model="searchStatus">
        <el-radio-button label="全部" />
        <el-radio-button label="已支付" />
        <el-radio-button label="待发货" />
        <el-radio-button label="已发货" />
        <el-radio-button label="已完成" />
        <el-radio-button label="申请退款中" />
        <el-radio-button label="已取消" />
      </el-radio-group>
      <el-input
        v-model="searchQuery"
        placeholder="请输入商品名以搜索商品"
        class="search-input"
        clearable
        @clear="getUserInfo"
        @keyup.enter="getUserInfo"
      >
        <template #suffix>
          <el-icon class="search-icon" size="150%" @click="getUserInfo">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>
    <div class="orders-header">
      <el-row class="orders-row" :gutter="20">
        <el-col :span="3"><div class="grid-content">订单号</div></el-col>
        <el-col :span="3"><div class="grid-content">商品名</div></el-col>
        <el-col :span="3"><div class="grid-content">单价</div></el-col>
        <el-col :span="3"><div class="grid-content">下单日期</div></el-col>
        <el-col :span="3"><div class="grid-content">更新日期</div></el-col>
        <el-col :span="3"><div class="grid-content">订单状态</div></el-col>
        <el-col :span="3"><div class="grid-content">操作</div></el-col>
        <!-- 如果有更多列，继续添加 -->
      </el-row>
    </div>
    <div class="order-card" v-for="order in orders" :key="order.id">
      <div class="order-info">
        <div class="order-id">{{ order.orderID }}</div>
        <div class="product-name">{{ order.itemName }}</div>
        <div class="product-price">￥{{ order.price }}</div>
        <div class="order-date">{{ order.createdAt }}</div>
        <div class="order-date">{{ order.completedAt }}</div>
        <div class="order-status">
          <el-tag>{{ order.orderStatus }}</el-tag>
        </div>
      </div>
      <div class="order-actions">
        <el-icon class="icons" style="padding-right: 5px"
          ><ZoomIn @click="viewDetails(order)"
        /></el-icon>
        <el-icon
          class="icons"
          style="padding-left: 5px"
          v-if="order.orderStatus === '已取消'"
          ><Delete @click="deleteOrder(order.orderID)"
        /></el-icon>
      </div>
    </div>
    <div v-if="total === 0">
      <el-empty description="description" />
    </div>
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[6, 12, 20, 40, 60, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </div>
    <el-dialog title="订单详情" v-model="detailsVisible">
      <el-form :model="orderSelected">
        <el-form-item label="订单号" label-width="80px" prop="orderID">
          <el-text tag="b">{{ orderSelected.orderID }}</el-text>
        </el-form-item>
        <el-form-item label="商品名" label-width="80px" prop="itemName">
          <el-text tag="b">{{ orderSelected.itemName }}</el-text>
        </el-form-item>
        <el-form-item label="价格" label-width="80px" prop="price">
          <el-text tag="b">{{ orderSelected.price }}</el-text>
        </el-form-item>
        <el-form-item label="创建日期" label-width="80px" prop="createdAt">
          <el-text tag="b">{{ orderSelected.createdAt }}</el-text>
        </el-form-item>
        <el-form-item label="更新日期" label-width="80px" prop="completedAt">
          <el-text tag="b">{{ orderSelected.completedAt }}</el-text>
        </el-form-item>
        <el-form-item
          label="收件人姓名"
          label-width="80px"
          prop="recipientName"
        >
          <el-text tag="b">{{ orderSelected.recipientName }}</el-text>
        </el-form-item>
        <el-form-item label="收件人地址" label-width="80px" prop="address">
          <el-text tag="b">{{ orderSelected.address }}</el-text>
        </el-form-item>
        <el-form-item
          label="收件人联系电话"
          label-width="80px"
          prop="phoneNumber"
        >
          <el-text tag="b">{{ orderSelected.phoneNumber }}</el-text>
        </el-form-item>
        <el-form-item label="订单状态" label-width="80px" prop="price">
          <el-text tag="b">{{ orderSelected.orderStatus }}</el-text>
        </el-form-item>
      </el-form>
      <div class="handles">
        <el-button
          type="primary"
          v-if="changeStatus === '已支付'"
          @click="sellerChangeStatus(1)"
          >接单</el-button
        >
        <el-button
          type="primary"
          v-else-if="changeStatus === '待发货'"
          @click="sellerChangeStatus(2)"
          >发货</el-button
        >
        <el-button
          type="primary"
          v-else-if="changeStatus === '申请退款中'"
          @click="sellerChangeStatus(3)"
          >同意退款</el-button
        >
        <el-button
          type="primary"
          v-if="changeStatus === '申请退款中'"
          @click="sellerChangeStatus(1)"
          >拒绝退款</el-button
        >
        <!-- <el-radio-group v-model="changeStatus">
          <el-radio-button label="已支付" />
          <el-radio-button label="待发货" />
          <el-radio-button label="已发货" />
          <el-radio-button label="已完成" />
          <el-radio-button label="申请退款中" />
          <el-radio-button label="已取消" />
        </el-radio-group> -->
        <el-button @click="detailsVisible = false">关闭</el-button>
      </div>
    </el-dialog>
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
const sellerChangeStatus = async (value) => {
  if (value === 1) {
    orderSelected.value.orderStatus = "待发货";
    changeStatus.value = "待发货";
  } else if (value === 2) {
    orderSelected.value.orderStatus = "已发货";
    changeStatus.value = "已发货";
  } else if (value === 3) {
    orderSelected.value.orderStatus = "已取消";
    changeStatus.value = "已取消";
  }
  try {
    const res = await axios.put(
      "http://192.168.1.112:8080/updateOrderStatus",
      orderSelected.value
    );
    if (res.data === "status updated") {
      ElMessage.success("订单状态更新成功");
      getOrders(user.value.userID);
    } else {
      ElMessage.error("订单状态更新失败!");
    }
  } catch (err) {
    ElMessage.error("订单状态更新请求错误!", err);
  }
};

const viewDetails = (value) => {
  orderSelected.value = value;
  changeStatus.value = value.orderStatus;
  detailsVisible.value = true;
};

const deleteOrder = async (orderId) => {
  ElMessageBox.confirm("确认删除此订单吗？删除后将无法恢复！！！", "Warning", {
    confirmButtonText: "确认删除",
    cancelButtonText: "再考虑一下",
    type: "warning",
  }).then(async () => {
    try {
      const res = await axios.delete(
        "http://192.168.1.112:8080/deleteOrderByID?orderID=" + orderId
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
      "http://192.168.1.112:8080/getUserByUsername",
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
    const res = await axios.get("http://192.168.1.112:8080/getOrders", {
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
</script>

<style scoped>
.order-card {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 40px;
}

.order-info {
  display: flex;
  align-items: center;
  flex: 1; /* 使得.order-info占据剩余空间 */
}

.order-id,
.product-name,
.product-price,
.order-date,
.order-total,
.order-status {
  /* flex: 1; 分配相等的空间给每个子元素 */
  /* width: 15%; */
  text-align: center; /* 使文本居中显示 */
  white-space: normal; /* 允许文本自动换行 */
  overflow-wrap: break-word; /* 在长单词或URL地址内部进行换行 */
}

.order-id {
  width: 15%;
  display: flex;
  justify-content: center;
}
.product-name {
  width: 20%;
  display: flex;
  justify-content: center;
}

.product-price {
  width: 12.5%;
  display: flex;
  justify-content: center;
}

.order-date {
  width: 15%;
}

.order-total {
  width: 10%;
  display: flex;
  justify-content: center;
}

.order-status {
  width: 20%;
  display: flex;
  justify-content: center;
}

.product-image {
  /* flex: 0 0 50px; 不改变图片大小，但不允许它成长或缩小 */
  width: 12.5%;
  padding-left: 2%;
  margin-right: 10px;
  border-radius: 5px;
}

.order-actions {
  display: flex;
  align-items: center;
  padding-right: 20px;
  flex: 0 0 auto; /* 使得操作按钮不伸缩，占据必要的空间 */
}

.dialog-content {
  /* 根据需要添加样式 */
}

.icons {
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.icons:hover {
  color: #409eff; /* 鼠标悬停时的颜色，根据需要调整 */
}

.pagination-wrapper {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 根据需要添加一些顶部外边距 */
}

.handles {
  display: flex;
  justify-content: space-around;
}

.search-input {
  margin-top: 1%;
}
.manager_head {
  margin-bottom: 1%;
}
</style>
