import { getList as getActivityList } from '@/api/back/activity'
import * as checkinApi from '@/api/back/activity/checkin'
import * as registerApi from '@/api/back/activity/register'
import { getUserList } from '@/api/back/system/user'
import { defineStore } from 'pinia'

// 活动报名管理的Store
export const useParticipateStore = defineStore('back-participate', {
  state: () => ({
    // 列表数据
    list: [],
    // 总数
    total: 0,
    // 加载状态
    loading: false,
    // 活动选项
    activityOptions: [],
    // 老人选项
    elderOptions: [],
    // 最后一次查询参数
    lastQuery: {
      pageNum: 1,
      pageSize: 10
    }
  }),

  getters: {
    // 获取状态类型映射
    statusTypeMap: () => ({
      0: 'warning',  // 待审核 - 等待管理员审核
      1: 'success',  // 已通过 - 审核通过，报名成功
      2: 'danger',   // 已拒绝 - 审核拒绝，报名失败
      3: 'info',     // 已取消 - 报名取消
      4: 'primary'   // 已签到 - 报名成功并签到
    }),

    // 获取状态文本映射
    statusTextMap: () => ({
      0: '待审核',  // 等待管理员审核
      1: '已通过',  // 审核通过，报名成功
      2: '已拒绝',  // 审核拒绝，报名失败
      3: '已取消',  // 报名取消
      4: '已签到'   // 报名成功并签到
    })
  },

  actions: {
    // 统一错误处理函数
    handleError(error, message) {
      console.error(message, error)
      // 如果是API响应错误，提取错误信息
      if (error.response && error.response.data && error.response.data.msg) {
        throw new Error(error.response.data.msg)
      }
      throw error
    },

    // 获取参与记录列表
    async getList(query) {
      this.loading = true
      try {
        // 保存最后一次查询参数，用于后续刷新
        if (query) {
          this.lastQuery = { ...query }
        }

        const response = await registerApi.getList(query)
        this.list = response.data.records
        this.total = response.data.total
        return response
      } catch (error) {
        this.handleError(error, '获取参与记录列表失败')
      } finally {
        this.loading = false
      }
    },

    // 签到操作
    async checkin(id, checkInUserId) {
      try {
        // 检查签到人ID是否存在
        if (!checkInUserId) {
          throw new Error('签到人ID不能为空');
        }

        // 使用签到API
        const response = await checkinApi.create({
          registerId: Number(id), // 确保是数字类型
          checkInUserId: Number(checkInUserId), // 使用传入的用户ID
          isProxyCheckIn: 0 // 0表示本人签到
        });

        // 如果签到成功，使用最后一次查询参数刷新列表
        if (response && response.code === 200) {
          await this.getList(this.lastQuery);
        }

        return response;
      } catch (error) {
        this.handleError(error, '签到失败');
      }
    },

    // 取消报名 - 通过将状态设置为"已取消"实现
    async cancel(id) {
      try {
        const response = await registerApi.audit(id, {
          status: 3, // 已取消状态
          remark: '用户主动取消报名'
        });
        // 取消成功后刷新列表
        if (response && response.code === 200) {
          await this.getList(this.lastQuery);
        }
        return response;
      } catch (error) {
        this.handleError(error, '取消报名失败');
      }
    },

    // 审核报名
    async audit(data) {
      try {
        const response = await registerApi.audit(data.id, {
          status: data.status,
          remark: data.remark
        })
        // 审核成功后刷新列表
        if (response && response.code === 200) {
          await this.getList(this.lastQuery);
        }
        return response
      } catch (error) {
        this.handleError(error, '审核报名失败');
      }
    },

    // 批量审核报名
    async batchAudit(data) {
      try {
        const response = await registerApi.batchAudit(data)
        // 批量审核成功后刷新列表
        if (response && response.code === 200) {
          await this.getList(this.lastQuery);
        }
        return response
      } catch (error) {
        this.handleError(error, '批量审核报名失败');
      }
    },

    // 获取活动选项 - 添加缓存机制
    async getActivityOptions(forceRefresh = false) {
      try {
        // 如果已有数据且不强制刷新，直接返回缓存数据
        if (this.activityOptions.length > 0 && !forceRefresh) {
          return this.activityOptions;
        }

        const response = await getActivityList({ pageSize: 100 })
        this.activityOptions = response.data.records.map(item => ({
          id: item.id,
          title: item.title
        }))
        return this.activityOptions
      } catch (error) {
        this.handleError(error, '获取活动选项失败');
        return []
      }
    },

    // 获取老人选项 - 添加缓存机制
    async getElderOptions(forceRefresh = false) {
      try {
        // 如果已有数据且不强制刷新，直接返回缓存数据
        if (this.elderOptions.length > 0 && !forceRefresh) {
          return this.elderOptions;
        }

        const response = await getUserList({ pageSize: 100, userType: 'elder' })
        this.elderOptions = response.data.records.map(item => ({
          id: item.userId,
          name: item.name,
          phone: item.phone
        }))
        return this.elderOptions
      } catch (error) {
        this.handleError(error, '获取老人选项失败');
        return []
      }
    },

    // 获取报名统计
    async getStats(activityId) {
      try {
        const response = await registerApi.getStats(activityId)
        return response.data
      } catch (error) {
        this.handleError(error, '获取报名统计失败');
      }
    },

    // 导出报名记录
    async exportList(params) {
      try {
        return await registerApi.exportList(params)
      } catch (error) {
        this.handleError(error, '导出报名记录失败');
      }
    }
  }
})
