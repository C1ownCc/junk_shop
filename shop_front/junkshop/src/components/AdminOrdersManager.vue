<template>
  <div class="items-management">
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
        @clear="onSearch"
        @keyup.enter="onSearch"
      >
        <template #suffix>
          <el-icon class="search-icon" size="150%" @click="onSearch">
            <Search />
          </el-icon>
        </template>
      </el-input>
    </div>
    <el-table :data="orders">
      <el-table-column prop="orderID" label="订单ID"></el-table-column>
      <el-table-column prop="buyerID" label="买家ID"></el-table-column>
      <el-table-column prop="sellerID" label="卖家ID"></el-table-column>
      <el-table-column prop="itemID" label="物品ID"></el-table-column>
      <el-table-column prop="itemName" label="商品名称"></el-table-column>
      <el-table-column prop="price" label="单价"></el-table-column>
      <el-table-column prop="orderStatus" label="订单状态"></el-table-column>
      <el-table-column
        prop="recipientName"
        label="收件人昵称"
      ></el-table-column>
      <el-table-column prop="address" label="订单地址"></el-table-column>
      <el-table-column prop="phoneNumber" label="联系电话"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间"></el-table-column>
      <el-table-column prop="completedAt" label="完成时间"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <!-- <el-icon class="icon-setting"
            ><ZoomIn @click="handleReview(scope.row)"
          /></el-icon> -->
          <el-icon class="icon-delete"
            ><Delete @click="handleDelete(scope.row.itemID)"
          /></el-icon>
        </template>
      </el-table-column>
    </el-table>
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
    <!-- <el-dialog v-model="dialogFormVisible" title="商品图片">
      <div v-if="itemImages.length > 0" class="images-box">
        <div v-for="image in itemImages" :key="image.imageID" class="images">
          <el-image :src="image.imageURL"> </el-image>
        </div>
      </div>
      <div v-else>
        <p>此商品未上传相应图片</p>
      </div>
      <div class="button">
        <el-radio-group v-model="changeStatus" size="large">
          <el-radio-button label="待审核" />
          <el-radio-button label="审核通过" />
          <el-radio-button label="审核不通过" />
          <el-radio-button label="已上架" />
          <el-radio-button label="已售出" />
          <el-radio-button label="下架" />
        </el-radio-group>
        <div>
          <el-button type="primary" @click="submitChangeStatus()" round
            >提交审核结果</el-button
          >
          <el-button @click="changeStatusClose()" round>关闭</el-button>
        </div>
      </div>
    </el-dialog> -->
  </div>
</template>

<script setup>
import { watch, reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
const searchQuery = ref("");
const searchStatus = ref("全部");

const handleDelete = (id) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该商品吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      console.log(id);
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
};

const orders = reactive([]);
const total = ref(0);
const getOrders = async () => {
  // console.log(user);
  try {
    const res = await axios.get("http://192.168.1.112:8080/getOrders", {
      params: {
        itemName: searchQuery.value,
        status: searchStatus.value,
        size: pageSize.value,
        page: currentPage.value - 1,
      },
    });
    orders.splice(0, orders.length, ...res.data.orders);
    if (res.data.total > 0) {
      total.value = res.data.total;
    }
    console.log("res--------------", orders.values);
  } catch (error) {
    ElMessage.error("获取订单列表失败!", error);
  }
};
onMounted(getOrders);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getItems();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getItems();
};

watch(searchStatus, getOrders);

//搜索
const onSearch = async () => {};
</script>

<style scoped>
.items-management {
  margin: 20px;
}

.filter-input {
  margin-bottom: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 根据需要添加一些顶部外边距 */
}

.search-input {
  margin-right: 10%;
  margin-left: 1%;
  width: 50%;
}
.manager_head {
  display: flex;
  width: 100%;
}

.icon-setting,
.icon-delete {
  cursor: pointer; /* 鼠标悬停时变成手形 */
  color: #000; /* 默认颜色 */
  font-size: 20px; /* 图标大小 */
  margin-right: 10px; /* 图标间距 */
}

.icon-setting:hover,
.icon-delete:hover {
  color: #42b983; /* 鼠标悬停时的颜色 */
  transform: scale(1.2); /* 鼠标悬停时变大 */
}

.images-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.images {
  max-width: 400px;
  margin: 5px;
}

.button {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
</style>
