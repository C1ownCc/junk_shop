<template>
  <div class="shop_card">
    <div class="shop_head">
      <el-button type="primary" @click="handleAdd">添加商品</el-button>
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
    <div class="product-card" v-for="product in items" :key="product.id">
      <div class="product-content">
        <img
          v-if="product.images.length > 0"
          :src="product.images[0].imageURL"
          class="product-image"
          alt="商品图片"
        />
        <div v-else class="product-image">
          <el-empty description="暂无照片" />
        </div>

        <div class="info-container">
          <div class="info name">{{ product.name }}</div>
          <div class="info description">{{ product.description }}</div>
          <div class="info price">
            <el-tag type="warning" round>￥{{ product.price }}</el-tag>
            <el-tag type="warning" round>{{ product.condition }}</el-tag>
            <el-tag type="warning" round>{{ product.category }}</el-tag>
            <el-tag type="" round>￥{{ product.status }}</el-tag>
          </div>
        </div>
        <div class="actions" v-if="product.status != '已售出'">
          <el-icon
            class="action-icon edit icon-setting"
            @click="toggleEdit(product)"
          >
            <Edit />
          </el-icon>
          <el-icon
            class="action-icon delete icon-delete"
            @click="editImages(product)"
            ><Picture
          /></el-icon>
          <el-icon
            class="action-icon delete icon-delete"
            @click="deleteProduct(product.itemID)"
          >
            <Delete />
          </el-icon>
        </div>
      </div>
    </div>
    <div v-if="totalItems === 0"><el-empty description="暂无商品" /></div>
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
    <el-dialog v-model="dialogInsertFormVisible" title="上架商品">
      <el-form
        ref="insertFormRulesRef"
        :model="insertForm"
        :rules="insertFormRules"
      >
        <el-form-item label="商品名称" label-width="80px" prop="name">
          <el-input v-model="insertForm.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" label-width="80px" prop="description">
          <el-input v-model="insertForm.description" autocomplete="off" />
        </el-form-item>
        <el-form-item label="价格" label-width="80px" prop="price">
          <el-input v-model="insertForm.price" autocomplete="off" />
        </el-form-item>

        <el-form-item label="类别" label-width="80px" prop="category">
          <el-select v-model="insertForm.category" placeholder="请选择类别">
            <el-option label="服饰鞋帽" value="服饰鞋帽" />
            <el-option label="家居用品" value="家居用品" />
            <el-option label="电子数码" value="电子数码" />
            <el-option label="美妆护肤" value="美妆护肤" />
            <el-option label="食品生鲜" value="食品生鲜" />
            <el-option label="图书音像" value="图书音像" />
            <el-option label="儿童玩具" value="儿童玩具" />
            <el-option label="运动户外" value="运动户外" />
            <el-option label="汽车用品" value="汽车用品" />
            <el-option label="医疗保健" value="医疗保健" />
            <el-option label="工艺礼品" value="工艺礼品" />
            <el-option label="虚拟物品" value="虚拟物品" />
          </el-select>
        </el-form-item>
        <el-form-item label="成色" label-width="80px" prop="condition">
          <el-select v-model="insertForm.condition" placeholder="请选择成色">
            <el-option label="全新" value="全新" />
            <el-option label="99新" value="99新" />
            <el-option label="95新" value="95新" />
            <el-option label="9成新" value="9成新" />
            <el-option label="8成新" value="8成新" />
            <el-option label="6成新" value="6成新" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogInsertFormVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitItemInform(insertFormRulesRef)"
          >
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogEditFormVisible" title="编辑商品">
      <el-form
        ref="editFormRulesRef"
        :model="editForm"
        :rules="insertFormRules"
      >
        <el-form-item label="商品名称" label-width="80px" prop="name">
          <el-input v-model="editForm.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" label-width="80px" prop="description">
          <el-input v-model="editForm.description" autocomplete="off" />
        </el-form-item>
        <el-form-item label="价格" label-width="80px" prop="price">
          <el-input v-model="editForm.price" autocomplete="off" />
        </el-form-item>
        <el-form-item label="类别" label-width="80px" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择类别">
            <el-option label="服饰鞋帽" value="服饰鞋帽" />
            <el-option label="家居用品" value="家居用品" />
            <el-option label="电子数码" value="电子数码" />
            <el-option label="美妆护肤" value="美妆护肤" />
            <el-option label="食品生鲜" value="食品生鲜" />
            <el-option label="图书音像" value="图书音像" />
            <el-option label="儿童玩具" value="儿童玩具" />
            <el-option label="运动户外" value="运动户外" />
            <el-option label="汽车用品" value="汽车用品" />
            <el-option label="医疗保健" value="医疗保健" />
            <el-option label="工艺礼品" value="工艺礼品" />
            <el-option label="虚拟物品" value="虚拟物品" />
          </el-select>
        </el-form-item>
        <el-form-item label="成色" label-width="80px" prop="condition">
          <el-select v-model="editForm.condition" placeholder="请选择成色">
            <el-option label="全新" value="全新" />
            <el-option label="99新" value="99新" />
            <el-option label="95新" value="95新" />
            <el-option label="9成新" value="9成新" />
            <el-option label="8成新" value="8成新" />
            <el-option label="6成新" value="6成新" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogEditFormVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitItemEditForm(editFormRulesRef)"
          >
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogInsertImageVisible" title="请上传商品图片">
      <el-upload
        :file-list="fileList"
        class="upload-demo"
        :http-request="handleImageUpload"
        multiple
        :on-remove="handleRemove"
        :before-remove="beforeRemove"
        :limit="3"
        :on-exceed="handleExceed"
      >
        <el-button type="primary">点击上传</el-button>
        <template #tip>
          <div class="el-upload__tip">jpg/png 文件不超过1M。</div>
        </template>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeInsertImage">完成</el-button>
      </span>
    </el-dialog>

    <el-dialog v-model="dialogEditImagesVisible" title="商品图片">
      <div v-if="editImagesForm.length > 0" class="images-box">
        <div
          v-for="image in editImagesForm"
          :key="image.imageID"
          class="images"
        >
          <el-image :src="image.imageURL"> </el-image>
          <div>
            <el-icon class="action-icon delete icon-delete"
              ><Delete @click="deleteImage(image)"
            /></el-icon>
          </div>
        </div>
      </div>
      <div v-else>
        <p>此商品未上传相应图片</p>
      </div>
      <div class="button">
        <el-button @click="editImagesFormClose">完成</el-button>
        <el-upload
          v-if="editImagesForm.length <= 3"
          :show-file-list="false"
          :http-request="handleImageEdit"
          :before-upload="beforeImageUpload"
        >
          <el-button type="primary">添加图片</el-button>
        </el-upload>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from "vue";
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
//控制添加弹窗
const dialogInsertFormVisible = ref(false);
const dialogEditFormVisible = ref(false);
const dialogEditImagesVisible = ref(false);
const dialogInsertImageVisible = ref(false);
const user = reactive([]);
const items = reactive([]);

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
//总数
const totalItems = ref(0);

