<template>
  <div class="profile-page">
    <div class="content-layout">
      <!-- 用户信息卡片 -->
      <aside class="side-col">
        <div v-if="user" class="profile-card">
          <el-avatar :size="80" class="profile-avatar">
            {{ user.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <h2 class="profile-name">{{ user.username }}</h2>
          <p v-if="user.email" class="profile-email">{{ user.email }}</p>
          <p class="profile-joined">加入于 {{ formatDate(user.createdAt) }}</p>

          <div v-if="auth.isLoggedIn && auth.user?.id !== user.id" class="follow-section">
            <el-button :type="isFollowing ? 'default' : 'primary'"
              style="width:100%" @click="toggleFollow">
              {{ isFollowing ? '已关注' : '关注' }}
            </el-button>
          </div>

          <div class="stats-row">
            <div class="stat" @click="activeTab='followers'">
              <strong>{{ followerCount }}</strong>
              <span>粉丝</span>
            </div>
            <div class="stat" @click="activeTab='following'">
              <strong>{{ followingCount }}</strong>
              <span>关注</span>
            </div>
            <div class="stat">
              <strong>{{ articleTotal }}</strong>
              <span>文章</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 主内容区 -->
      <div class="main-col">
        <div class="tab-nav">
          <button :class="{ active: activeTab === 'articles' }" @click="activeTab='articles'">
            <el-icon :size="16"><Document /></el-icon>文章
          </button>
          <button :class="{ active: activeTab === 'followers' }" @click="activeTab='followers'">
            <el-icon :size="16"><User /></el-icon>粉丝
          </button>
          <button :class="{ active: activeTab === 'following' }" @click="activeTab='following'">
            <el-icon :size="16"><Star /></el-icon>关注
          </button>
        </div>

        <!-- 文章列表 -->
        <div v-if="activeTab === 'articles'">
          <article v-for="article in articles" :key="article.id" class="article-card"
            @click="$router.push(`/articles/${article.id}`)">
            <h3 class="card-title">{{ article.title }}</h3>
            <p class="card-summary">{{ article.summary || article.content?.substring(0, 200) }}</p>
            <span class="card-date">{{ formatDate(article.createdAt) }}</span>
          </article>
          <div v-if="articles.length === 0" class="empty-state">暂无文章</div>
          <div class="pagination-wrap" v-if="articleTotal > 10">
            <el-pagination background layout="prev, pager, next" :total="articleTotal"
              :page-size="10" :current-page="currentPage" @current-change="onPageChange" />
          </div>
        </div>

        <!-- 粉丝列表 -->
        <div v-if="activeTab === 'followers'">
          <div v-for="f in followers" :key="f.id" class="user-row"
            @click="$router.push(`/users/${f.id}`)">
            <el-avatar :size="36" class="row-avatar">
              {{ f.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="row-info">
              <strong>{{ f.username }}</strong>
              <span v-if="f.email" class="row-email">{{ f.email }}</span>
            </div>
          </div>
          <div v-if="followers.length === 0" class="empty-state">暂无粉丝</div>
        </div>

        <!-- 关注列表 -->
        <div v-if="activeTab === 'following'">
          <div v-for="f in following" :key="f.id" class="user-row"
            @click="$router.push(`/users/${f.id}`)">
            <el-avatar :size="36" class="row-avatar">
              {{ f.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="row-info">
              <strong>{{ f.username }}</strong>
              <span v-if="f.email" class="row-email">{{ f.email }}</span>
            </div>
          </div>
          <div v-if="following.length === 0" class="empty-state">暂无关注</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const user = ref(null)
const articles = ref([])
const followers = ref([])
const following = ref([])
const followerCount = ref(0)
const followingCount = ref(0)
const isFollowing = ref(false)
const articleTotal = ref(0)
const currentPage = ref(1)
const activeTab = ref('articles')

async function fetchProfile() {
  const res = await request.get(`/users/${route.params.id}/profile`)
  const data = res.data || {}
  user.value = data.user
  followerCount.value = data.followerCount || 0
  followingCount.value = data.followingCount || 0
  isFollowing.value = data.isFollowing || false
}

async function fetchArticles() {
  const params = { page: currentPage.value - 1, size: 10 }
  const res = await request.get(`/users/${route.params.id}/articles`, { params })
  const pageResult = res.data || {}
  articles.value = pageResult.records || []
  articleTotal.value = pageResult.total || 0
}

async function fetchFollowers() {
  const res = await request.get(`/users/${route.params.id}/followers`)
  followers.value = res.data || []
}

async function fetchFollowing() {
  const res = await request.get(`/users/${route.params.id}/following`)
  following.value = res.data || []
}

function onPageChange(page) { currentPage.value = page; fetchArticles() }

async function toggleFollow() {
  if (!auth.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  try {
    if (isFollowing.value) {
      await request.delete(`/users/${route.params.id}/follow`)
      isFollowing.value = false
      followerCount.value--
    } else {
      await request.post(`/users/${route.params.id}/follow`)
      isFollowing.value = true
      followerCount.value++
    }
  } catch (e) {}
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

watch(activeTab, (tab) => {
  if (tab === 'followers') fetchFollowers()
  if (tab === 'following') fetchFollowing()
})

onMounted(() => {
  fetchProfile()
  fetchArticles()
})
</script>

<style scoped>
.profile-page { max-width: 1200px; margin: 0 auto; padding: 24px; }

.content-layout {
  display: grid; grid-template-columns: 260px 1fr; gap: 32px; align-items: start;
}
.side-col { position: sticky; top: 80px; }

/* Profile Card */
.profile-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 24px; text-align: center;
}
.profile-avatar {
  background: var(--accent) !important; font-size: 32px; font-weight: 700; margin-bottom: 12px;
}
.profile-name { font-size: 22px; font-weight: 700; color: var(--text-primary); }
.profile-email { font-size: 13px; color: var(--text-secondary); margin: 4px 0; max-width: 100%; overflow: hidden; text-overflow: ellipsis; }
.profile-joined { font-size: 12px; color: var(--text-tertiary); margin: 4px 0 16px; }
.follow-section { margin-bottom: 16px; }
.stats-row { display: flex; justify-content: space-around; padding-top: 16px; border-top: 1px solid var(--border-light); }
.stat { cursor: pointer; }
.stat strong { display: block; font-size: 18px; color: var(--text-primary); }
.stat span { font-size: 12px; color: var(--text-secondary); }

/* Tab Nav */
.tab-nav {
  display: flex; gap: 0; margin-bottom: 20px; background: var(--bg-surface);
  border: 1px solid var(--border-default); border-radius: var(--radius); overflow: hidden;
}
.tab-nav button {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 10px 16px; border: none; background: none; cursor: pointer;
  font-size: 14px; font-weight: 500; color: var(--text-secondary); transition: all .15s;
}
.tab-nav button:hover { background: var(--bg-primary); }
.tab-nav button.active { color: var(--text-primary); background: var(--accent-bg); box-shadow: inset 0 -2px 0 var(--accent); }

/* Article Cards */
.article-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 12px; cursor: pointer; transition: all .15s;
}
.article-card:hover { border-color: var(--accent); }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 6px; }
.card-summary { font-size: 13px; color: var(--text-secondary); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-date { font-size: 12px; color: var(--text-tertiary); margin-top: 8px; display: block; }

/* User Rows */
.user-row {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px;
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  margin-bottom: 8px; cursor: pointer; transition: all .15s;
}
.user-row:hover { border-color: var(--accent); }
.row-avatar { background: var(--accent) !important; font-weight: 600; flex-shrink: 0; }
.row-info strong { font-size: 14px; color: var(--text-primary); }
.row-email { font-size: 12px; color: var(--text-tertiary); margin-left: 8px; }

.empty-state { text-align: center; padding: 48px 0; color: var(--text-tertiary); font-size: 14px; }
.pagination-wrap { display: flex; justify-content: center; margin: 24px 0; }

@media (max-width: 768px) {
  .content-layout { grid-template-columns: 1fr; }
  .side-col { position: static; }
}
</style>
