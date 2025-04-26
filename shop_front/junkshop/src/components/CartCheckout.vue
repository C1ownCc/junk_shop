<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <h2 class="page-title">确认订单</h2>
    </div>

    <!-- 收货地址 -->
    <div class="section address-section">
      <div class="section-header">
        <h3>收货地址</h3>
        <el-button type="primary" link @click="toggleAddressEdit">
          {{ editingAddress ? '保存地址' : '修改地址' }}
        </el-button>
      </div>
      <div class="address-content">
        <el-form label-position="left" label-width="100px">
          <el-form-item label="收件人">
            <el-input v-model="addressForm.recipientName" placeholder="请输入收件人姓名" :disabled="!editingAddress">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="addressForm.phoneNumber" placeholder="请输入联系电话" :disabled="!editingAddress">
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="收货地址">
            <el-input v-model="addressForm.address" placeholder="请输入详细地址" :disabled="!editingAddress" type="textarea" :rows="2">
            </el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 订单商品 -->
    <div class="section order-section">
      <h3>订单商品</h3>
      
      <!-- 按卖家分组的订单 -->
      <template v-for="(group, sellerId) in groupedItems" :key="sellerId" class="seller-group">
        <div class="seller-header">
          <span class="seller-title">卖家ID: <el-tag size="small" type="info">{{ sellerId }}</el-tag></span>
        </div>
        <el-table :data="group" border>
          <el-table-column label="商品图片" width="120" align="center">
            <template #default="{ row }">
              <div class="item-image">
                <template v-if="row.images && row.images.length > 0">
                  <img :src="row.images[0].imageURL" alt="商品图片" />
                </template>
                <template v-else-if="row.image">
                  <img :src="row.image" alt="商品图片" />
                </template>
                <el-empty v-else description="暂无图片" :image-size="40" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="商品信息">
            <template #default="{ row }">
              <div class="item-name">{{ row.name }}</div>
              <div class="item-desc" v-if="row.description">{{ row.description }}</div>
              <div class="item-desc" v-else-if="row.desc">{{ row.desc }}</div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">
              <span class="item-price">¥{{ row.price.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120" align="center">
            <template #default="{ row }">
              <span>{{ row.quantity }}</span>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">
              <span class="item-subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <div class="seller-subtotal">
          店铺合计: <span class="price">¥{{ calculateSellerTotal(group).toFixed(2) }}</span>
        </div>
      </template>
    </div>
    
    <!-- 订单备注 -->
    <div class="section message-section">
      <h3>订单备注</h3>
      <el-input 
        v-model="message" 
        placeholder="选填: 请输入订单备注信息" 
        type="textarea" 
        :rows="2"
      />
    </div>
    
    <!-- 订单总结 -->
    <div class="section summary-section">
      <div class="summary-row">
        <span>商品总数:</span>
        <span>{{ totalItems }}件</span>
      </div>
      <div class="summary-row">
        <span>商品总金额:</span>
        <span class="total-price">¥{{ calculateGrandTotal().toFixed(2) }}</span>
      </div>
    </div>
    
    <!-- 底部结算栏 -->
    <div class="checkout-footer">
      <div class="footer-summary">
        <div class="summary-label">实付金额:</div>
        <div class="grand-total">¥{{ calculateGrandTotal().toFixed(2) }}</div>
      </div>
      <el-button type="primary" size="large" @click="submitOrder" :loading="submitting" :disabled="!isFormValid">
        提交订单
      </el-button>
    </div>

    <!-- 支付方式选择对话框 -->
    <el-dialog 
      v-model="paymentDialogVisible" 
      title="选择支付方式"
      width="400px"
      class="payment-dialog"
    >
      <div class="payment-options">
        <h4 class="payment-title">订单金额: ¥{{ calculateGrandTotal().toFixed(2) }}</h4>
        
        <el-radio-group v-model="paymentMethod" class="payment-method-group">
          <el-radio label="wallet" class="payment-method-item">
            <div class="method-content">
              <el-icon><Wallet /></el-icon>
              <div class="method-details">
                <span class="method-name">钱包支付</span>
                <span class="method-desc">当前余额: ¥{{ userWalletBalance }}</span>
              </div>
            </div>
          </el-radio>
          <el-radio label="qrcode" class="payment-method-item">
            <div class="method-content">
              <el-icon><Link /></el-icon>
              <div class="method-details">
                <span class="method-name">扫码支付</span>
                <span class="method-desc">扫描二维码完成支付</span>
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="paymentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment">确认支付</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 扫码支付对话框 -->
    <el-dialog 
      v-model="qrCodeDialogVisible" 
      title="扫码支付"
      width="400px"
      class="qrcode-dialog"
    >
      <div class="qrcode-container">
        <h4 class="payment-title">订单金额: ¥{{ calculateGrandTotal().toFixed(2) }}</h4>
        <div class="qrcode-image">
          <el-image src="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=fakepayment" />
        </div>
        <p class="qrcode-hint">请使用手机扫描上方二维码完成支付</p>
        <div class="qrcode-timer">
          <p>二维码有效时间：{{ qrCodeTimer }} 秒</p>
          <el-progress :percentage="(qrCodeTimer / 60) * 100" :show-text="false" />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="qrCodeDialogVisible = false">取消</el-button>
          <el-button type="success" @click="simulateQRCodePayment">模拟支付成功</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Phone, Wallet, Link } from '@element-plus/icons-vue';
import axios from 'axios';

const props = defineProps({
  cartItems: {
    type: Array,
    required: true
  }
});

const emits = defineEmits(['update-nav', 'checkout-complete']);

// 用户信息
const user = reactive({});
const userWalletBalance = ref(0);

// 收货地址
const editingAddress = ref(false);
const addressForm = reactive({
  recipientName: '',
  phoneNumber: '',
  address: ''
});

// 订单备注
const message = ref('');

// 支付相关
const paymentDialogVisible = ref(false);
const qrCodeDialogVisible = ref(false);
const paymentMethod = ref('wallet');
const qrCodeTimer = ref(60);
const timerInterval = ref(null);
const submitting = ref(false);
const createdOrders = ref([]);

// 按卖家分组商品
const groupedItems = computed(() => {
  const grouped = {};
  
  props.cartItems.forEach(item => {
    // 跳过不完整的商品信息
    if (!item || !item.name) {
      console.error('商品信息不完整', item);
      return;
    }
    
    if (!item.sellerID) {
      console.warn('商品缺少卖家ID', item);
      // 使用默认卖家ID
      item.sellerID = 0;
    }
    
    if (!grouped[item.sellerID]) {
      grouped[item.sellerID] = [];
    }
    
    grouped[item.sellerID].push(item);
  });
  
  return grouped;
});

// 商品总数
const totalItems = computed(() => {
  return props.cartItems.reduce((sum, item) => sum + item.quantity, 0);
});

// 表单验证
const isFormValid = computed(() => {
  return addressForm.recipientName && 
         addressForm.phoneNumber && 
         addressForm.address;
});

// 计算某个卖家的商品总金额
const calculateSellerTotal = (items) => {
  // 如果传入的是字符串(sellerID)，则从groupedItems中获取该卖家的商品列表
  const sellerItems = Array.isArray(items) ? items : groupedItems.value[items] || [];
  return sellerItems.reduce((total, item) => {
    return total + (item.price * item.quantity);
  }, 0);
};

// 计算订单总金额
const calculateGrandTotal = () => {
  return props.cartItems.reduce((total, item) => {
    return total + (item.price * item.quantity);
  }, 0);
};

// 切换地址编辑状态
const toggleAddressEdit = () => {
  if (editingAddress.value) {
    // 如果当前是编辑状态，点击后保存并关闭编辑
    editingAddress.value = false;
  } else {
    // 如果当前不是编辑状态，点击后打开编辑
    editingAddress.value = true;
  }
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      console.error("无法获取用户信息，用户未登录或 localStorage 中无用户数据。");
      return;
    }

    const response = await axios.get(
      "http://localhost:8080/getUserByUsername",
      {
        params: { username: userInfo.username },
      }
    );
    
    Object.assign(user, response.data);
    userWalletBalance.value = user.walletBalance || 0;
    
    // 填充默认地址
    if (user.recipientName) {
      addressForm.recipientName = user.recipientName;
      addressForm.phoneNumber = user.phoneNumber || '';
      addressForm.address = user.address || '';
    }
  } catch (error) {
    console.error("获取用户信息失败", error);
    ElMessage.error("获取用户信息失败");
  }
};

