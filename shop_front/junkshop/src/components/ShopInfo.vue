<template>
  <div>
    <el-card class="product-details-card">
      <el-icon class="backIcon"><ArrowLeft @click="goBack" /></el-icon>
      <div
        class="product-image-container"
        v-for="image in props.selectedItem.images"
        :key="image.imageID"
      >
        <img
          :src="image.imageURL"
          v-if="image.imageURL != null"
          alt="商品图片"
          class="product-image"
        />
        <div class="product-image" v-else>
          <el-empty description="此商品暂无图片" :image-size="50" />
        </div>
      </div>
      <div class="product-details-container">
        <h2 class="product-name">{{ props.selectedItem.name }}</h2>
        <h3 class="product-price">
          ￥{{ props.selectedItem.price }} <el-divider direction="vertical" />{{
            props.selectedItem.condition
          }}
        </h3>
        <el-divider content-position="left">简介</el-divider>
        <p>{{ props.selectedItem.description }}</p>
        <el-divider content-position="left">操作</el-divider>
        <div class="icon_box">
          <el-button type="primary" class="buy-now-btn" @click="buyNow"
            >立即购买</el-button
          >
          <el-button
            type="info"
            class="contact-seller-btn"
            @click="contactSeller"
            >联系商家</el-button
          >
        </div>
      </div>
    </el-card>
    <el-dialog v-model="dialogVisible" title="请确认订单信息" width="50%">
      <el-form
        label-width="100px"
        :model="order"
        style="max-width: 460px"
        class="el-form"
      >
        <el-form-item label="商品名称：">
          <el-text tag="b">{{ order.itemName }}</el-text>
        </el-form-item>
        <el-form-item label="商品图片：">
          <div
            class="orderImage"
            v-for="image in props.selectedItem.images"
            :key="image.imageID"
          >
            <img
              :src="image.imageURL"
              v-if="image.imageURL != null"
              alt="商品图片"
              class="product-image"
            />
            <div class="product-image" v-else>
              <el-empty description="此商品暂无图片" :image-size="50" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="规格：">
          <el-text tag="b">{{ props.selectedItem.condition }}</el-text>
        </el-form-item>
        <el-form-item label="价格：">
          <el-text tag="b">￥{{ props.selectedItem.price }}</el-text>
        </el-form-item>
        <el-form-item label="收件人姓名：">
          <el-text tag="b">￥{{ user.value.recipientName }}</el-text>
        </el-form-item>
        <el-form-item label="收件人地址：">
          <el-text tag="b">￥{{ user.value.address }}</el-text>
        </el-form-item>
        <el-form-item label="收件人电话：">
          <el-text tag="b">￥{{ user.value.phoneNumber }}</el-text>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-tooltip effect="dark" placement="top">
            <template #content>
              如订单信息错误，请联系商家修改！！！<br />如收件人信息错误，请在个人信息中修改！！！
            </template>
            <el-icon class="icon"><QuestionFilled /></el-icon>
          </el-tooltip>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="payNow"> 立即支付 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessageBox, ElMessage } from "element-plus";
import { defineProps, onMounted, ref, reactive, getCurrentInstance } from "vue";
import axios from "axios";

const emitBack = defineEmits(["back", "chat", "order"]);
const instance = getCurrentInstance();
const props = defineProps({
  selectedItem: {
    type: Object,
    required: true,
  },
});

const goBack = () => {
  emitBack("back");
};

const contactSeller = () => {
  // 实现联系商家的逻辑，比如弹窗或导航到聊天页面
  instance.emit("sellerID", props.selectedItem.sellerID);
  emitBack("chat");
};

const dialogVisible = ref(false);
const order = ref({
  orderID: "",
  buyerID: "", //
  sellerID: "",
  itemID: "",
  price: "",
  itemName: "",
});
const buyNow = () => {
  dialogVisible.value = true;
  order.value.buyerID = user.value.userID;
  order.value.sellerID = props.selectedItem.sellerID;
  order.value.itemID = props.selectedItem.itemID;
  order.value.price = props.selectedItem.price;
  order.value.itemName = props.selectedItem.name;
  order.value.recipientName = user.value.recipientName;
  order.value.phoneNumber = user.value.phoneNumber;
  order.value.address = user.value.address;
};

const payNow = () => {
  ElMessageBox.confirm("确定要支付吗？", "Warning", {
    confirmButtonText: "确定支付",
    cancelButtonText: "取消支付",
    type: "warning",
  })
    .then(async () => {
      if (user.value.userID === props.selectedItem.sellerID) {
        ElMessage.error("不能购买自己的商品！");
        return;
      }
      if (user.value.walletBalance < order.value.price) {
        ElMessage.error("余额不足！请前往个人中心充值！");
        return;
      }
      try {
        const res = await axios.post(
          "http://192.168.1.112:8080/userCreateOrder",
          order.value
        );
        console.log(res.data);
        if (res.data === "order created") {
          let changeStatus = ref({});
          changeStatus.value.itemID = props.selectedItem.itemID;
          changeStatus.value.status = "已售出";
          const res2 = await axios.put(
            "http://192.168.1.112:8080/userChangeStatus",
            changeStatus.value
          );
          if (res2.data === "status changed") {
            let changeMoney = ref({});
            changeMoney.value.userID = user.value.userID;
            changeMoney.value.walletBalance =
              user.value.walletBalance - order.value.price;
            const res3 = await axios.put(
              "http://192.168.1.112:8080/changeUserWalletBalance",
              changeMoney.value
            );
            if (res3.data === "walletBalance changed") {
              ElMessage.success("支付成功！请等待卖家发货！");
              dialogVisible.value = false;
              emitBack("order");
            }
          }
        }
      } catch (err) {
        ElMessage.error("支付请求错误！", err);
      }
    })
    .catch(() => {});
};

const user = reactive([]);
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
  } catch (error) {
    console.error("获取用户信息失败", error);
  }
};

onMounted(getUserInfo);
</script>

<style scoped>
.product-details-card {
  width: 800px;
  margin: auto;
  display: flex;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  justify-content: center;
  align-items: center;
}

.product-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 700px;
}

.product-image {
  width: 70%;
  height: 70%;
  border-radius: 10px;
  margin-top: 20px;
}

.product-details-container {
  flex: 2;
  padding: 20px;
}

.product-details-container h2 {
  margin-top: 0;
}

.product-details-container h3 {
  color: #f56c6c;
}

.product-details-container p {
  text-align: justify;
  margin-top: 20px;
}

.product-details-container {
  flex: 2;
  padding: 20px;
  text-align: center; /* 居中文本 */
}

.product-name {
  margin-top: 0;
}

.product-price {
  color: #f56c6c;
}

.contact-seller-btn,
.buy-now-btn {
  margin-top: 10px; /* 按钮与价格之间的间距 */
  width: 100%; /* 按钮宽度充满容器 */
}

.icon_box {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.backIcon {
  cursor: pointer; /* 鼠标悬停时显示手形光标 */
  transition: color 0.3s; /* 平滑的颜色变换效果 */
}

.backIcon:hover {
  color: #409eff; /* 鼠标悬停时的颜色，根据需要调整 */
}

.orderImage {
  display: flex;
}

.el-form {
  display: flex;
  flex-direction: column;
  padding-left: 70px;
}

.icon {
  margin-right: 20px;
}
.icon :hover {
  color: #3ca8e7; /* 鼠标悬停时的颜色 */
  transform: scale(1.1); /* 鼠标悬停时变大 */
}
</style>
