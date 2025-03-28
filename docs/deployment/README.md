# 部署文档

## 环境要求

### 基础环境
- JDK 17+
- Node.js 16+
- MySQL 8.4.0+
- Redis 6+

### 构建工具
- Maven 3.8+
- Yarn 1.22+

### 可选组件
- Nginx (用于生产环境前端部署)
- Prometheus (用于监控)

## 开发环境部署

### 1. 后端服务部署

#### 准备工作
1. 克隆项目
```bash
git clone https://github.com/yourusername/community-pension-system.git
cd community-pension-system
```

2. 配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE community_pension DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 执行SQL脚本
mysql -u root -p community_pension < docs/database/init.sql
```

3. 修改配置文件
```bash
# 修改 application-dev.yml
cd community-pension-admin
vim src/main/resources/application-dev.yml
```

配置示例：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_pension?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

注意事项：
- 默认使用root账号，密码为root
- 请确保数据库账号有足够的权限
- 生产环境建议使用加密方式存储密码并修改默认账号密码
- 修改后需要重启服务生效

4. 编译打包
```bash
mvn clean package -DskipTests
```

5. 运行服务
```bash
# 开发模式运行(带热部署)
mvn spring-boot:run

# 或者打包后运行
java -jar target/community-pension-admin.jar
```

### 2. 前端项目部署
1. 安装依赖
```bash
cd community-pension-frontend
yarn install
```

2. 修改环境配置
```bash
# 修改 .env.development
vim .env.development
```

3. 开发环境运行
```bash
yarn dev
```

4. 生产环境打包
```bash
yarn build
```

## 生产环境部署

### 1. 后端服务部署
1. 准备服务器环境
```bash
# 安装JDK
sudo apt update
sudo apt install openjdk-17-jdk

# 安装MySQL
sudo apt install mysql-server

# 安装Redis
sudo apt install redis-server
```

2. 配置服务
```bash
# 创建服务用户
sudo useradd -r -s /bin/false communitypension

# 创建应用目录
sudo mkdir -p /opt/communitypension
sudo chown -R communitypension:communitypension /opt/communitypension
```

3. 配置systemd服务
```bash
sudo vim /etc/systemd/system/communitypension.service
```

```ini
[Unit]
Description=Community Pension System
After=network.target

[Service]
Type=simple
User=communitypension
WorkingDirectory=/opt/communitypension
ExecStart=/usr/bin/java -jar community-pension-admin.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

4. 启动服务
```bash
sudo systemctl daemon-reload
sudo systemctl enable communitypension
sudo systemctl start communitypension
```

### 2. 前端项目部署
1. 安装Nginx
```bash
sudo apt install nginx
```

2. 配置Nginx
```bash
sudo vim /etc/nginx/sites-available/communitypension
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/communitypension;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

3. 启用站点配置
```bash
sudo ln -s /etc/nginx/sites-available/communitypension /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

4. 部署前端文件
```bash
sudo mkdir -p /var/www/communitypension
sudo cp -r dist/* /var/www/communitypension/
sudo chown -R www-data:www-data /var/www/communitypension
```

## 监控和维护

### 1. 日志管理
- 后端日志：`/opt/communitypension/logs/`
- Nginx日志：`/var/log/nginx/`
- 系统日志：`/var/log/syslog`

### 2. 备份策略
1. 数据库备份
```bash
# 创建备份脚本
vim /opt/communitypension/backup.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/opt/communitypension/backups"
DATE=$(date +%Y%m%d)
mysqldump -u root -p community_pension > $BACKUP_DIR/backup_$DATE.sql
```

2. 配置文件备份
```bash
# 定期备份配置文件
cp /opt/communitypension/application.yml /opt/communitypension/backups/
```

### 3. 性能监控
1. 系统监控
```bash
# 安装监控工具
sudo apt install prometheus node-exporter
```

2. 应用监控
```bash
# 配置Spring Boot Actuator
# 在application.yml中添加配置
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

## 故障处理
1. 服务无法启动
   - 检查日志文件
   - 检查端口占用
   - 检查配置文件

2. 数据库连接失败
   - 检查数据库服务状态
   - 验证数据库连接信息
   - 检查防火墙设置

3. 前端访问异常
   - 检查Nginx配置
   - 验证静态资源
   - 检查网络连接