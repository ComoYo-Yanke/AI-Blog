<template>
  <div class="page">
    <div class="content-layout">
      <div class="main-col">
        <div class="page-header">
          <h1>写文章</h1>
        </div>
        <div class="editor-card">
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="给你的文章起个标题..." size="large" />
            </el-form-item>
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="2"
                placeholder="简短描述文章内容（选填）" resize="none" />
            </el-form-item>
            <el-form-item label="内容" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="16"
                placeholder="开始写作..." resize="none" />
            </el-form-item>
            <el-form-item>
              <div class="form-actions">
                <el-button type="primary" size="large" @click="handleSubmit" :loading="loading">
                  <el-icon><Promotion /></el-icon>发布文章
                </el-button>
                <el-button size="large" @click="$router.push('/')">取消</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
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

const form = reactive({ title: '', summary: '', content: '' })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.post('/articles', form)
    ElMessage.success('发布成功')
    router.push('/')
  } catch (e) {} finally { loading.value = false }
}
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 24px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; font-weight: 700; }
.editor-card { background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 28px; }
.form-actions { display: flex; gap: 12px; }
</style>
