<template>
  <div class="login">
    <div class="login-container">
      <div class="login-content">
        <div class="login-header">
          <h2 class="title">高考志愿智能填报系统</h2>
          <p class="subtitle">欢迎回来！请登录您的账号</p>
        </div>
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="verification-code">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                class="custom-input code-input"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码"/>
              </div>
            </div>
          </el-form-item>
          <div class="remember-forgot">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              type="primary"
              class="login-button"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
          <div class="register-link" v-if="register">
            <span>还没有账号？</span>
            <router-link class="link-type" :to="'/register'">立即注册</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: true,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('~@/assets/images/login-background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 0;
  }
}

.login-container {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);
  width: 420px;
  padding: 40px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 12px 40px 0 rgba(0, 0, 0, 0.25);
  }
}

.login-content {
  .login-header {
    text-align: center;
    margin-bottom: 40px;

    .title {
      font-size: 26px;
      color: #fff;
      margin-bottom: 12px;
      font-weight: 700;
      letter-spacing: 2px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    .subtitle {
      color: rgba(255, 255, 255, 0.85);
      font-size: 14px;
      letter-spacing: 1px;
    }
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 25px;
  }

  .custom-input {
    .el-input__inner {
      height: 45px;
      line-height: 45px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(5px);
      padding-left: 45px;
      transition: all 0.3s ease;
      color: #fff;

      &::placeholder {
        color: rgba(255, 255, 255, 0.7);
      }

      &:focus {
        border-color: rgba(255, 255, 255, 0.6);
        background: rgba(255, 255, 255, 0.15);
        box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
      }
    }
  }

  .input-icon {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    height: 45px;
    width: 16px;
    margin: 0 15px;
  }
}

.verification-code {
  display: flex;
  gap: 10px;

  .code-input {
    flex: 1;
  }

  .login-code-img {
    height: 45px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 255, 255, 0.3);

    &:hover {
      opacity: 0.85;
      border-color: rgba(255, 255, 255, 0.5);
    }
  }
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;

  ::v-deep .el-checkbox {
    color: #fff;

    .el-checkbox__inner {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.6);
    }

    .el-checkbox__input.is-checked .el-checkbox__inner {
      background: #1a5fb4;
      border-color: #1a5fb4;
    }

    .el-checkbox__label {
      color: #fff;
      font-weight: 500;
    }
  }

  .forgot-password {
    color: #fff;
    text-decoration: none;
    font-size: 14px;
    transition: all 0.3s ease;

    &:hover {
      color: #fff;
      text-decoration: underline;
    }
  }
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  background: #1a5fb4;
  border: none;
  color: white;
  transition: all 0.3s ease;
  font-weight: 600;
  letter-spacing: 4px;

  &:hover {
    background: #1c71d8;
    box-shadow: 0 4px 12px rgba(26, 95, 180, 0.4);
  }

  &:active {
    background: #155099;
  }
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);

  .link-type {
    color: #fff;
    text-decoration: none;
    font-weight: 600;
    margin-left: 5px;
    transition: all 0.3s ease;
    border-bottom: 1px solid transparent;

    &:hover {
      border-bottom-color: #fff;
    }
  }
}

@media screen and (max-width: 480px) {
  .login-container {
    width: 90%;
    padding: 20px;
  }
}
</style>
