# 🚀 IP地址配置指南

## 📋 概述
本项目已优化配置，支持灵活修改IP地址，无需修改代码。

## 🔧 前端配置

### 方法1：环境变量文件（推荐）
1. 复制 `jiji_memorization_frontend/env.example` 为 `.env.local`
2. 修改 `VITE_API_BASE_URL` 值：
   ```bash
   # 本地开发
   VITE_API_BASE_URL=http://localhost:8080
   
   # 局域网其他设备
   VITE_API_BASE_URL=http://192.168.1.100:8080
   
   # 公网服务器
   VITE_API_BASE_URL=http://your-server.com:8080
   ```

### 方法2：系统环境变量
设置系统环境变量 `VITE_API_BASE_URL`

## 🔧 后端配置

### 方法1：系统环境变量
```bash
# Windows PowerShell
$env:DB_HOST="192.168.1.100"
$env:SERVER_PORT="8080"

# Windows CMD
set DB_HOST=192.168.1.100
set SERVER_PORT=8080

# Linux/Mac
export DB_HOST=192.168.1.100
export SERVER_PORT=8080
```

### 方法2：启动脚本
修改启动脚本，在启动前设置环境变量：
```powershell
# start-backend.ps1
$env:DB_HOST="192.168.1.100"
$env:SERVER_PORT="8080"
mvn spring-boot:run
```

## 🌐 常见场景

### 1. 局域网开发
- 前端：`VITE_API_BASE_URL=http://192.168.1.100:8080`
- 后端：`DB_HOST=192.168.1.100`

### 2. 生产部署
- 前端：`VITE_API_BASE_URL=http://your-domain.com`
- 后端：`DB_HOST=your-db-server.com`

### 3. Docker部署
```yaml
environment:
  - DB_HOST=mysql-container
  - SERVER_PORT=8080
```

## ✅ 优势

1. **零代码修改**：只需修改配置文件
2. **环境隔离**：开发/测试/生产环境独立配置
3. **团队协作**：每个人可以有自己的本地配置
4. **部署灵活**：支持各种部署环境

## 📝 注意事项

1. 修改配置后需要重启服务
2. 确保防火墙允许相应端口访问
3. 数据库服务器需要允许远程连接
4. 生产环境请使用强密码和HTTPS

## 🔍 验证配置

### 前端验证
访问前端页面，检查网络请求是否指向正确的API地址

### 后端验证
查看启动日志，确认数据库连接地址和端口
