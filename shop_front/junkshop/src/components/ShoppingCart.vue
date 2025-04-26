<template>
  <div class="shopping-cart-container">
    <!-- 购物车内容 -->
    <div v-if="!showCheckout">
      <div class="cart-header">
        <h2 class="cart-title">我的购物车</h2>
        <div class="cart-actions">
          <el-button type="primary" @click="handleCheckout" :disabled="selectedItems.length === 0">
            <el-icon><ShoppingBag /></el-icon>结算
          </el-button>
          <el-button type="danger" @click="clearCart" :disabled="cartItems.length === 0">
            <el-icon><Delete /></el-icon>清空购物车
          </el-button>
        </div>
      </div>

      
      <!-- 购物车内容 -->
      <div class="cart-content">
        <el-table
          v-if="cartItems.length > 0"
          :data="cartItems"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="45" :selectable="row => !row.outOfStock" />
          <el-table-column label="商品图片" width="80">
            <template #default="{ row }">
              <div class="item-image">
                <img 
                  v-if="row.images && row.images.length > 0" 
                  :src="row.images[0].imageURL" 
                  alt="商品图片" 
                />
                <el-empty v-else description="暂无图片" :image-size="40" />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="itemName" label="商品名称" min-width="120">
            <template #default="{ row }">
              <div class="item-name">
                <el-tooltip 
                  :content="row.itemName" 
                  placement="top" 
                  :show-after="500"
                  :enterable="false"
                >
                  <span @click="viewItemDetails(row)" class="name-text">{{ row.itemName }}</span>
                </el-tooltip>
                <el-tag v-if="row.outOfStock" type="danger" size="small" effect="plain" class="stock-tag">库存不足</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="80">
            <template #default="{ row }">
              <span class="item-price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="100">
            <template #default="{ row }">
              <div class="quantity-control">
                <el-button 
                  size="small" 
                  @click="decreaseQuantity(row)" 
                  :disabled="row.outOfStock || row.quantity <= 1"
                  class="quantity-btn"
                >-</el-button>
                <span class="quantity-display">{{ row.quantity }}</span>
                <el-button 
                  size="small" 
                  @click="increaseQuantity(row)" 
                  :disabled="row.outOfStock || row.quantity >= 10"
                  class="quantity-btn"
                >+</el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="80">
            <template #default="{ row }">
              <span class="item-total">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                size="small" 
                @click="removeItem(row.cartItemID)"
                text
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空购物车状态 -->
        <el-empty 
          v-else 
          description="购物车还是空的" 
          :image-size="200" 
        >
          <el-button type="primary" @click="goShopping">去购物</el-button>
        </el-empty>
      </div>

      <!-- 结算区域 -->
      <div class="cart-footer" v-if="cartItems.length > 0">
        <div class="cart-summary">
          <div class="summary-item">
            <span>已选商品:</span>
            <span>{{ selectedItems.length }} 件</span>
          </div>
          <div class="summary-item">
            <span>合计金额:</span>
            <span class="total-price">¥{{ calculateTotal() }}</span>
          </div>
        </div>
        <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedItems.length === 0">
          结算
        </el-button>
      </div>
    </div>
    
    <!-- 订单确认页面 -->
    <div v-else>
      <CheckoutOrder 
        :items="selectedItems" 
        @back="showCheckout = false"
        @order-submitted="handleOrderSubmitted"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { Delete, ShoppingBag } from '@element-plus/icons-vue';
import CheckoutOrder from './CheckoutOrder.vue';

const emits = defineEmits(['update-nav', 'on-checkout', 'view-item']);

// 购物车数据
const cartItems = ref([]);
const selectedItems = ref([]);
const loading = ref(false);
const userId = ref(0);
// 是否显示结算页面
const showCheckout = ref(false);

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
    userId.value = response.data.userID;
    fetchCartItems();
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

// 获取购物车中的商品
const fetchCartItems = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/cart/contents`, {
      params: { userId: userId.value }
    });
    if (response.data.success) {
      // 获取商品列表
      let items = response.data.items || [];
      
      // 确保每个商品都有完整信息
      const itemsWithDetails = await Promise.all(items.map(async (item) => {
        // 检查库存状态
        if (item.quantity <= 0 || item.status === "已售出") {
          item.outOfStock = true;
        } else {
          item.outOfStock = false;
        }
        
        // 如果缺少必要信息，尝试获取完整商品数据
        if (!item.name || !item.sellerID) {
          try {
            // 使用正确的商品详情API
            const itemResponse = await axios.get(`http://localhost:8080/getItemById`, {
              params: { id: item.itemID }
            });
            if (itemResponse.data) {
              // 合并数据，保留购物车中的数量信息
              return {
                ...item,
                ...itemResponse.data,
                quantity: item.quantity,
                cartItemID: item.cartItemID,
                outOfStock: item.outOfStock
              };
            }
          } catch (error) {
            console.error(`获取商品ID ${item.itemID} 详情失败:`, error);
          }
        }
        return item;
      }));
      
      cartItems.value = itemsWithDetails;
    } else {
      ElMessage.error(response.data.message || '获取购物车失败');
    }
  } catch (error) {
    console.error('获取购物车出错:', error);
    ElMessage.error('获取购物车时发生错误');
  } finally {
    loading.value = false;
  }
};

