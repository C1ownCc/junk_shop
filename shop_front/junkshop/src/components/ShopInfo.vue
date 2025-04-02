<template>
  <div class="shop-info-container">
    <el-card class="product-details-card">
      <div class="back-button" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>

      <!-- 图片展示区 -->
      <div class="image-gallery">
        <el-carousel 
          :interval="4000" 
          type="card" 
          height="400px"
          v-if="props.selectedItem.images && props.selectedItem.images.length > 0"
        >
          <el-carousel-item 
            v-for="image in props.selectedItem.images" 
            :key="image.imageID"
            class="carousel-item"
          >
            <img 
              v-if="image.imageURL"
              :src="image.imageURL"
              alt="商品图片"
              class="carousel-image"
            />
          </el-carousel-item>
        </el-carousel>
        <el-empty 
          v-else 
          description="暂无图片" 
          :image-size="200"
        />
      </div>

      <!-- 商品信息区 -->
      <div class="product-info">
        <h1 class="product-name">{{ props.selectedItem.name }}</h1>
        
        <div class="price-condition">
          <span class="price">¥{{ props.selectedItem.price }}</span>
          <el-tag size="large" effect="plain">{{ props.selectedItem.condition }}</el-tag>
        </div>

        <div class="description-section">
          <h3>商品描述</h3>
          <p class="description">{{ props.selectedItem.description }}</p>
        </div>

        <!-- 商家信息区域 -->
        <div class="seller-section">
          <h3>商家信息</h3>
          <div class="seller-info">
            <el-avatar :size="50" :src="sellerInfo.avatar" />
            <div class="seller-details">
              <div class="seller-name">{{ sellerInfo.nickName }}</div>
              <div class="seller-contact">
                <span>联系电话：{{ sellerInfo.phoneNumber }}</span>
                <span>邮箱：{{ sellerInfo.email }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="buyNow">
            <el-icon><ShoppingCart /></el-icon>立即购买
          </el-button>
          <el-button type="info" size="large" @click="contactSeller">
            <el-icon><Message /></el-icon>联系商家
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 订单确认对话框 -->
    <el-dialog v-model="dialogVisible" 
               title="订单确认" 
               width="600px"
               destroy-on-close>
      <div class="order-form">
        <el-form label-position="left" 
                 label-width="100px" 
                 :model="order">
          <div class="order-summary">
            <h3>商品信息</h3>
            <el-form-item label="商品名称">
              <el-text>{{ order.itemName }}</el-text>
            </el-form-item>
            <el-form-item label="商品图片">
              <div class="order-images">
                <img v-for="image in props.selectedItem.images"
                     :key="image.imageID"
                     :src="image.imageURL"
                     v-if="image.imageURL"
                     class="order-image" />
              </div>
            </el-form-item>
            <el-form-item label="商品规格">
              <el-text>{{ props.selectedItem.condition }}</el-text>
            </el-form-item>
            <el-form-item label="商品价格">
              <el-text type="danger">¥{{ props.selectedItem.price }}</el-text>
            </el-form-item>
          </div>

          <div class="delivery-info">
            <h3>收货信息</h3>
            <el-form-item label="收件人">
              <el-text>{{ user.value.recipientName }}</el-text>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-text>{{ user.value.phoneNumber }}</el-text>
            </el-form-item>
            <el-form-item label="收货地址">
              <el-text>{{ user.value.address }}</el-text>
            </el-form-item>
          </div>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-tooltip
            effect="dark"
            content="如订单信息错误，请联系商家修改！如收件人信息错误，请在个人信息中修改！"
            placement="top"
          >
            <el-icon class="help-icon"><QuestionFilled /></el-icon>
          </el-tooltip>
          <div class="dialog-buttons">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="payNow">确认支付</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessageBox, ElMessage } from "element-plus";
import { onMounted, ref, reactive, getCurrentInstance } from "vue";
import axios from "axios";

const emitBack = defineEmits(["back", "chat", "order"]);
const instance = getCurrentInstance();
const props = defineProps({
  selectedItem: {
    type: Object,
    required: true,
  },
});

const sellerInfo = ref({});

const getSellerInfo = async () => {
  try {
    const response = await axios.get("http://localhost:8080/getUserByUserID", {
      params: { userID: props.selectedItem.sellerID },
    });
    sellerInfo.value = response.data;
  } catch (error) {
    console.error("获取商家信息失败", error);
    ElMessage.error("获取商家信息失败");
  }
};

onMounted(getSellerInfo);

const goBack = () => {
  emitBack("back");
};

