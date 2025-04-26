<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <h2 class="checkout-title">确认订单</h2>
    </div>
    
    <!-- 收货信息部分 -->
    <div class="section shipping-info">
      <h3 class="section-title">收货信息</h3>
      <el-form :model="orderInfo" label-width="80px" class="shipping-form">
        <el-form-item label="收件人" required>
          <el-input v-model="orderInfo.recipientName" placeholder="请输入收件人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="orderInfo.phoneNumber" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="收货地址" required>
          <el-input v-model="orderInfo.address" type="textarea" :rows="2" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        
        <div class="address-actions">
          <el-button type="primary" link @click="useDefaultAddress" v-if="hasDefaultAddress">
            使用默认地址
          </el-button>
        </div>
      </el-form>
    </div>
    
    <!-- 订单商品列表 -->
    <div class="section order-items">
      <h3 class="section-title">订单商品</h3>
      <div class="item-list">
        <div v-for="(item, index) in checkoutItems" :key="index" class="order-item">
          <div class="item-image">
            <img 
              v-if="item.images && item.images.length > 0" 
              :src="item.images[0].imageURL" 
              alt="商品图片" 
            />
            <div v-else class="no-image">暂无图片</div>
          </div>
          <div class="item-details">
            <div class="item-name">{{ item.itemName }}</div>
            <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
          </div>
          <div class="item-quantity">x{{ item.quantity }}</div>
          <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>
    </div>
    
    <!-- 订单备注 -->
    <div class="section order-remark">
      <h3 class="section-title">订单备注</h3>
      <el-input 
        v-model="orderInfo.message" 
        type="textarea" 
        :rows="3" 
        placeholder="选填：请输入订单备注，如有特殊要求请在此说明"
      ></el-input>
    </div>
    
    <!-- 金额信息 -->
    <div class="section payment-info">
      <div class="payment-line">
        <span>商品金额：</span>
        <span>¥{{ calculateSubtotal().toFixed(2) }}</span>
      </div>
      <div class="payment-line">
        <span>优惠券：</span>
        <span>¥0.00</span>
      </div>
      <div class="payment-line total">
        <span>应付金额：</span>
        <span class="total-price">¥{{ calculateTotal().toFixed(2) }}</span>
      </div>
    </div>
    
    <!-- 底部结算按钮 -->
    <div class="checkout-footer">
      <el-button @click="goBack">返回购物车</el-button>
      <el-button type="primary" @click="submitOrder" :loading="submitting">提交订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['back', 'order-submitted']);

// 当前用户信息
const user = ref({});
// 结算商品
const checkoutItems = ref([]);
// 订单信息
const orderInfo = ref({
  recipientName: '',
  phoneNumber: '',
  address: '',
  message: '',
  cartItemIds: [] // 要结算的购物车项ID
});
// 是否有默认地址
const hasDefaultAddress = computed(() => 
  user.value.recipientName && user.value.phoneNumber && user.value.address
);
// 提交中状态
const submitting = ref(false);

// 计算商品小计
const calculateSubtotal = () => {
  return checkoutItems.value.reduce((total, item) => 
    total + (item.price * item.quantity), 0
  );
};

// 计算总金额
const calculateTotal = () => {
  return calculateSubtotal(); // 这里可以加入其他计算，如优惠、运费等
};

// 返回购物车
const goBack = () => {
  emit('back');
};

// 使用默认地址
const useDefaultAddress = () => {
  if (hasDefaultAddress.value) {
    orderInfo.value.recipientName = user.value.recipientName;
    orderInfo.value.phoneNumber = user.value.phoneNumber;
    orderInfo.value.address = user.value.address;
  } else {
    ElMessage.warning('未找到默认地址信息');
  }
};

