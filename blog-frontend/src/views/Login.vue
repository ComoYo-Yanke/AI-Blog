<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <el-icon :size="32" color="#409eff"><Reading /></el-icon>
        <h1>登录 BlogHub</h1>
        <p>欢迎回来，请登录你的账号</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password
            placeholder="请输入密码" size="large" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleLogin"
            :loading="loading" style="width:100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // error handled by interceptor
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
