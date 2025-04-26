<template>
  <div class="shop-wrapper">
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
          <div class="image-container">
            <img
              v-if="product.images.length > 0"
              :src="product.images[0].imageURL"
              class="product-image"
              alt="商品图片"
            />
            <el-empty v-else description="暂无照片" />
          </div>

          <div class="info-container">
            <div class="product-header">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-status">
                <el-tag 
                  :type="getStatusType(product)"
                  effect="plain"
                >
                  {{ getStatusText(product) }}
                </el-tag>
              </div>
            </div>

            <div class="product-description">
              {{ product.description }}
            </div>

            <div class="product-tags">
              <el-tag type="warning" effect="light" round>￥{{ product.price }}</el-tag>
              <el-tag type="info" effect="plain" round>{{ product.condition }}</el-tag>
              <el-tag type="success" effect="plain" round>{{ product.category }}</el-tag>
              <el-tag type="primary" effect="plain" round>库存: {{ product.quantity || 0 }}</el-tag>
            </div>

            <div class="product-actions" v-if="product.status !== '已售出'">
              <el-tooltip content="编辑商品" placement="top">
                <el-button 
                  type="primary" 
                  :icon="Edit" 
                  circle
                  @click="toggleEdit(product)"
                />
              </el-tooltip>
              <el-tooltip content="管理图片" placement="top">
                <el-button 
                  type="info" 
                  :icon="Picture" 
                  circle
                  @click="editImages(product)"
                />
              </el-tooltip>
<<<<<<< HEAD
              <el-tooltip :content="product.status === '已上架' ? '下架商品' : '上架商品'" placement="top">
                <el-button 
                  :type="product.status === '已上架' ? 'warning' : 'success'" 
                  :icon="product.status === '已上架' ? TakeawayBox : Sell" 
=======
              <el-tooltip :content="product.status === '已下架' ? '上架商品' : '下架商品'" placement="top">
                <el-button 
                  :type="product.status === '已下架' ? 'success' : 'warning'" 
                  :icon="product.status === '已下架' ? 'Upload' : 'Download'" 
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
                  circle
                  @click="toggleItemStatus(product)"
                />
              </el-tooltip>
              <el-tooltip content="删除商品" placement="top">
                <el-button 
                  type="danger" 
                  :icon="Delete" 
                  circle
                  @click="deleteProduct(product.itemID)"
                />
              </el-tooltip>
            </div>
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
          <el-form-item label="库存数量" label-width="80px" prop="quantity">
            <el-input-number v-model="insertForm.quantity" :min="1" :max="9999" />
          </el-form-item>
          <el-form-item label="商品图片" label-width="80px" prop="images">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="3"
              :on-exceed="handleExceed"
              :before-upload="beforeImageUpload"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip">
                  请上传jpg/png格式，单张不超过1M的图片（最多3张）
                </div>
              </template>
            </el-upload>
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
          <el-form-item label="库存数量" label-width="80px" prop="quantity">
            <el-input-number v-model="editForm.quantity" :min="1" :max="9999" />
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
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from "vue";
import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
<<<<<<< HEAD
import { Edit, Picture, Delete, Plus, Sell, TakeawayBox } from '@element-plus/icons-vue'
=======
import { Edit, Picture, Delete, Search, Upload, Download } from '@element-plus/icons-vue'
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
//控制添加弹窗
const dialogInsertFormVisible = ref(false);
const dialogEditFormVisible = ref(false);
const dialogEditImagesVisible = ref(false);
const user = reactive([]);
const items = reactive([]);

//第几页
const currentPage = ref(1);
//页幅
const pageSize = ref(6);
//总数
const totalItems = ref(0);

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
  quantity: 1,
});

