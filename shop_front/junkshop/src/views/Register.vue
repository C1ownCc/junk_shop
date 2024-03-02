<template>
  <div class="main">
    <div class="register">
      <div class="register-form-box">
        <a href="home" class="back">返回登录</a>
        <p class="title">跳蚤市场二手交易平台</p>
        <p class="title">注册</p>
        <form class="register-form">
          <input
            type="text"
            class="username"
            placeholder="账号"
            v-model="formData.username"
          />
          <input
            type="email"
            class="username"
            placeholder="邮箱"
            v-model="formData.email"
          />
          <input
            type="text"
            class="username"
            placeholder="昵称"
            v-model="formData.nickName"
          />
          <input
            type="password"
            class="password"
            placeholder="密码"
            v-model="formData.passwordHash"
          />
          <input
            type="text"
            class="phone"
            placeholder="手机号"
            v-model="formData.phoneNumber"
          />
          <input
            type="text"
            class="phone"
            placeholder="收件人姓名"
            v-model="formData.recipientName"
          />
          <input
            type="text"
            class="phone"
            placeholder="收件人地址"
            v-model="formData.address"
          />
          <!-- <div class="code-pane">
            <input type="text" class="code" placeholder="验证码">
            <input type="button" class="code-button" value="获取验证码">
            </div> -->
          <input
            type="button"
            class="register-button"
            value="注册"
            @click="handleRegister"
          />
          <!-- <div class="link">
            <a href="login" class="login">登录</a>
            <a href="home" class="index">主页</a>
          </div> -->
        </form>
        <p class="error" v-if="!formValid">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter(); // 正确获取 router 实例

// 表单数据
const formData = ref({
  username: "",
  email: "",
  nickName: "",
  passwordHash: "",
  phoneNumber: "",
  recipientName: "",
  address: "",
});

// 表单验证状态
const formValid = ref(true);
const errorMessage = ref("");

// 正则表达式
const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
const phoneRegex = /^[1][3-9][0-9]{9}$/;

// 验证逻辑
const validateForm = () => {
  formValid.value = true;
  errorMessage.value = "";

  if (!formData.value.username.trim() || formData.value.username.length < 5) {
    formValid.value = false;
    errorMessage.value = "账号不能为空";
    return false;
  }

  if (!emailRegex.test(formData.value.email)) {
    formValid.value = false;
    errorMessage.value = "请输入有效的邮箱";
    return false;
  }

  if (!formData.value.nickName || !formData.value.nickName.trim()) {
    formValid.value = false;
    errorMessage.value = "昵称不能为空";
    return false;
  }

  console.log(formData.value.passwordHash);
  if (!formData.value.passwordHash || formData.value.passwordHash.length < 5) {
    formValid.value = false;
    errorMessage.value = "密码长度至少6位";
    return false;
  }

  if (!phoneRegex.test(formData.value.phoneNumber)) {
    formValid.value = false;
    errorMessage.value = "请输入有效的手机号码";
    return false;
  }

  if (!formData.value.recipientName.trim()) {
    formValid.value = false;
    errorMessage.value = "收件人姓名不能为空";
    return false;
  }

  if (!formData.value.address.trim()) {
    formValid.value = false;
    errorMessage.value = "收件人地址不能为空";
    return false;
  }

  return true;
};

// 注册请求
const handleRegister = async () => {
  if (!validateForm()) return;

  try {
    const response = await axios.post(
      "http://192.168.1.112:8080/register",
      formData.value
    );
    // alert(response.data); // 或者根据实际情况做其他处理
    if (response.data === "Registration Successful") {
      router.push("/login");
      alert("注册成功，请登录");
    } else if (
      response.data === "Username, Phone Number, or Email already exists"
    ) {
      alert("该用户名、手机号或邮箱已被注册");
    } else {
      alert("注册失败，请稍后重试");
    }
  } catch (error) {
    console.error(error);
  }
};
</script>

<style scoped lang="scss">
@import "@/assets/css/main.scss"; // 确保路径正确

.register {
  display: flex;
  width: 100%;
  height: 100vh;
  background: url("../assets/imgs/user.png") no-repeat center;
  background-size: cover;

  .back {
    position: absolute;
    top: 32px;
    left: 32px;
    color: black;
    transition: all 0.5s;

    &:hover {
      color: #0188fb;
    }
  }

  .register-form-box {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: relative;
    width: 50%;
    height: 100%;
    background-color: white;
    border-right: 1px solid rgb(243, 243, 243);

    .title {
      font-size: 32px;
    }

    .register-form {
      display: flex;
      flex-direction: column;
      width: 400px;

      input {
        height: 36px;
        margin-bottom: 32px;
        text-align: center;
        border: 1px solid rgb(235, 235, 235);
        border-radius: 36px;
        transition: all 0.5s;

        &:focus {
          border: 1px solid #0188fb;
        }
      }

      .code-pane {
        width: 100%;
        border: 1px solid rgb(235, 235, 235);
        border-radius: 36px;
        margin-bottom: 32px;

        &:focus-within {
          border: 1px solid #0188fb;
        }

        input {
          margin: 0;
          border: none;
        }

        .code {
          width: calc(60% - 52px);
          padding-left: 48px;
        }

        .code-button {
          width: calc(40% - 8px);
          background-color: transparent;
          cursor: pointer;
          transition: all 0.5s;

          &:hover {
            color: #0188fb;
          }

          &:disabled {
            pointer-events: none;
          }
        }
      }

      .register-button {
        height: 42px;
        background-color: #35a1ff;
        color: white;
        font-size: 18px;
        cursor: pointer;
        transition: all 0.5s;

        &:hover {
          opacity: 0.5;
        }
      }

      .link {
        display: flex;
        justify-content: space-between;

        a {
          color: black;
          transition: all 0.5s;

          &:hover {
            color: #0188fb;
          }
        }

        .login {
          margin-left: 16px;
        }

        .index {
          margin-right: 16px;
        }
      }
    }

    .error {
      color: red;
    }
  }
}
</style>