// 更新商品数量
const updateQuantity = async (cartItemId, quantity) => {
  try {
    const response = await axios.put(`http://localhost:8080/cart/update`, null, {
      params: {
        cartItemId: cartItemId,
        quantity: quantity
      }
    });
    if (response.data.success) {
      ElMessage.success(response.data.message || '数量已更新');
    } else {
      ElMessage.error(response.data.message || '更新数量失败');
      fetchCartItems(); // 刷新购物车
    }
  } catch (error) {
    console.error('更新数量出错:', error);
    ElMessage.error('更新数量时发生错误');
    fetchCartItems(); // 刷新购物车
  }
};

// 从购物车中移除商品
const removeItem = async (cartItemId) => {
  try {
    const response = await axios.delete(`http://localhost:8080/cart/remove`, {
      params: { cartItemId: cartItemId }
    });
    if (response.data.success) {
      ElMessage.success(response.data.message || '商品已移除');
      fetchCartItems(); // 刷新购物车
    } else {
      ElMessage.error(response.data.message || '移除商品失败');
    }
  } catch (error) {
    console.error('移除商品出错:', error);
    ElMessage.error('移除商品时发生错误');
  }
};

// 清空购物车
const clearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.delete(`http://localhost:8080/cart/clear`, {
        params: { userId: userId.value }
      });
      if (response.data.success) {
        ElMessage.success(response.data.message || '购物车已清空');
        fetchCartItems(); // 刷新购物车
      } else {
        ElMessage.error(response.data.message || '清空购物车失败');
      }
    } catch (error) {
      console.error('清空购物车出错:', error);
      ElMessage.error('清空购物车时发生错误');
    }
  }).catch(() => {});
};

// 处理选择变化
const handleSelectionChange = (items) => {
  selectedItems.value = items;
};

// 计算总金额
const calculateTotal = () => {
  return selectedItems.value
    .reduce((total, item) => total + item.price * item.quantity, 0)
    .toFixed(2);
};

// 跳转到商品市场
const goShopping = () => {
  emits('update-nav', '1');
};

// 查看商品详情
const viewItemDetails = (item) => {
  emits('view-item', item);
};

// 处理结算
const handleCheckout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品');
    return;
  }
  
  // 检查是否选择了库存不足的商品
  const outOfStockItems = selectedItems.value.filter(item => item.outOfStock);
  if (outOfStockItems.length > 0) {
    ElMessage.warning('您选中了库存不足的商品，请先移除');
    return;
  }
  
  // 显示结算页面
  showCheckout.value = true;
};

// 处理订单提交成功
const handleOrderSubmitted = (orderIds) => {
  // 刷新购物车数据
  fetchCartItems();
  
  // 如果需要跳转到订单页面
  emits('update-nav', '4'); // 跳转到"我的订单"页面
  
  // 隐藏结算页面
  showCheckout.value = false;
};

// 减少数量
const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    const newQuantity = item.quantity - 1;
    updateQuantity(item.cartItemID, newQuantity);
  }
};

// 增加数量
const increaseQuantity = (item) => {
  if (item.quantity < 10) {
    const newQuantity = item.quantity + 1;
    updateQuantity(item.cartItemID, newQuantity);
  }
};

// 组件挂载时获取购物车数据
onMounted(getUserInfo);
</script>

<style scoped>
.shopping-cart-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 24px;
  max-width: 1000px; /* 限制最大宽度 */
  margin: 0 auto; /* 居中显示 */
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.cart-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.cart-actions {
  display: flex;
  gap: 12px;
}

.cart-content {
  margin-bottom: 24px;
}

.item-image {
  width: 60px;
  height: 60px;
  overflow: hidden;
  border-radius: 4px;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-name {
  color: #1e293b;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.item-name .name-text {
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}

.item-name:hover {
  color: #3b82f6;
}

.item-name:hover .name-text {
  text-decoration: underline;
}

.stock-tag {
  margin-left: 8px;
  font-size: 10px;
}

.item-price {
  color: #64748b;
  font-weight: 500;
}

.item-total {
  color: #ef4444;
  font-weight: 600;
  padding-left: 5px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.cart-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
}

.total-price {
  font-size: 20px;
  font-weight: 600;
  color: #ef4444;
}

/* 自定义数量控制样式 */
.quantity-control {
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn {
  min-width: 24px;
  height: 24px;
  padding: 0;
  margin: 0;
  font-size: 12px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-display {
  width: 24px;
  text-align: center;
  font-size: 14px;
  margin: 0 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 16px;
  }
  
  .cart-summary {
    width: 100%;
  }
}
</style> 