// 提交订单
const submitOrder = async () => {
  // 表单验证
  if (!isFormValid.value) {
    ElMessage.warning("请完善收货信息");
    editingAddress.value = true;
    return;
  }
  
  try {
    submitting.value = true;
    
    // 按卖家分组创建订单
    const orderPromises = [];
    const cartItemIds = [];
    
    for (const sellerId in groupedItems.value) {
      const sellerItems = groupedItems.value[sellerId];
      
      // 收集该卖家所有商品的cartItemIds
      sellerItems.forEach(item => {
        if (item.cartItemID) {
          cartItemIds.push(item.cartItemID);
        }
      });
    }
    
    // 调用后端接口创建订单
    const checkoutResponse = await axios.post("http://localhost:8080/cart/checkout", {
      userId: user.userID,
      cartItemIds: cartItemIds,
      recipientName: addressForm.recipientName,
      address: addressForm.address,
      phoneNumber: addressForm.phoneNumber,
      message: message.value
    });
    
    if (checkoutResponse.data.success) {
      // 保存创建的订单ID列表
      createdOrders.value = checkoutResponse.data.orderIds || [];
      
      if (createdOrders.value.length > 0) {
        ElMessage.success("订单创建成功！请前往订单页面查看并支付");
        
        // 直接跳转到订单页面，不显示支付对话框
        setTimeout(() => {
          emits('checkout-complete');
        }, 1500);
      } else {
        ElMessage.error("订单创建成功，但未返回订单ID");
      }
    } else {
      ElMessage.error(checkoutResponse.data.message || "订单创建失败");
    }
  } catch (error) {
    console.error("提交订单出错", error);
    ElMessage.error("订单提交失败，请重试");
  } finally {
    submitting.value = false;
  }
};

