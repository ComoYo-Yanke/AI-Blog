<template>
  <div class="page">
    <div class="content-layout">
      <div class="main-col">
        <div class="page-header">
          <h1>编辑文章</h1>
        </div>
        <div class="editor-card" v-if="form">
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="文章标题" size="large" />
            </el-form-item>
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="2"
                placeholder="简短描述文章内容（选填）" resize="none" />
            </el-form-item>
            <el-form-item label="内容" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="16"
                placeholder="文章内容" resize="none" />
            </el-form-item>
            <el-form-item>
              <div class="form-actions">
                <el-button type="primary" size="large" @click="handleSubmit" :loading="loading">
                  <el-icon><Check /></el-icon>保存修改
                </el-button>
                <el-button size="large" @click="router.push(`/articles/${articleId}`)">取消</el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const articleId = route.params.id
const formRef = ref(null)
const loading = ref(false)
const form = ref(null)

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function fetchArticle() {
  const res = await request.get(`/articles/${articleId}`)
  form.value = {
    title: res.data.title,
    summary: res.data.summary || '',
    content: res.data.content
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.put(`/articles/${articleId}`, form.value)
    ElMessage.success('保存成功')
    router.push(`/articles/${articleId}`)
  } catch (e) {} finally { loading.value = false }
}

onMounted(fetchArticle)
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 24px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; font-weight: 700; }
.editor-card { background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 28px; }
.form-actions { display: flex; gap: 12px; }
</style>