// 提交订单
const submitOrder = async () => {
  // 验证订单信息
  if (!orderInfo.value.recipientName || !orderInfo.value.phoneNumber || !orderInfo.value.address) {
    ElMessage.error('请完善收货信息');
    return;
  }
  
  if (checkoutItems.value.length === 0) {
    ElMessage.error('订单中没有商品');
    return;
  }
  
  submitting.value = true;
  
  try {
    // 准备请求数据
    const requestData = {
      userId: user.value.userID,
      recipientName: orderInfo.value.recipientName,
      address: orderInfo.value.address,
      phoneNumber: orderInfo.value.phoneNumber,
      message: orderInfo.value.message
    };
    
    // 发送结算请求
    const response = await axios.post(
      `http://localhost:8080/cart/checkout?userId=${user.value.userID}&recipientName=${encodeURIComponent(orderInfo.value.recipientName)}&address=${encodeURIComponent(orderInfo.value.address)}&phoneNumber=${encodeURIComponent(orderInfo.value.phoneNumber)}`,
      orderInfo.value.cartItemIds,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    
    if (response.data.success) {
      ElMessage.success('订单创建成功');
      
      // 如果有创建的订单ID，可以跳转到支付页面
      if (response.data.orderIds && response.data.orderIds.length > 0) {
        // 保存订单信息到本地存储，用于显示支付弹窗
        const pendingOrder = {
          orderID: parseInt(response.data.orderIds[0]), // 使用第一个订单ID作为主ID
          allOrderIds: response.data.orderIds, // 保存所有订单ID
          buyerID: user.value.userID,
          itemName: checkoutItems.value.length > 1 
            ? `${checkoutItems.value[0].itemName || '商品'}等${checkoutItems.value.length}件商品` 
            : (checkoutItems.value[0]?.itemName || '商品'),
          price: calculateTotal(),
          quantity: checkoutItems.value.reduce((sum, item) => sum + item.quantity, 0), // 计算总数量
          recipientName: orderInfo.value.recipientName,
          phoneNumber: orderInfo.value.phoneNumber,
          address: orderInfo.value.address,
          message: orderInfo.value.message,
          orderStatus: '待支付',
          isMultipleOrders: response.data.orderIds.length > 1 // 标记是否为多订单
        };
        
        localStorage.setItem('pendingPaymentOrder', JSON.stringify(pendingOrder));
        localStorage.setItem('showPendingPayment', 'true');
      }
      
      // 通知父组件订单已提交
      emit('order-submitted', response.data.orderIds);
    } else {
      ElMessage.error(response.data.message || '订单创建失败');
    }
  } catch (error) {
    console.error('提交订单失败', error);
    ElMessage.error('提交订单时发生错误');
  } finally {
    submitting.value = false;
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
    
    const response = await axios.get("http://localhost:8080/getUserByUsername", {
      params: { username: userInfo.username },
    });
    
    user.value = response.data;
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

// 初始化订单数据
const initOrderData = () => {
  // 初始化结算商品
  checkoutItems.value = [...props.items];
  
  // 收集购物车项ID
  orderInfo.value.cartItemIds = checkoutItems.value.map(item => item.cartItemID);
};

// 组件挂载时初始化
onMounted(async () => {
  await getUserInfo();
  initOrderData();
  
  // 如果有默认地址，自动填充
  if (hasDefaultAddress.value) {
    useDefaultAddress();
  }
});
</script>

<style scoped>
.checkout-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
}

.checkout-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.checkout-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.section {
  margin-bottom: 28px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #334155;
  margin: 0 0 16px 0;
  border-left: 4px solid #3b82f6;
  padding-left: 10px;
}

.shipping-form {
  max-width: 600px;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: -10px;
}

.order-items {
  margin-top: 20px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e2e8f0;
  color: #94a3b8;
  font-size: 12px;
}

.item-details {
  flex: 1;
}

.item-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #334155;
}

.item-price {
  color: #64748b;
  font-size: 14px;
}

.item-quantity {
  margin: 0 16px;
  color: #64748b;
}

.item-total {
  font-weight: 600;
  color: #f43f5e;
  min-width: 70px;
  text-align: right;
}

.payment-info {
  background: white;
}

.payment-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #64748b;
}

.payment-line.total {
  font-weight: 500;
  color: #334155;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #e2e8f0;
}

.total-price {
  font-size: 20px;
  color: #f43f5e;
  font-weight: 600;
}

.checkout-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}
</style> 