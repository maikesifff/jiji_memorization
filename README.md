# 记忆项目 (Jiji Memorization)

这是一个基于Spring Boot和Vue3的前后端分离项目。

## 项目结构

```
jiji_memorization/
├── jiji_memorization_backend/    # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/jiji/
│   │   │   │   ├── controller/   # 控制器层
│   │   │   │   ├── config/       # 配置类
│   │   │   │   └── JijiMemorizationBackendApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── pom.xml                   # Maven配置
│   └── settings.xml              # Maven镜像配置
├── jiji_memorization_frontend/   # Vue3前端
│   ├── src/
│   │   ├── components/           # Vue组件
│   │   ├── views/                # 页面组件
│   │   ├── router/               # 路由配置
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
├── setup-environment.ps1         # 环境配置脚本
├── start-services.ps1            # 一键启动前后端服务
├── start-backend.ps1             # 单独启动后端服务
├── start-frontend.ps1            # 单独启动前端服务
├── test-services.ps1             # 服务状态检查脚本
├── test-mysql-connection.ps1     # MySQL连接测试脚本
├── .gitignore
└── README.md
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven 3.9.6
- Java 17

### 前端
- Vue 3.3.8
- Vue Router 4.2.5
- Pinia 2.1.7
- Axios 1.6.2
- Vite 5.0.0

## 环境要求

- Java 17 或更高版本
- Node.js 16 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6 或更高版本

## 快速开始

### 1. 环境配置

首次使用需要配置环境变量：

```powershell
# 运行环境配置脚本
.\setup-environment.ps1
```

此脚本会：
- 设置 JAVA_HOME 环境变量
- 设置 MAVEN_HOME 环境变量
- 更新 PATH 环境变量
- 配置完成后需要重新打开PowerShell窗口

### 2. 数据库配置

确保MySQL服务正在运行，并创建数据库：

```sql
CREATE DATABASE jiji_memorization CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 启动服务

#### 方式一：使用启动脚本（推荐）

**一键启动前后端：**
```powershell
.\start-services.ps1
```

**单独启动后端：**
```powershell
.\start-backend.ps1
```

**单独启动前端：**
```powershell
.\start-frontend.ps1
```

#### 方式二：手动启动

**启动后端：**
```powershell
cd jiji_memorization_backend
mvn -s settings.xml spring-boot:run
```

**启动前端：**
```powershell
cd jiji_memorization_frontend
npm install
npm run dev
```

### 4. 验证服务

```powershell
# 检查服务状态
.\test-services.ps1
```

## 管理脚本说明

### setup-environment.ps1
**功能：** 配置开发环境变量
**使用场景：** 首次安装或环境变量丢失时
**执行内容：**
- 设置 JAVA_HOME 指向 Java 17 安装目录
- 设置 MAVEN_HOME 指向 Maven 安装目录
- 更新 PATH 环境变量
- 验证配置结果

### start-services.ps1
**功能：** 一键启动前后端服务
**使用场景：** 日常开发启动项目
**执行内容：**
- 设置临时环境变量
- 在新PowerShell窗口中启动后端服务
- 在新PowerShell窗口中启动前端服务
- 显示访问地址信息

### start-backend.ps1
**功能：** 单独启动后端服务
**使用场景：** 只需要后端服务时
**执行内容：**
- 设置Java和Maven环境变量
- 显示Java版本信息
- 启动Spring Boot应用
- 在当前窗口显示启动日志

### start-frontend.ps1
**功能：** 单独启动前端服务
**使用场景：** 只需要前端服务时
**执行内容：**
- 检查并安装依赖（如果需要）
- 启动Vue.js开发服务器
- 在当前窗口显示启动日志

### test-services.ps1
**功能：** 检查服务运行状态
**使用场景：** 验证服务是否正常启动
**检查内容：**
- 后端服务 (http://localhost:8080/api/hello)
- 前端服务 (http://localhost:3000)
- 显示详细的连接状态和响应信息

### test-mysql-connection.ps1
**功能：** 测试MySQL数据库连接
**使用场景：** 排查数据库连接问题
**检查内容：**
- MySQL服务状态
- 数据库连接参数
- 连接测试结果

## 访问地址

- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:8080/api/hello
- **数据库测试**: http://localhost:8080/api/database/test

## API接口

### 基础接口
- `GET /api/hello` - 测试接口，返回欢迎消息

### 数据库接口
- `GET /api/database/test` - 数据库连接测试接口

## 开发说明

### 后端开发
- 使用Spring Boot 3.2.0框架
- 配置了MySQL数据库连接
- 使用JPA进行数据持久化
- 配置了CORS跨域支持
- 使用Maven进行依赖管理

### 前端开发
- 使用Vue 3 Composition API
- 配置了Vue Router进行路由管理
- 使用Pinia进行状态管理
- 配置了Axios进行HTTP请求
- 使用Vite作为构建工具

### 数据库配置
- 数据库类型：MySQL 8.0
- 连接地址：localhost:3306
- 数据库名：jiji_memorization
- 用户名：root
- 密码：root
- 字符集：utf8mb4

## 故障排除

### 常见问题

1. **Java版本问题**
   ```powershell
   java -version
   # 确保显示Java 17或更高版本
   ```

2. **Maven未找到**
   ```powershell
   mvn --version
   # 如果命令不存在，运行 setup-environment.ps1
   ```

3. **端口被占用**
   ```powershell
   netstat -ano | findstr ":8080"
   netstat -ano | findstr ":3000"
   # 检查端口占用情况
   ```

4. **数据库连接失败**
   ```powershell
   .\test-mysql-connection.ps1
   # 测试数据库连接
   ```

5. **服务启动失败**
   - 检查环境变量是否正确设置
   - 确认MySQL服务正在运行
   - 查看后端启动日志

### 日志查看

**后端日志：** 在启动后端的PowerShell窗口中查看
**前端日志：** 在启动前端的PowerShell窗口中查看

## 项目特性

- ✅ 前后端分离架构
- ✅ CORS跨域配置
- ✅ 路由管理
- ✅ 状态管理
- ✅ API代理配置
- ✅ 数据库集成
- ✅ 环境配置脚本
- ✅ 服务管理脚本

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。

