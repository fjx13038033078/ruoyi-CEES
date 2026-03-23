<template>
  <div class="register">
    <div class="register-container">
      <div class="register-content">
        <div class="register-header">
          <h2 class="title">高考志愿智能填报系统</h2>
          <p class="subtitle">创建新账号</p>
        </div>

        <!-- 角色选择 Tab -->
        <div class="role-tabs">
          <div
            :class="['role-tab', { active: registerForm.userType === '01' }]"
            @click="registerForm.userType = '01'"
          >
            <i class="el-icon-user"></i>
            <span>考生注册</span>
          </div>
          <div
            :class="['role-tab', { active: registerForm.userType === '02' }]"
            @click="registerForm.userType = '02'"
          >
            <i class="el-icon-s-custom"></i>
            <span>家长注册</span>
          </div>
        </div>

        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <!-- 考生/家长 真实姓名 -->
          <el-form-item prop="realName">
            <el-input
              v-model="registerForm.realName"
              type="text"
              auto-complete="off"
              placeholder="请输入真实姓名"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <!-- 考生注册字段 -->
          <template v-if="registerForm.userType === '01'">
            <el-form-item prop="idCard">
              <el-input
                v-model="registerForm.idCard"
                type="text"
                auto-complete="off"
                placeholder="请输入身份证号"
                class="custom-input"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>
            <el-form-item prop="examNumber">
              <el-input
                v-model="registerForm.examNumber"
                type="text"
                auto-complete="off"
                placeholder="请输入考生号"
                class="custom-input"
              >
                <svg-icon slot="prefix" icon-class="education" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>
          </template>

          <!-- 家长注册字段 -->
          <template v-if="registerForm.userType === '02'">
            <el-form-item prop="relatedExamNumber">
              <el-input
                v-model="registerForm.relatedExamNumber"
                type="text"
                auto-complete="off"
                placeholder="请输入关联考生号"
                class="custom-input"
              >
                <svg-icon slot="prefix" icon-class="education" class="el-input__icon input-icon" />
              </el-input>
            </el-form-item>
          </template>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              auto-complete="off"
              placeholder="请再次输入密码"
              @keyup.enter.native="handleRegister"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="verification-code">
              <el-input
                v-model="registerForm.code"
                auto-complete="off"
                placeholder="验证码"
                class="custom-input code-input"
                @keyup.enter.native="handleRegister"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="register-code">
                <img :src="codeUrl" @click="getCode" class="register-code-img" alt="验证码"/>
              </div>
            </div>
          </el-form-item>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              type="primary"
              class="register-button"
              @click.native.prevent="handleRegister"
            >
              <span v-if="!loading">注 册</span>
              <span v-else>注 册 中...</span>
            </el-button>
          </el-form-item>
          <div class="login-link">
            <span>已有账号？</span>
            <router-link class="link-type" :to="'/login'">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login";

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const validateIdCard = (rule, value, callback) => {
      if (this.registerForm.userType === '01' && value) {
        const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        if (!reg.test(value)) {
          callback(new Error("身份证号格式不正确"));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    return {
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: "",
        userType: "01",
        realName: "",
        idCard: "",
        examNumber: "",
        relatedExamNumber: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        realName: [
          { required: true, trigger: "blur", message: "请输入真实姓名" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        idCard: [
          { validator: validateIdCard, trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.registerForm.uuid = res.uuid;
        }
      });
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          register(this.registerForm).then(res => {
            const username = this.registerForm.username;
            this.$alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              this.$router.push("/login");
            }).catch(() => {});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          })
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('~@/assets/images/login-background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: auto;
  padding: 40px 0;

  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 0;
  }
}

.register-container {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.2);
  width: 440px;
  padding: 35px 40px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 12px 40px 0 rgba(0, 0, 0, 0.25);
  }
}

.register-content {
  .register-header {
    text-align: center;
    margin-bottom: 25px;

    .title {
      font-size: 26px;
      color: #fff;
      margin-bottom: 10px;
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

.role-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 25px;

  .role-tab {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px 0;
    border: 1px solid rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 0.08);
    color: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 14px;

    i {
      font-size: 18px;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      border-color: rgba(255, 255, 255, 0.5);
    }

    &.active {
      background: rgba(26, 95, 180, 0.6);
      border-color: rgba(26, 95, 180, 0.8);
      color: #fff;
      font-weight: 600;
    }
  }
}

.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .custom-input {
    .el-input__inner {
      height: 42px;
      line-height: 42px;
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
    height: 42px;
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

  .register-code-img {
    height: 42px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 255, 255, 0.3);

    &:hover {
      opacity: 0.85;
      border-color: rgba(255, 255, 255, 0.5);
    }
  }
}

.register-button {
  width: 100%;
  height: 42px;
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

.login-link {
  text-align: center;
  margin-top: 15px;
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
  .register-container {
    width: 90%;
    padding: 20px;
  }
}
</style>
