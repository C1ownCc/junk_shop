<template>
  <div class="main">
    <div class="login">
      <div class="login-form-box">
        <!-- <a href="home" class="back">首页</a> -->
        <p class="title">跳蚤市场二手交易平台</p>
        <p class="title">登录</p>
        <form class="login-form" @submit.prevent="login">
          <input
            type="text"
            class="username"
            placeholder="用户名"
            v-model="loginData.username"
          />
          <div v-if="loginError.username" class="error">
            用户名至少需要 5 个字符
          </div>

          <input
            type="password"
            class="password"
            placeholder="密码"
            v-model="loginData.passwordHash"
          />
          <div v-if="loginError.password" class="error">
            密码至少需要 5 个字符
          </div>

          <input type="submit" class="login-button" value="登录" />
          <div class="link">
            <a href="register" class="register">注册账号</a>
            <a href="#forget" class="forget" @click="forgetPassword"
              >忘记密码</a
            >
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter(); // 正确获取 router 实例

//登录数据
const loginData = ref({
  username: "",
  passwordHash: "",
});

//登陆错误
const loginError = ref({
  username: false,
  password: false,
});

//登录方法
const login = async () => {
  loginError.value.username =
    !loginData.value.username || loginData.value.username.length < 5;
  loginError.value.password =
    !loginData.value.passwordHash || loginData.value.passwordHash.length < 5;

  if (loginError.value.username || loginError.value.password) {
    console.log("校验未通过");
    return; // 如果校验失败，停止执行登录逻辑
  }
  console.log(loginData.value);
  try {
    const response = await axios.post(
      "http://localhost:8080/login",
      loginData.value
    );
    // console.log("获取数据:", response.data);

    // 根据角色跳转
    if (response.data === "Admin Logged In") {
      router.push("/admin");
      // 存储管理员信息和登录状态
      localStorage.setItem("userInfo", JSON.stringify(loginData.value));
      localStorage.setItem("userLoggedIn", "true");
      localStorage.setItem("role", "0");
    } else if (response.data === "User Logged In") {
      router.push("/home");
      // 存储用户信息和登录状态
      localStorage.setItem("userInfo", JSON.stringify(loginData.value));
      localStorage.setItem("userLoggedIn", "true");
      localStorage.setItem("role", "1");
    } else {
      ElMessage.error("登陆失败！用户名或密码错误！");
      console.log("登录失败");
      // 处理登录失败的情况
    }
  } catch (error) {
    console.error("登录请求出错:", error);
    // 清理可能的存储状态
    localStorage.removeItem("userInfo");
    localStorage.removeItem("userLoggedIn");
    // 处理错误情况
  }
};

const forgetPassword = () => {
  alert(
    "请联系管理员找回密码！\n" +
      "联系方式：\n" +
      "QQ:********** \n" +
      "邮箱:**********@qq.com \n"
  );
};
</script>

<style scoped lang="scss">
@import url(@/assets/css/main.scss);

.login {
  display: flex;
  flex-direction: row-reverse;
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

  .login-form-box {
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

    .login-form {
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

      .error {
        color: red;
        margin-top: 5px;
        margin-bottom: 15px;
      }

      .login-button {
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

        .register {
          margin-left: 16px;
        }

        .forget {
          margin-right: 16px;
        }
      }
    }
  }
}
</style>
