import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const saved = localStorage.getItem('theme')
  const isDark = ref(saved === 'dark')

  function toggle() {
    isDark.value = !isDark.value
  }

  function apply() {
    document.documentElement.classList.toggle('dark', isDark.value)
  }

  watch(isDark, (val) => {
    localStorage.setItem('theme', val ? 'dark' : 'light')
    apply()
  }, { immediate: true })

  return { isDark, toggle, apply }
})
