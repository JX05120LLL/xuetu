<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>关于我们</h3>
          <ul>
            <li><a href="#">公司介绍</a></li>
            <li><a href="#">加入我们</a></li>
            <li><a href="#">联系方式</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>帮助中心</h3>
          <ul>
            <li><a href="#">常见问题</a></li>
            <li><a href="#">使用指南</a></li>
            <li><a href="#">隐私政策</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>友情链接</h3>
          <ul>
            <li><a href="#">合作伙伴</a></li>
            <li><a href="#">教育资源</a></li>
            <li><a href="#">技术博客</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h3>联系我们</h3>
          <ul>
            <li>邮箱: contact@xuetu.com</li>
            <li>电话: 400-123-4567</li>
            <li>地址: 北京市朝阳区xxx路xxx号</li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; 2025 学途在线教育平台. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.app-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #1a252f 100%);
  color: #ecf0f1;
  padding: 60px 0 30px;
  margin-top: 80px;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  }

  .footer-content {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 50px;
    margin-bottom: 40px;
  }

  .footer-section {
    h3 {
      font-size: 18px;
      margin-bottom: 20px;
      color: #fff;
      font-weight: 600;
      position: relative;
      padding-bottom: 12px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        width: 40px;
        height: 3px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
      }
    }

    ul {
      list-style: none;

      li {
        margin-bottom: 12px;
        font-size: 14px;
        transition: all 0.3s;

        a {
          color: #bdc3c7;
          transition: all 0.3s;
          display: inline-block;

          &:hover {
            color: #fff;
            transform: translateX(5px);
          }
        }
      }
    }
  }

  .footer-bottom {
    padding-top: 30px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    text-align: center;
    color: #95a5a6;
    font-size: 14px;

    p {
      margin: 0;
    }
  }
}
</style>