// 确认支付
const confirmPayment = () => {
  if (paymentMethod.value === 'wallet') {
    // 钱包支付
    processWalletPayment();
  } else if (paymentMethod.value === 'qrcode') {
    // 扫码支付
    paymentDialogVisible.value = false;
    qrCodeDialogVisible.value = true;
    startQRCodeTimer();
  }
};

// 处理钱包支付
const processWalletPayment = async () => {
  try {
    // 检查钱包余额
    if (userWalletBalance.value < calculateGrandTotal()) {
      ElMessage.error("钱包余额不足，请选择其他支付方式");
      return;
    }
    
    const payResults = [];
    for (const orderId of createdOrders.value) {
      const payResponse = await axios.post("http://localhost:8080/payOrderWithWallet", null, {
        params: {
          orderID: orderId,
          buyerID: user.userID
        }
      });
      
      payResults.push({
        orderId,
        success: payResponse.data === "order paid successfully with wallet",
        message: payResponse.data
      });
    }
    
    // 检查是否所有订单都支付成功
    const allSuccess = payResults.every(result => result.success);
    const failedOrders = payResults.filter(result => !result.success);
    
    if (allSuccess) {
      paymentDialogVisible.value = false;
      ElMessage.success("支付成功！");
      // 刷新用户余额
      await getUserInfo();
      // 通知父组件支付完成，跳转到订单页面
      setTimeout(() => {
        emits('checkout-complete');
      }, 1500);
    } else {
      ElMessage.error(`部分订单支付失败: ${failedOrders.map(o => o.message).join(', ')}`);
    }
  } catch (error) {
    console.error("钱包支付出错", error);
    ElMessage.error("支付处理出错，请重试");
  }
};

// 启动扫码支付倒计时
const startQRCodeTimer = () => {
  qrCodeTimer.value = 60;
  clearInterval(timerInterval.value);
  
  timerInterval.value = setInterval(() => {
    qrCodeTimer.value--;
    if (qrCodeTimer.value <= 0) {
      clearInterval(timerInterval.value);
      qrCodeDialogVisible.value = false;
      ElMessage.warning("支付二维码已过期，请重新提交");
    }
  }, 1000);
};

