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

          <el-form-item prop="idCard">
            <el-input
              v-model="registerForm.idCard"
              type="text"
              auto-complete="off"
              placeholder="请输入身份证号（15 位或 18 位）"
              maxlength="18"
              class="custom-input"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <!-- 考生注册字段 -->
          <template v-if="registerForm.userType === '01'">
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
              placeholder="请输入密码（至少「中」：8 位及以上，含两类字符）"
              class="custom-input"
              @input="onPasswordInput"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
            <div class="pwd-strength" v-if="registerForm.password">
              <span class="pwd-strength-label">密码安全性：</span>
              <span :class="['pwd-level', passwordStrengthClass]">{{ passwordStrengthText }}</span>
              <div class="pwd-bar">
                <i :class="{ active: passwordStrengthLevel >= 1 }" />
                <i :class="{ active: passwordStrengthLevel >= 2 }" />
                <i :class="{ active: passwordStrengthLevel >= 3 }" />
              </div>
            </div>
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

/** 与后端 IdCardUtils 规则一致 */
function isValidIdCard(idCard) {
  if (!idCard || typeof idCard !== "string") {
    return false;
  }
  const s = idCard.trim().toUpperCase();
  if (s.length === 18) {
    if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9X]$/.test(s)) {
      return false;
    }
    const birth = s.substring(6, 14);
    if (!isValidBirthYmd(birth)) {
      return false;
    }
    const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
    const checks = ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"];
    let sum = 0;
    for (let i = 0; i < 17; i++) {
      sum += (s.charCodeAt(i) - 48) * weights[i];
    }
    return s.charAt(17) === checks[sum % 11];
  }
  if (s.length === 15) {
    if (!/^[1-9]\d{7}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}$/.test(s)) {
      return false;
    }
    return isValidBirthYmd("19" + s.substring(6, 12));
  }
  return false;
}

function isValidBirthYmd(yyyyMMdd) {
  const y = parseInt(yyyyMMdd.slice(0, 4), 10);
  const m = parseInt(yyyyMMdd.slice(4, 6), 10) - 1;
  const d = parseInt(yyyyMMdd.slice(6, 8), 10);
  const dt = new Date(y, m, d);
  if (dt.getFullYear() !== y || dt.getMonth() !== m || dt.getDate() !== d) {
    return false;
  }
  const now = new Date();
  const max = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);
  const min = new Date(1900, 0, 1);
  return dt >= min && dt <= max;
}

/** 与后端 PasswordStrengthUtils 一致 */
function evaluatePasswordStrength(password) {
  if (!password) {
    return 0;
  }
  const len = password.length;
  let hasLower = /[a-z]/.test(password);
  let hasUpper = /[A-Z]/.test(password);
  let hasDigit = /\d/.test(password);
  let hasSpecial = /[^0-9a-zA-Z]/.test(password);
  let categories = 0;
  if (hasLower || hasUpper) {
    categories++;
  }
  if (hasDigit) {
    categories++;
  }
  if (hasSpecial) {
    categories++;
  }
  if (len >= 10 && categories >= 3) {
    return 3;
  }
  if (len >= 8 && categories >= 2) {
    return 2;
  }
  return 1;
}

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
      if (this.registerForm.userType !== "01" && this.registerForm.userType !== "02") {
        callback();
        return;
      }
      if (!value || !String(value).trim()) {
        callback(new Error("请输入身份证号"));
        return;
      }
      if (!isValidIdCard(value)) {
        callback(new Error("身份证号格式或校验码不正确"));
        return;
      }
      callback();
    };
    const validatePasswordStrength = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入您的密码"));
        return;
      }
      if (value.length < 5 || value.length > 20) {
        callback(new Error("用户密码长度必须介于 5 和 20 之间"));
        return;
      }
      if (evaluatePasswordStrength(value) < 2) {
        callback(new Error("密码安全性不足，请至少达到「中」（8 位及以上，字母/数字/符号至少两类）"));
        return;
      }
      callback();
    };
    return {
      codeUrl: "",
      passwordStrengthLevel: 0,
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
          { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
        ],
        realName: [
          { required: true, trigger: "blur", message: "请输入真实姓名" }
        ],
        idCard: [
          { required: true, trigger: "blur", message: "请输入身份证号" },
          { validator: validateIdCard, trigger: "blur" }
        ],
        examNumber: [
          {
            validator: (rule, value, callback) => {
              if (this.registerForm.userType !== "01") {
                callback();
                return;
              }
              if (!value || !String(value).trim()) {
                callback(new Error("请输入考生号"));
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
        relatedExamNumber: [
          {
            validator: (rule, value, callback) => {
              if (this.registerForm.userType !== "02") {
                callback();
                return;
              }
              if (!value || !String(value).trim()) {
                callback(new Error("请输入关联考生号"));
              } else {
                callback();
              }
            },
            trigger: "blur"
          }
        ],
        password: [
          { validator: validatePasswordStrength, trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    };
  },
  computed: {
    passwordStrengthText() {
      const m = { 0: "", 1: "低", 2: "中", 3: "高" };
      return m[this.passwordStrengthLevel] || "";
    },
    passwordStrengthClass() {
      const m = { 1: "low", 2: "medium", 3: "high" };
      return m[this.passwordStrengthLevel] || "";
    }
  },
  created() {
    this.getCode();
  },
  methods: {
    onPasswordInput() {
      this.passwordStrengthLevel = evaluatePasswordStrength(this.registerForm.password);
    },
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
      this.passwordStrengthLevel = evaluatePasswordStrength(this.registerForm.password);
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          register(this.registerForm)
            .then(res => {
              const username = this.registerForm.username;
              this.loading = false;
              this.$alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", "系统提示", {
                dangerouslyUseHTMLString: true,
                type: "success"
              })
                .then(() => {
                  this.$router.push("/login");
                })
                .catch(() => {});
            })
            .catch(() => {
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
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url("~@/assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: auto;
  padding: 40px 0;

  &::before {
    content: "";
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

.pwd-strength {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);

  .pwd-strength-label {
    margin-right: 6px;
  }

  .pwd-level.low {
    color: #f56c6c;
  }
  .pwd-level.medium {
    color: #e6a23c;
  }
  .pwd-level.high {
    color: #67c23a;
  }

  .pwd-bar {
    display: flex;
    gap: 4px;
    margin-top: 6px;

    i {
      flex: 1;
      height: 4px;
      border-radius: 2px;
      background: rgba(255, 255, 255, 0.25);
      transition: background 0.2s;

      &.active:nth-child(1) {
        background: #f56c6c;
      }
      &.active:nth-child(2) {
        background: #e6a23c;
      }
      &.active:nth-child(3) {
        background: #67c23a;
      }
    }
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
