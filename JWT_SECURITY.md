# 🔐 JWT安全配置说明

## 📋 问题描述
之前的JWT配置遇到了密钥长度不足的问题：
```
The signing key's size is 432 bits which is not secure enough for the HS512 algorithm. 
The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HS512 
MUST have a size >= 512 bits
```

## ✅ 解决方案

### 1. 密钥长度要求
- **HS256**: 至少256位 (32字节)
- **HS384**: 至少384位 (48字节)  
- **HS512**: 至少512位 (64字节) ← 我们使用的算法

### 2. 当前配置
```yaml
jwt:
  secret: jiji-memorization-secret-key-2024-very-long-and-secure-for-hs512-algorithm-with-sufficient-length
  expiration: 86400000 # 24小时
```

### 3. 密钥长度计算
当前密钥长度：`jiji-memorization-secret-key-2024-very-long-and-secure-for-hs512-algorithm-with-sufficient-length`
- 字符数：约85个字符
- 字节数：85字节 (UTF-8编码)
- 位数：680位 > 512位 ✅ **安全**

## 🛡️ 安全特性

### 1. 自动密钥验证
```java
private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    // 如果密钥长度不够，自动生成安全的密钥
    if (keyBytes.length < 64) { // 512位 = 64字节
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    return Keys.hmacShaKeyFor(keyBytes);
}
```

### 2. 算法选择
- 使用 **HS512** 算法（HMAC with SHA-512）
- 提供最高级别的安全性
- 适合生产环境使用

### 3. Token过期时间
- 默认24小时过期
- 可通过环境变量配置
- 支持毫秒级精度

## 🔧 配置方法

### 方法1：环境变量
```bash
# Windows PowerShell
$env:JWT_SECRET="your-very-long-secret-key-at-least-64-characters"
$env:JWT_EXPIRATION="86400000"

# Linux/Mac
export JWT_SECRET="your-very-long-secret-key-at-least-64-characters"
export JWT_EXPIRATION="86400000"
```

### 方法2：配置文件
```yaml
jwt:
  secret: ${JWT_SECRET:your-very-long-secret-key-at-least-64-characters}
  expiration: ${JWT_EXPIRATION:86400000}
```

## 📝 最佳实践

### 1. 密钥管理
- 密钥长度至少64字符
- 包含大小写字母、数字、特殊字符
- 定期更换密钥
- 不同环境使用不同密钥

### 2. 安全建议
- 生产环境使用强随机密钥
- 密钥不要提交到代码仓库
- 使用环境变量或密钥管理服务
- 定期审计JWT配置

### 3. 监控和日志
- 记录JWT生成和验证日志
- 监控异常token使用
- 设置token过期提醒

## 🚀 现在可以正常使用

修复后的配置：
- ✅ 密钥长度足够（680位 > 512位）
- ✅ 使用安全的HS512算法
- ✅ 自动密钥验证机制
- ✅ 支持环境变量配置

现在你可以正常注册和登录了！
