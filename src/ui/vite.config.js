import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath } from 'node:url'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/messaging': {
        target: 'ws://127.0.0.1:8082',
        ws: true,
        rewriteWsOrigin: true
      },
      '/api': {
        target: 'http://127.0.0.1:8082/',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: [
      {
        find: '@',
        replacement: fileURLToPath(new URL('./src', import.meta.url))
      }
    ]
  }
})