const contactSeller = () => {
  emitBack("chat", props.selectedItem.sellerID);
};

const dialogVisible = ref(false);
const user = ref({});
const order = ref({
  orderID: "",
  buyerID: "", //
  sellerID: "",
  itemID: "",
  price: "",
  itemName: "",
});
const buyNow = () => {
  dialogVisible.value = true;
  order.value.buyerID = user.value.userID;
  order.value.sellerID = props.selectedItem.sellerID;
  order.value.itemID = props.selectedItem.itemID;
  order.value.price = props.selectedItem.price;
  order.value.itemName = props.selectedItem.name;
  order.value.recipientName = user.value.recipientName;
  order.value.phoneNumber = user.value.phoneNumber;
  order.value.address = user.value.address;
};

const payNow = () => {
  ElMessageBox.confirm("确定要支付吗？", "Warning", {
    confirmButtonText: "确定支付",
    cancelButtonText: "取消支付",
    type: "warning",
  })
    .then(async () => {
      if (user.value.userID === props.selectedItem.sellerID) {
        ElMessage.error("不能购买自己的商品！");
        return;
      }
      if (user.value.walletBalance < order.value.price) {
        ElMessage.error("余额不足！请前往个人中心充值！");
        return;
      }
      try {
        const res = await axios.post(
          "http://localhost:8080/userCreateOrder",
          order.value
        );
        console.log(res.data);
        if (res.data === "order created") {
          let changeStatus = ref({});
          changeStatus.value.itemID = props.selectedItem.itemID;
          changeStatus.value.status = "已售出";
          const res2 = await axios.put(
            "http://localhost:8080/userChangeStatus",
            changeStatus.value
          );
          if (res2.data === "status changed") {
            let changeMoney = ref({});
            changeMoney.value.userID = user.value.userID;
            changeMoney.value.walletBalance =
              user.value.walletBalance - order.value.price;
            const res3 = await axios.put(
              "http://localhost:8080/changeUserWalletBalance",
              changeMoney.value
            );
            if (res3.data === "walletBalance changed") {
              ElMessage.success("支付成功！请等待卖家发货！");
              dialogVisible.value = false;
              emitBack("order");
            }
          }
        }
      } catch (err) {
        ElMessage.error("支付请求错误！", err);
      }
    })
    .catch(() => {});
};

const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      console.error("无法获取用户信息，用户未登录或 localStorage 中无用户数据。");
      return;
    }

    const response = await axios.get("http://localhost:8080/getUserByUsername", {
      params: { username: userInfo.username },
    });
    user.value = response.data;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

onMounted(getUserInfo);
</script>

<style scoped>
.shop-info-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.product-details-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.back-button:hover {
  color: #409EFF;
}

.image-gallery {
  padding: 20px;
  width: 100%;
}

:deep(.el-carousel__item) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-carousel__item--card) {
  border-radius: 8px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #f8f9fa;
}

:deep(.el-carousel__arrow) {
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  border: 2px solid #fff;
  transition: all 0.3s;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(0, 0, 0, 0.5);
}

:deep(.el-carousel__indicators) {
  transform: translateY(16px);
}

:deep(.el-carousel__indicator--horizontal) {
  padding: 12px 4px;
}

:deep(.el-carousel__button) {
  width: 30px;
  height: 2px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 2px;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 14px;
}

.price-condition {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
}

.price {
  font-size: 24px;
  font-weight: 600;
  color: #f56c6c;
}

.description-section {
  margin: 20px 0;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
}

.description-section h3 {
  color: #64748b;
  margin-bottom: 10px;
}

.description {
  color: #334155;
  line-height: 1.6;
}

.seller-section {
  margin: 20px 0;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
}

.seller-section h3 {
  color: #64748b;
  margin-bottom: 16px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.seller-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seller-name {
  font-size: 18px;
  font-weight: 600;
  color: #334155;
}

.seller-contact {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #64748b;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.action-buttons .el-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

/* 订单确认对话框样式 */
.order-form {
  padding: 16px;
}

.order-summary,
.delivery-info {
  margin-bottom: 20px;
}

.order-summary h3,
.delivery-info h3 {
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
  color: #64748b;
}

.order-images {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
}

.order-image {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px;
}

.help-icon {
  font-size: 18px;
  color: #94a3b8;
  cursor: help;
}

.dialog-buttons {
  display: flex;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .shop-info-container {
    padding: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .product-name {
    font-size: 20px;
  }

  .price {
    font-size: 22px;
  }
}
</style>
