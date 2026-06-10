<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <el-icon :size="32" color="#409eff"><Reading /></el-icon>
        <h1>注册 BlogHub</h1>
        <p>创建你的账号，开始写作之旅</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="3-50个字符" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password
            placeholder="至少6位密码" size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password
            placeholder="再次输入密码" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="选填" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleRegister"
            :loading="loading" style="width:100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '', password: '', confirmPassword: '', email: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '长度6-50个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.post('/auth/register', {
      username: form.username, password: form.password, email: form.email
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex; justify-content: center; align-items: center;
  min-height: calc(100vh - 56px - 60px); padding: 24px;
}
.auth-card {
  width: 400px; background: var(--bg-surface); border: 1px solid var(--border-default);
  border-radius: 12px; padding: 32px;
}
.auth-header { text-align: center; margin-bottom: 28px; }
.auth-header h1 { font-size: 22px; font-weight: 700; margin: 12px 0 4px; color: var(--text-primary); }
.auth-header p { font-size: 14px; color: var(--text-secondary); }
.auth-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 16px; }
.auth-footer a { color: var(--accent); font-weight: 500; }
</style>
