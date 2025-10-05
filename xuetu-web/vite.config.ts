import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: true,
    open: false,
    cors: true,
    proxy: {
      // 订单、支付、用户课程相关
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true
      },
      // 认证相关
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('🔄 代理请求:', req.method, req.url, '→', options.target + req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('✅ 代理响应:', req.url, '状态码:', proxyRes.statusCode);
          });
        }
      },
      // 用户相关
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 课程相关
      '/course': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 分类相关
      '/category': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 章节相关
      '/chapter': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 课时相关
      '/lesson': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 评论相关
      '/comment': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 学习记录和笔记
      '/learning': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // AI相关
      '/chat': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/analysis': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/recommend': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 权限管理
      '/role': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/permission': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})