const editForm = ref({
  itemID: "",
  name: "",
  description: "",
  price: "",
  category: "",
  condition: "",
  quantity: 1,
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
      "http://localhost:8080/getUserByUsername",
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
      "http://localhost:8080/userGetAllSellerItems",
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
  editForm.value.quantity = item.quantity || 1;
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
          "http://localhost:8080/deleteItemById?id=" + itemId
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
        "http://localhost:8080/userFindItemByName",
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

// 用于存储临时上传的文件
const uploadFiles = ref([]);

// 处理文件改变事件
const handleFileChange = (file) => {
  // 检查文件类型和大小
  const isJPGorPNG = file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt1M = file.raw.size / 1024 / 1024 < 1;
  
  if (!isJPGorPNG) {
    ElMessage.error("上传图片只能是 JPG/PNG 格式!");
    return false;
  }
  if (!isLt1M) {
    ElMessage.error("上传图片大小不能超过 1MB!");
    return false;
  }
  
  // 添加到临时文件列表
  const fileIndex = uploadFiles.value.findIndex(item => item.uid === file.uid);
  if(fileIndex === -1) {
    uploadFiles.value.push(file.raw);
  }
};

// 处理文件删除事件
const handleFileRemove = (file) => {
  const fileIndex = uploadFiles.value.findIndex(item => item.uid === file.uid);
  if(fileIndex !== -1) {
    uploadFiles.value.splice(fileIndex, 1);
  }
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
        // 1. 先创建商品记录
        const res = await axios.post(
          "http://localhost:8080/userInsertItem",
          insertForm.value
        );
        
        if (res.status === 200) {
          const itemID = res.data;
          
          // 2. 如果有上传图片，则上传图片
          if (uploadFiles.value && uploadFiles.value.length > 0) {
            const uploadPromises = uploadFiles.value.map(file => {
              const formData = new FormData();
              formData.append("file", file);
              formData.append("itemID", itemID);
              
              return axios.post(
                "http://localhost:8080/userUploadImages",
                formData,
                {
                  headers: {
                    "Content-Type": "multipart/form-data",
                  },
                }
              );
            });
            
            try {
              // 等待所有图片上传完成
              await Promise.all(uploadPromises);
              ElMessage.success("商品及图片上传成功！");
            } catch (error) {
              ElMessage.warning("商品添加成功，但部分图片上传失败！");
              console.error("图片上传错误:", error);
            }
          } else {
            ElMessage.success("商品添加成功！");
          }
          
          // 3. 重置表单和上传文件列表
          dialogInsertFormVisible.value = false;
          ref.resetFields();
          uploadFiles.value = [];
          
          // 4. 刷新商品列表
          currentPage.value = 1;
          getItems();
        } else {
          ElMessage.error("服务器错误！");
        }
      } catch (err) {
        ElMessage.error("商品添加失败，请重试！");
        console.error("添加商品错误:", err);
      }
    } else {
      ElMessage.error("表单校验不通过！");
      return false;
    }
  });
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
          "http://localhost:8080/userUpdateItem",
          editForm.value
        );
        if (res.data == "success") {
          editForm.value.itemID = "";
          editForm.value.name = "";
          editForm.value.price = "";
          editForm.value.category = "";
          editForm.value.description = "";
          editForm.value.condition = "";
          editForm.value.quantity = 1;
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
  quantity: [
    {
      required: true,
      message: "请输入库存数量",
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
          "http://localhost:8080/deleteImageById",
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
      "http://localhost:8080/getItemImagesByItemID",
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
      "http://localhost:8080/userUploadImages",
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

// 获取商品状态样式
const getStatusType = (product) => {
  if (product.status === '已售出' || product.quantity === 0) {
    return 'info';
  } else if (product.status === '已下架') {
    return 'danger';
  } else if (product.status === '待审核') {
    return 'warning';
  } else if (product.status === '审核不通过') {
    return 'danger';
  } else if (product.quantity <= 3) {
    return 'warning';
  } else {
    return 'success';
  }
};

// 获取商品状态文本
const getStatusText = (product) => {
  if (product.status === '已售出' || product.quantity === 0) {
    return '已售出';
  } else if (product.status === '已下架') {
    return '已下架';
  } else if (product.status === '待审核') {
    return '待审核';
  } else if (product.status === '审核不通过') {
    return '审核不通过';
  } else if (product.quantity <= 3) {
    return `库存紧张(${product.quantity})`;
  } else {
    return '在售中';
  }
};

<<<<<<< HEAD
// 切换商品上下架状态
const toggleItemStatus = (product) => {
  const newStatus = product.status === '已上架' ? '已下架' : '已上架';
  const actionText = product.status === '已上架' ? '下架' : '上架';
  
  ElMessageBox.confirm(`确定要${actionText}该商品吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const res = await axios.put(
          "http://localhost:8080/userUpdateItemStatus",
          {
            itemID: product.itemID,
            status: newStatus
          }
        );
        
        if (res.data === "success") {
          ElMessage.success(`商品${actionText}成功！`);
          // 刷新商品列表
          getItems();
        } else {
          ElMessage.error(`商品${actionText}失败！`);
        }
      } catch (error) {
        console.error(`商品${actionText}错误:`, error);
        ElMessage.error(`商品${actionText}失败，请重试！`);
      }
    })
    .catch(() => {
      // 用户取消操作，不做任何处理
    });
=======
// 上/下架商品
const toggleItemStatus = (product) => {
  const newStatus = product.status === '已下架' ? '已上架' : '已下架';
  const actionText = product.status === '已下架' ? '上架' : '下架';
  
  ElMessageBox.confirm(`确认${actionText}该商品吗？`, "提示", {
    confirmButtonText: `确认${actionText}`,
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/changeStatus`,
        {
          params: {
            item_id: product.itemID,
            status: newStatus
          }
        }
      );
      
      if (res.data === "status changed") {
        ElMessage.success(`商品${actionText}成功！`);
        getItems(); // 刷新商品列表
      } else {
        ElMessage.error(`商品${actionText}失败！`);
      }
    } catch (error) {
      console.error(`商品${actionText}请求出错:`, error);
      ElMessage.error(`商品${actionText}失败！`);
    }
  }).catch(() => {
    ElMessage.info("操作已取消");
  });
>>>>>>> 34b1b487329d4e7b745a7dcc11ed2f45af9627dd
};
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  margin-bottom: 20px;
  overflow: hidden;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-content {
  display: flex;
  padding: 20px;
  gap: 24px;
}

.image-container {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image:hover {
  transform: scale(1.05);
}

.info-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.product-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 8px 0;
  flex-grow: 1;
}

.product-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 8px 0;
}

.product-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-content {
    flex-direction: column;
  }

  .image-container {
    width: 100%;
    height: 240px;
  }
}

.shop_head {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-bottom: 4%;
  margin-top: 2%;
  gap: 20px;
}

.search-input {
  margin: 0;
  width: 300px;
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
  max-width: 100%;
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

.shop-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
}
</style>