const insertItemID = ref("");
const changeItemID = ref("");
//搜索数据
const searchQuery = ref("");

const editImagesForm = ref([]);

//添加表单
const insertForm = ref({
  sellerID: "",
  name: "",
  description: "",
  price: "",
  category: "",
  condition: "",
});

const editForm = ref({
  itemID: "",
  name: "",
  description: "",
  price: "",
  category: "",
  condition: "",
});

const fileList = ref([]);

//判断表单是否校验成功
const insertFormRulesRef = ref(null);
const editFormRulesRef = ref(null);

//获取用户信息
const getUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.username) {
      console.error(
        "无法获取用户信息，用户未登录或 localStorage 中无用户数据。"
      );
      return;
    }

    const response = await axios.get(
      "http://192.168.1.112:8080/getUserByUsername",
      {
        params: { username: userInfo.username },
      }
    );
    user.value = response.data;
    getItems();
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

onMounted(getUserInfo);

const getItems = async () => {
  try {
    const res = await axios.get(
      "http://192.168.1.112:8080/userGetAllSellerItems",
      {
        params: {
          page: currentPage.value - 1,
          size: pageSize.value,
          sellerID: user.value.userID,
        },
      }
    );
    items.splice(0, items.length, ...res.data.items);
    totalItems.value = res.data.total;
  } catch (err) {
    console.log("获取商品信息失败！", err);
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
// 控制编辑框的显示与隐藏
const toggleEdit = (item) => {
  editForm.value.itemID = item.itemID;
  editForm.value.name = item.name;
  editForm.value.price = item.price;
  editForm.value.category = item.category;
  editForm.value.description = item.description;
  editForm.value.condition = item.condition;
  dialogEditFormVisible.value = true;
};

//删除商品deleteProduct
const deleteProduct = (itemId) => {
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
          ElMessage.error("商品不存在！");
        } else {
          ElMessage.error("删除失败！");
        }
      } catch (error) {
        alert("请求错误:", error);
        console.error("删除失败:", error);
        // 可以显示一些错误信息
      }
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
  // 删除商品逻辑
};

