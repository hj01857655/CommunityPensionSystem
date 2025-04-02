<template>
  <div class="email-container">
    <div class="header">
      <h2>社区养老服务预约通知</h2>
    </div>
    
    <div class="content">
      <p>尊敬的 <span class="highlight">{{ userName }}</span>：</p>
      
      <p>您的服务预约信息如下：</p>
      <ul>
        <li>预约编号：<span class="highlight">{{ orderId }}</span></li>
        <li>预约状态：<span class="highlight">{{ message }}</span></li>
        <li>预约时间：<span class="highlight">{{ scheduleTime }}</span></li>
        <li>服务项目：<span class="highlight">{{ serviceName }}</span></li>
      </ul>
      
      <p>如有任何疑问，请联系我们的客服人员。</p>
    </div>
    
    <div class="footer">
      <p>此邮件由系统自动发送，请勿回复。</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  userName: {
    type: String,
    required: true
  },
  orderId: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  scheduleTime: {
    type: String,
    required: true
  },
  serviceName: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.email-container {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  background-color: #f8f9fa;
  padding: 20px;
  text-align: center;
  border-radius: 5px;
  margin-bottom: 20px;
}

.content {
  background-color: #ffffff;
  padding: 20px;
  border: 1px solid #e9ecef;
  border-radius: 5px;
}

.footer {
  text-align: center;
  margin-top: 20px;
  color: #6c757d;
  font-size: 12px;
}

.highlight {
  color: #007bff;
  font-weight: bold;
}
</style> 