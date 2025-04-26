<template>
  <div class="favorites-container">
    <h2 class="page-title">我的收藏</h2>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="favorites.length === 0" class="empty-state">
      <el-empty description="暂无收藏商品" />
      <el-button type="primary" @click="redirectToHome">去逛逛</el-button>
    </div>
    
    <div v-else class="favorites-list">
      <el-card v-for="item in favorites" 
               :key="item.favoriteID" 
               class="favorite-item">
        <div class="favorite-content" @click="viewItemDetail(item)">
          <div class="favorite-image">
            <img v-if="item.images && item.images.length > 0" 
                 :src="item.images[0].imageURL" 
                 alt="商品图片" />
            <el-empty v-else description="暂无图片" :image-size="50" />
          </div>
          
          <div class="favorite-info">
            <h3 class="favorite-title">{{ item.name }}</h3>
            <p class="favorite-desc">{{ item.description }}</p>
            <div class="favorite-meta">
              <el-tag type="danger">¥{{ item.price }}</el-tag>
              <el-tag type="info">{{ item.condition }}</el-tag>
              <span class="favorite-time">收藏于 {{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
        </div>
        
        <div class="favorite-actions">
          <el-button type="primary" @click="viewItemDetail(item)" size="small">查看详情</el-button>
          <el-button type="danger" @click="removeFavorite(item)" size="small">取消收藏</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const emits = defineEmits(['update-nav', 'get-item-from-display']);

const favorites = ref([]);
const loading = ref(true);
const user = ref({});

// 获取用户信息
const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      ElMessage.error("请先登录");
      return false;
    }

    const response = await axios.get("http://localhost:8080/getUserByUsername", {
      params: { username: userInfo.username },
    });
    user.value = response.data;
    return true;
  } catch (error) {
    console.error("获取用户信息失败", error);
    ElMessage.error("获取用户信息失败");
    return false;
  }
};

// 获取收藏列表
const getFavorites = async () => {
  loading.value = true;
  
  try {
    if (!user.value.userID) {
      const isLoggedIn = await getUserInfo();
      if (!isLoggedIn) {
        loading.value = false;
        return;
      }
    }
    
    const response = await axios.get("http://localhost:8080/favorite/list", {
      params: { userID: user.value.userID }
    });
    
    favorites.value = response.data.favorites || [];
  } catch (error) {
    console.error("获取收藏列表失败", error);
    ElMessage.error("获取收藏列表失败");
  } finally {
    loading.value = false;
  }
};

// 取消收藏
const removeFavorite = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏此商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await axios.post("http://localhost:8080/favorite/remove", null, {
      params: {
        userID: user.value.userID,
        itemID: item.itemID
      }
    });
    
    if (response.data.success) {
      ElMessage.success(response.data.message);
      // 从列表中移除该商品
      favorites.value = favorites.value.filter(fav => fav.favoriteID !== item.favoriteID);
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error("取消收藏失败", error);
      ElMessage.error("取消收藏失败");
    }
  }
};

// 查看商品详情
const viewItemDetail = (item) => {
  const itemData = {
    itemID: item.itemID,
    name: item.name,
    description: item.description,
    price: item.price,
    category: item.category,
    condition: item.condition,
    quantity: item.quantity,
    status: item.status,
    sellerID: item.sellerID,
    images: item.images
  };
  
  emits('get-item-from-display', itemData);
  emits('update-nav', '6');
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 跳转到首页
const redirectToHome = () => {
  emits('update-nav', '3');
};

onMounted(async () => {
  await getFavorites();
});
</script>

<style scoped>
.favorites-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 600;
}

.loading-container {
  padding: 40px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  border-radius: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.favorite-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.favorite-content {
  display: flex;
  gap: 16px;
  cursor: pointer;
}

.favorite-image {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 4px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.favorite-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.favorite-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #333;
}

.favorite-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.favorite-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: auto;
}

.favorite-time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

.favorite-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .favorite-content {
    flex-direction: column;
  }
  
  .favorite-image {
    width: 100%;
    height: 160px;
  }
  
  .favorite-meta {
    flex-wrap: wrap;
  }
  
  .favorite-time {
    margin-left: 0;
    margin-top: 8px;
    width: 100%;
  }
}
</style> 