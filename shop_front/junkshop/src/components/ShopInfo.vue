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
          v-if="getItemData().images && getItemData().images.length > 0"
        >
          <el-carousel-item 
            v-for="image in getItemData().images" 
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
        <h1 class="product-name">{{ getItemData().name }}</h1>
        
        <div class="price-condition">
          <span class="price">¥{{ getItemData().price }}</span>
          <el-tag size="large" effect="plain">{{ getItemData().condition }}</el-tag>
          <span class="stock-info">
            {{ getStockDisplay(getItemData()) }}
          </span>
        </div>

        <div class="description-section">
          <h3>商品描述</h3>
          <p class="description">{{ getItemData().description }}</p>
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
          <el-button type="primary" size="large" @click="buyNow" :disabled="getItemData().quantity <= 0">
            <el-icon><ShoppingCart /></el-icon>立即购买
          </el-button>
          <el-button type="success" size="large" @click="addToCart" :disabled="getItemData().quantity <= 0">
            <el-icon><ShoppingBag /></el-icon>加入购物车
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
            <el-form-item label="商品规格">
              <el-text>{{ getItemData().condition }}</el-text>
            </el-form-item>
            <el-form-item label="商品单价">
              <el-text type="danger">¥{{ getItemData().price }}</el-text>
            </el-form-item>
            <el-form-item label="购买数量">
              <el-input-number 
                v-model="order.quantity" 
                :min="1" 
                :max="getItemData().quantity"
                :step="1"
                size="small"
                @change="updateTotalPrice" />
              <span class="inventory-info">库存: {{ getItemData().quantity }}</span>
            </el-form-item>
          </div>

          <div class="delivery-info">
            <h3>收货信息</h3>
            <el-form-item label="收件人">
              <el-input v-model="order.recipientName" placeholder="请输入收件人姓名" :disabled="!editingAddress">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="order.phoneNumber" placeholder="请输入联系电话" :disabled="!editingAddress">
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="order.address" placeholder="请输入详细地址" :disabled="!editingAddress" type="textarea" :rows="2">
              </el-input>
            </el-form-item>
            <div class="address-edit-btn">
              <el-button type="primary" link @click="toggleAddressEdit">
                {{ editingAddress ? '保存地址' : '修改地址' }}
              </el-button>
              <el-button v-if="!editingAddress" type="success" link @click="useDefaultAddress">
                使用默认地址
              </el-button>
            </div>
          </div>

          <div class="order-message">
            <h3>订单备注</h3>
            <el-form-item label="买家留言">
              <el-input v-model="order.message" placeholder="选填：请输入留言内容" type="textarea" :rows="2"></el-input>
            </el-form-item>
          </div>

          <div class="order-total">
            <div class="total-line">
              <span>商品总价：</span>
              <span class="price">¥{{ (order.price * order.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-tooltip
            effect="dark"
            content="提交订单后可在订单详情页面进行支付"
            placement="top"
          >
            <el-icon class="help-icon"><QuestionFilled /></el-icon>
          </el-tooltip>
          <div class="dialog-buttons">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitOrder">提交订单</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessageBox, ElMessage } from "element-plus";
import { onMounted, ref, reactive, getCurrentInstance, watch } from "vue";
import axios from "axios";
import { ShoppingBag, User, Phone } from '@element-plus/icons-vue';

const emitBack = defineEmits(["back", "chat", "order", "add-to-cart"]);
const instance = getCurrentInstance();
const props = defineProps({
  selectedItem: {
    type: Object,
    required: true,
  },
});

const sellerInfo = ref({});
const itemData = ref(null); // 用于存储从后端获取的最新商品数据

// 根据商品ID从后端获取最新商品信息
const fetchItemDetails = async (itemId) => {
  if (!itemId) return;
  
  try {
    const response = await axios.get("http://localhost:8080/getItemById", {
      params: { id: itemId }
    });
    
    if (response.data) {
      itemData.value = response.data;
      // 成功获取商品信息后获取卖家信息
      if (itemData.value.sellerID) {
        await getSellerInfo(itemData.value.sellerID);
      }
    } else {
      ElMessage.error("获取商品信息失败");
    }
  } catch (error) {
    console.error("获取商品详情失败", error);
    ElMessage.error("获取商品详情时出错");
  }
};

const getSellerInfo = async (sellerId) => {
  try {
    const response = await axios.get("http://localhost:8080/getUserByUserID", {
      params: { userID: sellerId || props.selectedItem.sellerID },
    });
    sellerInfo.value = response.data;
  } catch (error) {
    console.error("获取商家信息失败", error);
    ElMessage.error("获取商家信息失败");
  }
};

// 获取商品的实际数据，优先使用从后端获取的最新数据
const getItemData = () => {
  return itemData.value || props.selectedItem;
};

// 监听props.selectedItem的变化，当ID变化时重新获取数据
watch(() => props.selectedItem?.itemID, (newId) => {
  if (newId) {
    fetchItemDetails(newId);
  }
}, { immediate: true });

const goBack = () => {
  emitBack("back");
};

const contactSeller = () => {
  emitBack("chat", getItemData().sellerID);
};

const dialogVisible = ref(false);
const user = reactive({});
const editingAddress = ref(false);
const order = ref({
  orderID: "",
  buyerID: "",
  sellerID: "",
  itemID: "",
  price: "",
  itemName: "",
  quantity: 1, // 默认购买数量为1
  recipientName: "",
  phoneNumber: "",
  address: "",
  message: "", // 买家留言
  orderStatus: "待付款" // 初始状态为待付款
});

// 切换地址编辑状态
const toggleAddressEdit = () => {
  if (editingAddress.value) {
    // 如果当前正在编辑，点击后保存并退出编辑模式
    editingAddress.value = false;
  } else {
    // 如果当前不在编辑，点击后进入编辑模式
    editingAddress.value = true;
  }
};

// 使用默认地址
const useDefaultAddress = () => {
  if (user && user.recipientName) {
    order.value.recipientName = user.recipientName;
    order.value.phoneNumber = user.phoneNumber;
    order.value.address = user.address;
  } else {
    ElMessage.warning('未找到默认地址信息，请手动填写');
    editingAddress.value = true;
  }
};

// 更新订单总价
const updateTotalPrice = () => {
  // 确保数量不超过库存
  if (order.value.quantity > getItemData().quantity) {
    order.value.quantity = getItemData().quantity;
    ElMessage.warning(`最大可购买数量为${getItemData().quantity}`);
  }
};

const buyNow = () => {
  // 检查用户是否已登录
  if (!user.userID) {
    ElMessage.error("请先登录后再购买商品！");
    return;
  }
  
  const currentItem = getItemData();
  
  // 检查是否是自己的商品
  if (user.userID === currentItem.sellerID) {
    ElMessage.error("不能购买自己的商品！");
    return;
  }
  
  // 检查商品库存
  if (currentItem.quantity <= 0) {
    ElMessage.error("商品库存不足！");
    return;
  }
  
  console.log("商品信息:", currentItem); // 调试用，查看商品信息
  
  // 重置订单信息
  order.value = {
    orderID: "",
    buyerID: user.userID,
    sellerID: currentItem.sellerID, // 确保设置正确的卖家ID
    itemID: currentItem.itemID,
    price: currentItem.price,
    itemName: currentItem.name,
    quantity: 1, // 默认购买数量为1
    recipientName: user.recipientName || '',
    phoneNumber: user.phoneNumber || '',
    address: user.address || '',
    message: '',
    orderStatus: '待付款' // 设置初始订单状态
  };
  
  console.log("初始化订单数据:", order.value); // 调试用，查看初始化订单数据
  
  // 如果有默认地址，自动填充
  if (user.recipientName) {
    useDefaultAddress();
  } else {
    // 如果没有默认地址，自动开启编辑模式
    editingAddress.value = true;
  }
  
  dialogVisible.value = true;
};

// 提交订单
const submitOrder = async () => {
  // 验证订单信息
  if (!order.value.recipientName || !order.value.phoneNumber || !order.value.address) {
    ElMessage.error("请完善收货信息！");
    editingAddress.value = true;
    return;
  }
  
  // 验证购买数量
  if (order.value.quantity <= 0) {
    ElMessage.error("购买数量必须大于0！");
    return;
  }
  
  // 验证库存
  const currentItem = getItemData();
  if (order.value.quantity > currentItem.quantity) {
    ElMessage.error(`库存不足！最多可购买${currentItem.quantity}件`);
    order.value.quantity = currentItem.quantity;
    return;
  }
  
  try {
    // 确保设置了正确的sellerID和orderStatus
    order.value.sellerID = currentItem.sellerID;
    order.value.orderStatus = "待付款";
    
    console.log("提交订单数据:", order.value); // 调试用，查看提交的订单数据
    
    // 创建订单
    const res = await axios.post(
      "http://localhost:8080/userCreatePendingOrder", // 使用创建待付款订单的接口
      order.value
    );
    
    if (res.data.includes("order created") || res.data.includes("pending order created")) {
      ElMessage.success("订单提交成功！");
      dialogVisible.value = false;
      
      // 商品库存处理 - 在支付时才会减少库存，此处不需要减库存
      // 跳转到订单详情页
      emitBack("order");
    } else {
      ElMessage.error("订单提交失败！" + res.data);
    }
  } catch (err) {
    console.error("订单提交错误", err);
    ElMessage.error("订单提交出错，请稍后重试！");
  }
};

// 加入购物车功能
const addToCart = async () => {
  const currentItem = getItemData();
  
  if (user.userID === currentItem.sellerID) {
    ElMessage.error("不能购买自己的商品！");
    return;
  }
  
  if (currentItem.quantity <= 0) {
    ElMessage.error("商品库存不足！");
    return;
  }
  
  try {
    const response = await axios.post("http://localhost:8080/cart/add", null, {
      params: {
        userId: user.userID,
        itemId: currentItem.itemID,
        quantity: 1
      }
    });
    
    if (response.data.success) {
      ElMessage.success(response.data.message || "商品已加入购物车");
    } else {
      ElMessage.error(response.data.message || "加入购物车失败");
    }
  } catch (error) {
    console.error("加入购物车失败", error);
    ElMessage.error("加入购物车时出错");
  }
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
    // 将响应数据复制到user对象
    Object.assign(user, response.data);
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

onMounted(async () => {
  await getUserInfo();
  if (props.selectedItem && props.selectedItem.itemID) {
    await fetchItemDetails(props.selectedItem.itemID);
  }
});

const getStockDisplay = (item) => {
  // 始终显示库存数量，不管是多少
  return `库存: ${item.quantity || 0}`;
};
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

.stock-info {
  font-size: 14px;
  color: #64748b;
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

/* 响应式设计调整 */
@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
  }
}

/* 订单确认对话框样式 */
.order-form {
  padding: 16px;
}

.order-summary,
.delivery-info,
.order-message,
.order-total {
  margin-bottom: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
}

.order-summary h3,
.delivery-info h3,
.order-message h3 {
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
  color: #64748b;
  font-size: 16px;
  font-weight: 600;
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
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.address-edit-btn {
  display: flex;
  justify-content: flex-end;
  margin-top: -10px;
  margin-bottom: 10px;
}

.order-total {
  padding: 16px;
  margin-top: 20px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  padding: 8px 0;
}

.total-line .price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
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

/* 表单项样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #64748b;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

:deep(.el-input__inner) {
  color: #334155;
}

:deep(.el-textarea__inner) {
  color: #334155;
}

.inventory-info {
  margin-left: 12px;
  font-size: 13px;
  color: #64748b;
}
</style>
