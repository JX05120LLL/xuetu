package com.xuetu.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * API文档聚合控制器
 * 提供统一的API文档访问入口
 * 
 * @author star
 */
@Slf4j
@RestController
public class ApiDocController {

    /**
     * API文档首页 - 聚合所有服务
     */
    @GetMapping(value = "/api-docs", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> apiDocsIndex() {
        log.info("访问API文档聚合页面");
        
        String html = """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>学途在线教育平台 - API文档中心</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: 'Microsoft YaHei', Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
                    .container { max-width: 1200px; margin: 0 auto; padding: 40px 20px; }
                    h1 { color: white; text-align: center; margin-bottom: 50px; font-size: 2.5em; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }
                    .services-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 30px; margin-bottom: 50px; }
                    .service-card { background: white; border-radius: 15px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); transition: transform 0.3s, box-shadow 0.3s; }
                    .service-card:hover { transform: translateY(-5px); box-shadow: 0 15px 40px rgba(0,0,0,0.3); }
                    .service-title { font-size: 1.8em; font-weight: bold; color: #333; margin-bottom: 15px; display: flex; align-items: center; }
                    .service-icon { font-size: 1.5em; margin-right: 10px; }
                    .service-desc { color: #666; margin-bottom: 25px; line-height: 1.6; }
                    .btn-group { display: flex; gap: 15px; flex-wrap: wrap; }
                    .btn { display: inline-block; padding: 12px 24px; background: linear-gradient(45deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 8px; transition: all 0.3s; font-weight: 500; text-align: center; min-width: 140px; }
                    .btn:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4); }
                    .btn-secondary { background: linear-gradient(45deg, #f093fb, #f5576c); }
                    .test-section { background: white; border-radius: 15px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }
                    .test-title { font-size: 1.5em; color: #333; margin-bottom: 20px; display: flex; align-items: center; }
                    .test-steps { background: #f8f9fa; padding: 20px; border-radius: 10px; }
                    .step { margin-bottom: 15px; padding: 10px; background: white; border-radius: 8px; border-left: 4px solid #667eea; }
                    .step-number { display: inline-block; background: #667eea; color: white; width: 25px; height: 25px; border-radius: 50%; text-align: center; line-height: 25px; margin-right: 10px; font-weight: bold; }
                    code { background: #e9ecef; padding: 2px 6px; border-radius: 4px; color: #d63384; font-family: 'Consolas', monospace; }
                    .highlight { background: #fff3cd; padding: 15px; border-radius: 8px; border-left: 4px solid #ffc107; margin: 20px 0; }
                    .stats { display: flex; justify-content: space-around; margin: 30px 0; }
                    .stat { text-align: center; color: white; }
                    .stat-number { font-size: 2em; font-weight: bold; display: block; }
                    .stat-label { font-size: 0.9em; opacity: 0.8; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🎓 学途在线教育平台 - API文档中心</h1>
                    
                    <div class="stats">
                        <div class="stat">
                            <span class="stat-number">3</span>
                            <span class="stat-label">微服务模块</span>
                        </div>
                        <div class="stat">
                            <span class="stat-number">80+</span>
                            <span class="stat-label">API接口</span>
                        </div>
                        <div class="stat">
                            <span class="stat-number">100%</span>
                            <span class="stat-label">网关鉴权</span>
                        </div>
                    </div>
                    
                    <div class="services-grid">
                        <div class="service-card">
                            <div class="service-title">
                                <span class="service-icon">👤</span>
                                用户服务 API
                            </div>
                            <div class="service-desc">提供用户注册、登录、JWT认证、用户管理等核心功能的API接口。包含完整的认证授权体系。</div>
                            <div class="btn-group">
                                <a href="http://localhost:8088/doc.html" class="btn" target="_blank">📖 查看文档</a>
                                <a href="/api/user/v3/api-docs" class="btn btn-secondary" target="_blank">📄 原始数据</a>
                            </div>
                        </div>
                        
                        <div class="service-card">
                            <div class="service-title">
                                <span class="service-icon">📚</span>
                                课程服务 API
                            </div>
                            <div class="service-desc">提供课程管理、分类管理、章节管理、<strong>标签管理</strong>等功能的API接口。包含完整的课程业务逻辑。</div>
                            <div class="btn-group">
                                <a href="http://localhost:8077/doc.html" class="btn" target="_blank">📖 查看文档</a>
                                <a href="/api/course/v3/api-docs" class="btn btn-secondary" target="_blank">📄 原始数据</a>
                            </div>
                        </div>
                        
                        <div class="service-card">
                            <div class="service-title">
                                <span class="service-icon">🛒</span>
                                订单服务 API
                            </div>
                            <div class="service-desc">提供订单管理、<strong>模拟支付</strong>、用户课程关系等功能的API接口。支持完整的课程购买流程。</div>
                            <div class="btn-group">
                                <a href="http://localhost:8078/doc.html" class="btn" target="_blank">📖 查看文档</a>
                                <a href="/api/order/v3/api-docs" class="btn btn-secondary" target="_blank">📄 原始数据</a>
                            </div>
                        </div>
                    </div>
                    
                    <div class="test-section">
                        <div class="test-title">
                            <span style="margin-right: 10px;">🔐</span>
                            网关统一鉴权测试流程
                        </div>
                        
                        <div class="highlight">
                            <strong>🎯 测试目标：</strong> 验证所有API请求都通过网关进行统一鉴权，包括标签管理和订单支付功能
                        </div>
                        
                        <div class="test-steps">
                            <div class="step">
                                <span class="step-number">1</span>
                                <strong>用户登录获取Token：</strong>
                                <code>POST http://localhost:8080/api/user/login</code>
                                <br><small>请求体：{"username": "admin", "password": "123456"}</small>
                            </div>
                            
                            <div class="step">
                                <span class="step-number">2</span>
                                <strong>配置认证头：</strong>
                                <code>Authorization: Bearer {从登录响应获取的token}</code>
                            </div>
                            
                            <div class="step">
                                <span class="step-number">3</span>
                                <strong>测试课程和订单功能：</strong>
                                <br>• 查询所有标签：<code>GET /api/course/tags</code>
                                <br>• 创建订单：<code>POST /api/order/orders</code>
                                <br>• 快捷支付：<code>POST /api/order/payments/quick-pay</code>
                                <br>• 查询我的课程：<code>GET /api/order/user-courses/my</code>
                            </div>
                            
                            <div class="step">
                                <span class="step-number">4</span>
                                <strong>验证鉴权：</strong>
                                不带Token访问应返回401未授权错误
                            </div>
                        </div>
                        
                        <div class="highlight">
                            <strong>💡 提示：</strong> 所有测试都通过网关地址 <code>http://localhost:8080</code> 进行，确保验证网关的路由和鉴权功能
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
            
        return Mono.just(html);
    }
}