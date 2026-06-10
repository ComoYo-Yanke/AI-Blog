<template>
  <div class="admin-page">
    <div class="admin-nav">
      <div class="nav-inner">
        <router-link to="/admin/dashboard" class="nav-item">
          <el-icon><DataAnalysis /></el-icon>控制台
        </router-link>
        <router-link to="/admin/users" class="nav-item active">
          <el-icon><User /></el-icon>用户管理
        </router-link>
        <router-link to="/admin/articles" class="nav-item">
          <el-icon><Document /></el-icon>文章管理
        </router-link>
        <router-link to="/admin/comments" class="nav-item">
          <el-icon><ChatDotSquare /></el-icon>评论管理
        </router-link>
      </div>
    </div>

    <div class="page-content">
      <div class="page-header">
        <h1 class="page-title">用户管理</h1>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>添加用户
        </el-button>
      </div>
      <div class="table-wrap">
        <el-table :data="users" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="用户名" width="140">
            <template #default="{ row }">
              <span class="link" @click="$router.push(`/users/${row.id}`)">{{ row.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" min-width="160" />
          <el-table-column label="角色" width="90">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="关注" width="130">
            <template #default="{ row }">
              <span style="font-size:12px;color:#656d76">
                关注 {{ row.followingCount || 0 }} / 粉丝 {{ row.followerCount || 0 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
              <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
                @click="toggleStatus(row.id)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="480px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password
            :placeholder="isEdit ? '留空则不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role">
            <el-option label="USER" value="USER" />
            <el-option label="ADMIN" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const saving = ref(false)
const userFormRef = ref(null)
const userForm = ref({ username: '', password: '', email: '', role: 'USER' })

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

async function fetchUsers() { const res = await request.get('/admin/users'); users.value = res.data || [] }

function showAddDialog() {
  isEdit.value = false; editId.value = null
  userForm.value = { username: '', password: '', email: '', role: 'USER' }
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true; editId.value = row.id
  userForm.value = { username: row.username, password: '', email: row.email || '', role: row.role }
  dialogVisible.value = true
}

async function saveUser() {
  const valid = await userFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await request.put(`/admin/users/${editId.value}`, userForm.value)
    } else {
      await request.post('/admin/users', userForm.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchUsers()
  } catch (e) {} finally { saving.value = false }
}

async function toggleStatus(id) {
  await request.put(`/admin/users/${id}/status`)
  ElMessage.success('操作成功')
  fetchUsers()
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除此用户？所有关联数据将被一并删除。', '确认删除', { type: 'warning' })
  await request.delete(`/admin/users/${id}`)
  ElMessage.success('删除成功')
  fetchUsers()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-page { max-width: 1200px; margin: 0 auto; padding: 0 24px 24px; }
.admin-nav { margin: 0 -24px; padding: 0 24px; background: var(--bg-surface); border-bottom: 1px solid var(--border-default); }
.nav-inner { display: flex; gap: 0; }
.nav-item {
  display: flex; align-items: center; gap: 6px; padding: 12px 16px;
  font-size: 14px; font-weight: 500; color: var(--text-secondary); border-bottom: 2px solid transparent;
  text-decoration: none; transition: all .15s;
}
.nav-item:hover { color: var(--text-primary); background: var(--bg-hover); }
.nav-item.active { color: var(--text-primary); border-bottom-color: var(--accent); }
.page-content { margin-top: 24px; }
.page-title { font-size: 24px; font-weight: 700; margin: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-wrap { background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 16px; }
.link { cursor: pointer; color: var(--accent); }
.link:hover { text-decoration: underline; }
</style>