// 模拟扫码支付成功
const simulateQRCodePayment = async () => {
  try {
    const payResults = [];
    for (const orderId of createdOrders.value) {
      const payResponse = await axios.post(`http://localhost:8080/payOrder/${orderId}`);
      
      payResults.push({
        orderId,
        success: payResponse.data === "order paid successfully",
        message: payResponse.data
      });
    }
    
    // 检查是否所有订单都支付成功
    const allSuccess = payResults.every(result => result.success);
    const failedOrders = payResults.filter(result => !result.success);
    
    if (allSuccess) {
      clearInterval(timerInterval.value);
      qrCodeDialogVisible.value = false;
      ElMessage.success("支付成功！");
      // 通知父组件支付完成，跳转到订单页面
      setTimeout(() => {
        emits('checkout-complete');
      }, 1500);
    } else {
      ElMessage.error(`部分订单支付失败: ${failedOrders.map(o => o.message).join(', ')}`);
    }
  } catch (error) {
    console.error("模拟支付出错", error);
    ElMessage.error("支付处理出错，请重试");
  }
};

// 组件挂载时获取用户信息
onMounted(getUserInfo);

// 组件卸载时清理定时器
onUnmounted(() => {
  clearInterval(timerInterval.value);
});
</script>

<style scoped>
.checkout-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background-color: #f8fafc;
}

.checkout-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.section {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
}

/* 订单商品部分样式 */
.seller-group {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.seller-header {
  background-color: #f5f7fa;
  padding: 10px 15px;
  border-bottom: 1px solid #ebeef5;
}

.seller-title {
  font-weight: 600;
  color: #606266;
}

.item-image {
  width: 80px;
  height: 80px;
  overflow: hidden;
  margin: 0 auto;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.item-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price, .item-subtotal {
  color: #ff6b6b;
  font-weight: 600;
}

.seller-subtotal {
  text-align: right;
  padding: 10px 20px;
  background-color: #f5f7fa;
  font-weight: 600;
}

.seller-subtotal .price {
  color: #ff6b6b;
  font-size: 16px;
  margin-left: 5px;
}

.el-table {
  margin-bottom: 0;
}

.summary-section {
  padding: 16px 24px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #64748b;
}

.summary-row:last-child {
  margin-bottom: 0;
  font-weight: 500;
}

.total-price {
  color: #ef4444;
  font-weight: 600;
}

.checkout-footer {
  position: sticky;
  bottom: 0;
  background-color: white;
  padding: 16px 24px;
  box-shadow: 0 -1px 3px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 12px;
}

.footer-summary {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-label {
  color: #64748b;
}

.grand-total {
  font-size: 24px;
  font-weight: 600;
  color: #ef4444;
}

/* 支付对话框样式 */
.payment-title {
  margin-bottom: 24px;
  color: #1e293b;
  font-size: 16px;
  text-align: center;
}

.payment-method-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.payment-method-item {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
  height: auto;
  margin: 0;
}

.payment-method-item.is-checked {
  border-color: #059669;
  background-color: #f0fdf4;
}

.method-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.method-content .el-icon {
  font-size: 24px;
  color: #059669;
}

.method-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.method-name {
  font-size: 16px;
  font-weight: 500;
  color: #1e293b;
}

.method-desc {
  font-size: 13px;
  color: #64748b;
}

/* 扫码支付对话框样式 */
.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
}

.qrcode-image {
  margin: 24px 0;
  padding: 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.qrcode-hint {
  color: #64748b;
  font-size: 14px;
  margin: 16px 0;
  text-align: center;
}

.qrcode-timer {
  width: 100%;
  margin-top: 16px;
}

.qrcode-timer p {
  text-align: center;
  color: #64748b;
  margin-bottom: 8px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .checkout-container {
    padding: 16px;
  }
  
  .section {
    padding: 16px;
  }
  
  .checkout-footer {
    flex-direction: column;
    gap: 16px;
  }
  
  .footer-summary {
    width: 100%;
    justify-content: space-between;
  }
}
</style> 