//搜索商品
const onSearch = async () => {
  if (searchQuery.value === null || searchQuery.value === "") {
    getItems();
  } else {
    try {
      const response = await axios.get(
        "http://192.168.1.112:8080/userFindItemByName",
        {
          params: {
            sellerID: user.value.userID,
            name: searchQuery.value,
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
  }
};

//添加商品
const handleAdd = () => {
  dialogInsertFormVisible.value = true;
};

//提交商品信息
const submitItemInform = (ref) => {
  insertForm.value.sellerID = user.value.userID;
  if (!ref) {
    return;
  }
  ref.validate(async (valid) => {
    if (valid) {
      try {
        const res = await axios.post(
          "http://192.168.1.112:8080/userInsertItem",
          insertForm.value
        );
        if (res.status === 200) {
          ElMessage.success("信息添加成功!请上传商品图片！");
          dialogInsertFormVisible.value = false;
          dialogInsertImageVisible.value = true;
          ref.resetFields();
          insertItemID.value = res.data;
        } else {
          ElMessage.error("服务器错误！");
        }
      } catch (err) {
        ElMessage.error("表单校验不通过！", err);
      }
    } else {
      ElMessage.error("表单校验不通过！");
      return false;
    }
  });
};

//提交商品图片
const handleImageUpload = async (file) => {
  const formData = new FormData();
  formData.append("file", file.file); // 添加上传的文件
  formData.append("itemID", insertItemID.value); // 假设 itemID 是一个响应式引用，保存着当前商品的ID

  try {
    const res = await axios.post(
      "http://192.168.1.112:8080/userUploadImages",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    currentPage.value = 1;
    onSearch();
  } catch (error) {
    ElMessage.error("上传图片失败！", error);
  }
};

//提交编辑表单
const submitItemEditForm = (ref) => {
  if (!ref) {
    ElMessage.error("请检查表单是否填写完整！");
    return;
  }
  ref.validate(async (valid) => {
    if (valid) {
      try {
        const res = await axios.put(
          "http://192.168.1.112:8080/userUpdateItem",
          editForm.value
        );
        if (res.data == "success") {
          editForm.value.itemID = "";
          editForm.value.name = "";
          editForm.value.price = "";
          editForm.value.category = "";
          editForm.value.description = "";
          editForm.value.condition = "";
          ElMessage.success("修改成功！");
          dialogEditFormVisible.value = false;
          currentPage.value = 1;
          onSearch();
        } else {
          alert("修改失败！");
          ElMessage.error("修改失败！");
        }
      } catch (err) {
        ElMessage.error("请求错误！", err);
      }
    } else {
      ElMessage.error("请检查表单是否填写完整！");
    }
  });
  console.log(editForm.value);
};

//自定义价格校验
const validatePrice = (rule, value, callback) => {
  if (!value) return callback(new Error("请输入金额"));
  if (!Number(value)) return callback(new Error("请输入数字值"));
  // let reg = /^(([1-9]{1}\d*)|(0{1}))(\.\d{0,2})$/g; //小数限制0-2位
  let reg = /((^[1-9]\d*)|^0)(\.\d{0,2}){0,1}$/;
  if (!reg.test(value)) return callback(new Error("请输入正确价格"));
  callback();
};

//表单校验
const insertFormRules = ref({
  name: [
    {
      required: true,
      message: "商品名称不能为空",
      trigger: "blur",
    },
  ],
  description: [
    {
      required: true,
      message: "商品描述不能为空",
      trigger: "blur",
    },
  ],
  price: [
    {
      required: true,
      validator: validatePrice,
      trigger: "blur",
    },
  ],
  category: [
    {
      required: true,
      message: "请选择商品类别",
      trigger: "change",
    },
  ],
  condition: [
    {
      required: true,
      message: "请选择商品成色",
      trigger: "change",
    },
  ],
});

const handleRemove = (file, uploadFiles) => {
  //这里添加删除逻辑
  console.log(file, uploadFiles);
};

const handleExceed = (files, uploadFiles) => {
  ElMessage.warning("最多上传三张图片！");
};

const beforeRemove = (uploadFile, uploadFiles) => {
  return ElMessageBox.confirm(
    `Cancel the transfer of ${uploadFile.name} ?`
  ).then(
    () => true,
    () => false
  );
};

const editImages = (item) => {
  changeItemID.value = item.itemID;
  editImagesForm.value = item.images;
  dialogEditImagesVisible.value = true;
};

const editImagesFormClose = () => {
  currentPage.value = 1;
  onSearch();
  dialogEditImagesVisible.value = false;
  // editImagesForm.value = null;
};

const deleteImage = (item) => {
  ElMessageBox.confirm("确定删除此图片吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const res = await axios.delete(
          "http://192.168.1.112:8080/deleteImageById",
          {
            params: {
              imageID: item.imageID,
            },
          }
        );
        if (res.data === "delete success") {
          const newImages = await getItemImagesByItemID(item.itemID);
          if (newImages && Array.isArray(newImages)) {
            editImagesForm.value = [...newImages]; // 使用展开运算符创建新数组
          }
          ElMessage.success("删除成功！！！");
        } else {
          ElMessage.error("删除失败！！！");
        }
      } catch (err) {
        ElMessage.error("请求错误！", err);
      }
    })
    .catch(() => {
      // 用户点击取消或关闭弹窗，不执行任何操作
    });
};

const getItemImagesByItemID = async (itemID) => {
  try {
    const res = await axios.get(
      "http://192.168.1.112:8080/getItemImagesByItemID",
      {
        params: {
          itemID: itemID,
        },
      }
    );
    return res.data;
  } catch (err) {
    console.log("重新获取当前图片列表失败！");
  }
};

const beforeImageUpload = (file) => {
  const isJPGorPNG = file.type === "image/jpeg" || file.type === "image/png";
  if (!isJPGorPNG) {
    ElMessage.error("上传图片只能是 JPG/PNG 格式!");
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 1;
  if (!isLt2M) {
    ElMessage.error("上传图片大小不能超过 1MB!");
    return false;
  }
  return isJPGorPNG && isLt2M;
};

const handleImageEdit = async (file) => {
  if (editImagesForm.value.length >= 3) {
    ElMessage.error("最多上传三张图片！");
    return;
  }
  const formData = new FormData();
  formData.append("file", file.file); // 添加上传的文件
  formData.append("itemID", changeItemID.value); // 假设 itemID 是一个响应式引用，保存着当前商品的ID

  try {
    const res = await axios.post(
      "http://192.168.1.112:8080/userUploadImages",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    if (res.data === "Upload successful") {
      const newImages = await getItemImagesByItemID(changeItemID.value);
      if (newImages && Array.isArray(newImages)) {
        editImagesForm.value = [...newImages]; // 使用展开运算符创建新数组
      }
      ElMessage.success("上传图片成功！");
    }
  } catch (error) {
    ElMessage.error("上传图片失败！", error);
  }

  watch(editImagesForm, getItems);
};

const closeInsertImage = () => {
  currentPage.value = 1;
  onSearch();
  dialogInsertImageVisible.value = false;
};
</script>

<style scoped>
.product-card {
  display: flex;
  align-items: flex-start;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 10px;
  margin-bottom: 20px;
  min-width: 800px;
}

.product-content {
  display: flex;
  width: 100%;
}

.product-image {
  border-radius: 10px;
  width: 200px; /* 设定固定宽度 */
  height: 300px; /* 设定固定高度 */
  object-fit: cover;
  margin-right: 10px;
  padding: 20px;
}

.info-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-grow: 1;
  padding-top: 20px;
  padding-bottom: 20px;
}

.info {
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  background: #fff; /* 为了提高可读性，添加背景颜色 */
  flex-grow: 1;
}

.info:last-child {
  margin-bottom: 0;
}

.actions {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 垂直居中 */
  align-items: center; /* 水平居中 */
  padding-left: 30px; /* 与其他信息框的间隔 */
  padding-right: 20px; /* 与其他信息框的间隔 */
}

.action-icon {
  cursor: pointer;
  color: #409eff;
  font-size: 24px; /* 调整图标大小 */
  margin-bottom: 20px; /* 图标之间的间距 */
}

.action-icon:last-child {
  margin-bottom: 0; /* 最后一个图标没有底部边距 */
}

.name {
  /* min-height: 10%; */
  max-height: 30px;
}

.price {
  max-height: 20px;
}

/* 适应响应式设计 */
@media (max-width: 768px) {
  .product-card {
    flex-direction: column;
  }

  .product-content {
    flex-direction: column;
  }

  .info-container {
    width: 100%; /* 小屏幕时宽度为100% */
  }

  .info {
    margin-bottom: 10px;
    flex-grow: 0;
    height: auto; /* 小屏幕时高度自适应 */
  }
}

.shop_head {
  display: flex;
  width: 50%;
  margin-bottom: 4%;
  margin-top: 2%;
}

.search-input {
  margin-right: 5%;
  margin-left: 10%;
}

.icon-setting:hover,
.icon-delete:hover {
  color: #42b983; /* 鼠标悬停时的颜色 */
  transform: scale(1.2); /* 鼠标悬停时变大 */
}

.pagination-wrapper {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 根据需要添加一些顶部外边距 */
}

.shop_card {
  width: 1000px;
}

.images-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.images {
  display: flex;
  max-width: 500px;
  margin: 5px;
  margin-bottom: 10px;
  align-items: center;
}

.button {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.description {
  max-width: 800px;
}
</style>
