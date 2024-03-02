<template>
  <div class="items-management">
    <div class="manager_head">
      <el-radio-group v-model="searchStatus">
        <el-radio-button label="全部" />
        <el-radio-button label="待审核" />
        <el-radio-button label="审核通过" />
        <el-radio-button label="审核不通过" />
        <el-radio-button label="已上架" />
        <el-radio-button label="已售出" />
        <el-radio-button label="下架" />
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
    <el-table :data="items" style="width: 100%">
      <el-table-column prop="itemID" label="物品ID"></el-table-column>
      <el-table-column prop="sellerID" label="商家ID"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="price" label="价格"></el-table-column>
      <el-table-column prop="category" label="类别"></el-table-column>
      <el-table-column prop="condition" label="成色"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间"></el-table-column>
      <el-table-column prop="updatedAt" label="更新时间"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-icon class="icon-setting"
            ><ZoomIn @click="handleReview(scope.row)"
          /></el-icon>
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
        :total="totalItems"
      >
      </el-pagination>
    </div>
    <el-dialog v-model="dialogFormVisible" title="商品图片">
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
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "axios";

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
//总数
const totalItems = ref(0);
//商品列表
const items = reactive([{}]);
const dialogFormVisible = ref(false);
const itemImages = ref([]);
const changeStatus = ref("");
const itemIDWithStatus = ref("");
const searchQuery = ref("");
const searchStatus = ref("全部");

const handleReview = async (item) => {
  itemIDWithStatus.value = item.itemID;
  dialogFormVisible.value = true;
  try {
    const res = await axios.get(
      "http://192.168.1.112:8080/getItemImagesByItemID",
      {
        params: {
          itemID: item.itemID,
        },
      }
    );
    itemImages.value = res.data;
  } catch (err) {
    ElMessage.error("请求错误！", err);
  }
};

const handleDelete = (itemId) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该商品吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await axios.delete(
          "http://192.168.1.112:8080/deleteItemById?id=" + itemId
        );
        if (response.data === "item deleted") {
          // 删除成功后从 users 中移除该用户
          getItems();
          ElMessage.success("商品删除成功！");
        } else if (response.data === "item delete error") {
          // 处理后端返回的删除失败情况
          ElMessage.error("商品删除失败！");
        } else {
          ElMessage.error("请求错误！");
        }
      } catch (error) {
        ElMessage.error("请求错误:", error);
        console.error("请求错误:", error);
        // 可以显示一些错误信息
      }
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
  // 删除商品逻辑
};

// 获取所有用户请求（带分页）
const getItems = async () => {
  try {
    const response = await axios.get(
      "http://192.168.1.112:8080/adminGetAllItems",
      {
        params: {
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );
    items.splice(0, items.length, ...response.data.items); // 更新用户列表
    // console.log(response.data);
    totalItems.value = response.data.total; // 更新总用户数
  } catch (error) {
    console.error("Error fetching users:", error);
  }
};

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  getItems();
};

const handleCurrentChange = (newCurrent) => {
  currentPage.value = newCurrent;
  getItems();
};

//搜索
const onSearch = async () => {
  console.log("搜索....", searchQuery.value);

  try {
    const response = await axios.get(
      "http://192.168.1.112:8080/findItemsByName",
      {
        params: {
          name: searchQuery.value,
          status: searchStatus.value,
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );
    items.splice(0, items.length, ...response.data.items); // 更新用户列表
    // console.log(response.data);
    totalItems.value = response.data.total; // 更新总用户数
  } catch (err) {
    console.log("查询用户错误！", err);
  }
};

watch(searchStatus, onSearch);

const changeStatusClose = () => {
  itemIDWithStatus.value = "";
  dialogFormVisible.value = false;
  changeStatus.value = "";
};

const submitChangeStatus = async () => {
  if (
    changeStatus.value === "" ||
    changeStatus.value === null ||
    itemIDWithStatus === "" ||
    itemIDWithStatus === null
  ) {
    ElMessage.error("请选择审核状态！");
    return;
  }
  try {
    const res = await axios.put("http://192.168.1.112:8080/adminUpdateStatus", {
      status: changeStatus.value,
      itemID: itemIDWithStatus.value,
    });
    if (res.data === "status updated") {
      ElMessage.success("审核状态已更新！");
      changeStatusClose();
      getItems();
    } else {
      ElMessage.error("审核状态更新失败！");
    }
  } catch (err) {
    ElMessage.error("请求错误！", err);
  }
};

//组件加载时调用
onMounted(getItems);
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
