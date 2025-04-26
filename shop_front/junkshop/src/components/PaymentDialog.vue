<template>
  <el-dialog
    v-model="visible"
    title="订单支付"
    width="550px"
    :close-on-click-modal="false"
    class="payment-dialog"
  >
    <div class="payment-container">
      <!-- 订单信息部分 -->
      <div class="order-info-section">
        <h3>订单信息</h3>
        <div class="order-details">
          <div class="info-row">
            <span class="label">订单编号：</span>
            <span class="value">
              <template v-if="order.isMultipleOrders && order.allOrderIds">
                {{ order.orderID }}等{{ order.allOrderIds.length }}个订单
              </template>
              <template v-else>
                {{ order.orderID }}
              </template>
            </span>
          </div>
          <div class="info-row">
            <span class="label">商品名称：</span>
            <span class="value">{{ order.itemName || '购物车商品' }}</span>
          </div>
          <div class="info-row">
            <span class="label">购买数量：</span>
            <span class="value">{{ order.quantity || 0 }}件</span>
          </div>
          <div class="info-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ order.createdAt ? formatDate(order.createdAt) : '刚刚' }}</span>
          </div>
          <div class="info-row">
            <span class="label">支付期限：</span>
            <span class="value time-limit">{{ order.createdAt ? getPaymentDueTime(order.createdAt) : '24小时内' }}</span>
          </div>
          <div class="info-row">
            <span class="label">收货人：</span>
            <span class="value">{{ order.recipientName }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系电话：</span>
            <span class="value">{{ order.phoneNumber }}</span>
          </div>
          <div class="info-row">
            <span class="label">收货地址：</span>
            <span class="value address">{{ order.address }}</span>
          </div>
          <div class="info-row payment-amount">
            <span class="label">应付金额：</span>
            <span class="value price">¥{{ order.price.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-method-section">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="paymentMethod" class="payment-options">
          <el-radio label="wallet">
            <div class="payment-option">
              <el-icon><WalletFilled /></el-icon>
              <span>钱包支付</span>
            </div>
          </el-radio>
          <el-radio label="qrcode">
            <div class="payment-option">
              <el-icon><Promotion /></el-icon>
              <span>扫码支付</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 根据支付方式展示不同的支付内容 -->
      <div class="payment-content">
        <!-- 钱包支付内容 -->
        <div v-if="paymentMethod === 'wallet'" class="wallet-payment">
          <div class="balance-info">
            <span>账户余额：</span>
            <span class="balance">¥{{ userBalance.toFixed(2) }}</span>
          </div>
          <el-alert
            v-if="userBalance < order.price"
            title="账户余额不足，请充值或选择其他支付方式"
            type="warning"
            show-icon
          />
        </div>

        <!-- 扫码支付内容 -->
        <div v-else-if="paymentMethod === 'qrcode'" class="qrcode-payment">
          <div class="qrcode-container">
            <img :src="qrCodeUrl" alt="支付二维码" class="qrcode-image" />
            <p class="qrcode-tip">请使用手机扫描二维码完成支付</p>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">取消支付</el-button>
        <el-button
          type="danger"
          plain
          @click="cancelOrder"
        >
          取消订单
        </el-button>
        <el-button
          type="primary"
          :disabled="isPayButtonDisabled"
          @click="confirmPayment"
          :loading="paymentLoading"
        >
          立即支付
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { WalletFilled, Promotion } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  order: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update:modelValue', 'payment-success', 'payment-cancel', 'order-cancel']);

// 计算属性：对话框是否可见
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

// 支付相关状态
const paymentMethod = ref('wallet');
const userBalance = ref(10000); // 模拟用户余额，实际项目中应从API获取
const paymentLoading = ref(false);
const qrCodeUrl = ref('https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=https://example.com/pay/' + props.order.orderID);

// 模拟获取用户余额
const getUserBalance = async () => {
  try {
    console.log('正在获取用户余额...');
    // 实际项目中这里应该调用API获取用户余额
    // 以下是模拟代码
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    console.log('用户信息:', userInfo);
    
    if (userInfo && userInfo.username) {
      // 实际项目中应该通过API获取用户余额
      // const response = await axios.get('http://localhost:8080/getUserBalance', {
      //   params: { userID: userInfo.userID }
      // });
      // userBalance.value = response.data.balance;
      
      // 这里模拟随机余额，实际项目应该使用真实数据
      userBalance.value = Math.random() > 0.5 ? props.order.price * 2 : props.order.price * 0.5;
      console.log('设置用户余额为:', userBalance.value);
    } else {
      console.log('未找到用户信息，使用默认余额');
      userBalance.value = 10000; // 使用默认值
    }
  } catch (error) {
    console.error('获取用户余额失败', error);
    ElMessage.error('获取用户余额信息失败');
    userBalance.value = 10000; // 出错时使用默认值
  }
};

// 刷新二维码和更新用户余额
watch([paymentMethod, () => props.modelValue], ([newMethod, visible]) => {
  if (newMethod === 'qrcode') {
    // 生成新的支付二维码，添加随机参数防止缓存
    const randomParam = Math.random().toString(36).substring(7);
    qrCodeUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=https://example.com/pay/${props.order.orderID}?r=${randomParam}`;
  }
  
  // 当对话框显示时获取余额
  if (visible) {
    getUserBalance();
  }
});

// 计算支付按钮是否禁用
const isPayButtonDisabled = computed(() => {
  if (paymentMethod.value === 'wallet') {
    return userBalance.value < props.order.price;
  }
  return false;
});

// 关闭对话框
const closeDialog = () => {
  ElMessageBox.confirm(
    '确定要取消本次支付吗？',
    '取消支付',
    {
      confirmButtonText: '确定',
      cancelButtonText: '返回支付',
      type: 'warning',
    }
  )
    .then(() => {
      visible.value = false;
      emit('payment-cancel');
    })
    .catch(() => {
      // 用户选择返回支付，不做任何操作
    });
};

// 取消订单
const cancelOrder = () => {
  ElMessageBox.confirm(
    '确定要取消此订单吗？取消后无法恢复',
    '取消订单',
    {
      confirmButtonText: '确定取消',
      cancelButtonText: '返回支付',
      type: 'warning',
    }
  )
    .then(() => {
      visible.value = false;
      emit('order-cancel', props.order);
    })
    .catch(() => {
      // 用户选择返回支付，不做任何操作
    });
};

// 确认支付
const confirmPayment = async () => {
  try {
    paymentLoading.value = true;
    
    // 模拟支付延迟
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 检查是否是多订单支付
    if (props.order.isMultipleOrders && props.order.allOrderIds) {
      // 多订单情况，遍历所有订单ID进行支付
      const orderIds = props.order.allOrderIds;
      let failedOrders = [];
      let successCount = 0;
      
      for (const orderId of orderIds) {
        try {
          const response = await axios.post(`http://localhost:8080/payOrder/${orderId}`);
          if (response.data.includes('success') || response.data.includes('paid successfully')) {
            successCount++;
          } else {
            failedOrders.push(orderId);
          }
        } catch (error) {
          console.error(`订单 ${orderId} 支付失败:`, error);
          failedOrders.push(orderId);
        }
      }
      
      if (failedOrders.length === 0) {
        ElMessage.success('所有订单支付成功！');
        visible.value = false;
        emit('payment-success');
      } else if (successCount > 0) {
        ElMessage.warning(`部分订单支付成功，${failedOrders.length}个订单支付失败`);
        visible.value = false;
        emit('payment-success');
      } else {
        ElMessage.error('所有订单支付失败');
      }
    } else {
      // 单订单支付
      const response = await axios.post(`http://localhost:8080/payOrder/${props.order.orderID}`);
      
      if (response.data.includes('success') || response.data.includes('paid successfully')) {
        ElMessage.success('支付成功！');
        visible.value = false;
        emit('payment-success');
      } else {
        ElMessage.error('支付失败：' + response.data);
      }
    }
  } catch (error) {
    console.error('支付过程出错', error);
    ElMessage.error('支付过程出错，请稍后重试');
  } finally {
    paymentLoading.value = false;
  }
};

// 组件挂载时获取用户余额
onMounted(() => {
  getUserBalance();
});

// 添加日期格式化函数到script部分
// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return '刚刚';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
      // 如果日期无效，返回当前时间的格式化字符串
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error('日期格式化错误', error);
    return '刚刚';
  }
};

// 计算支付有效期（下单时间+24小时）
const getPaymentDueTime = (dateStr) => {
  if (!dateStr) return '24小时内';
  
  try {
    const orderDate = new Date(dateStr);
    if (isNaN(orderDate.getTime())) {
      // 如果日期无效，返回24小时后的时间
      const dueDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
      const year = dueDate.getFullYear();
      const month = String(dueDate.getMonth() + 1).padStart(2, '0');
      const day = String(dueDate.getDate()).padStart(2, '0');
      const hours = String(dueDate.getHours()).padStart(2, '0');
      const minutes = String(dueDate.getMinutes()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
    
    // 支付有效期为24小时
    const dueDate = new Date(orderDate.getTime() + 24 * 60 * 60 * 1000);
    
    const year = dueDate.getFullYear();
    const month = String(dueDate.getMonth() + 1).padStart(2, '0');
    const day = String(dueDate.getDate()).padStart(2, '0');
    const hours = String(dueDate.getHours()).padStart(2, '0');
    const minutes = String(dueDate.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error('计算支付有效期错误', error);
    return '24小时内';
  }
};
</script>

<style scoped>
.payment-container {
  padding: 10px;
}

.order-info-section, .payment-method-section {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.order-info-section h3, .payment-method-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.order-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-row {
  display: flex;
  align-items: flex-start;
}

.info-row.payment-amount {
  grid-column: span 2;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.label {
  color: #606266;
  width: 80px;
  flex-shrink: 0;
}

.value {
  color: #303133;
  word-break: break-all;
}

.value.price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.value.time-limit {
  color: #e6a23c;
  font-weight: 500;
}

.value.address {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.payment-options {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.wallet-payment, .qrcode-payment {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-top: 10px;
}

.balance-info {
  margin-bottom: 10px;
  font-size: 16px;
}

.balance {
  font-weight: 600;
  color: #67c23a;
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.qrcode-image {
  width: 200px;
  height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.qrcode-tip {
  color: #909399;
  font-size: 14px;
}

.info-row.full-width {
  grid-column: span 2;
}
</style> 