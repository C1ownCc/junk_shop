<template>
  <div class="items-management">
    <div class="manager_head">
      <el-input
        v-model="searchQuery"
        placeholder="请输入消息内容以搜索消息"
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
      <el-table-column prop="messageID" label="消息ID"></el-table-column>
      <el-table-column prop="fromUserID" label="发送者ID"></el-table-column>
      <el-table-column prop="toUserID" label="接收者ID"></el-table-column>
      <el-table-column prop="content" label="消息内容"></el-table-column>
      <el-table-column prop="sentAt" label="发送时间"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-icon class="icon-delete"
            ><Delete @click="handleDelete(scope.row.messageID)"
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
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from "vue";
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
const searchQuery = ref("");

const handleDelete = (itemId) => {
  // console.log(userId);
  ElMessageBox.confirm("确认删除该信息吗?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const response = await axios.delete(
          "http://192.168.1.112:8080/deleteMessageByID?messageID=" + itemId
        );
        if (response.data === "success") {
          // 删除成功后从 users 中移除该用户
          getItems();
          ElMessage.success("信息删除成功！");
        } else if (response.data === "error") {
          // 处理后端返回的删除失败情况
          ElMessage.error("信息删除失败！");
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
      "http://192.168.1.112:8080/getAllMessages",
      {
        params: {
          content: searchQuery.value,
          page: currentPage.value - 1, // 分页索引从0开始
          size: pageSize.value,
        },
      }
    );
    items.splice(0, items.length, ...response.data.messages); // 更新用户列表
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
  if (searchQuery.value === null || searchQuery.value === "") {
    getItems();
  } else {
    try {
      const response = await axios.get(
        "http://192.168.1.112:8080/getAllMessages",
        {
          params: {
            content: searchQuery.value,
            page: currentPage.value - 1, // 分页索引从0开始
            size: pageSize.value,
          },
        }
      );
      items.splice(0, items.length, ...response.data.messages); // 更新用户列表
      // console.log(response.data);
      totalItems.value = response.data.total; // 更新总用户数
    } catch (err) {
      console.log("查询用户错误！", err);
    }
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
  margin-left: 10%;
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
