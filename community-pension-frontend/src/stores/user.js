import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({

    /**
     * {
    "id": 1,
    "username": "zhangdaye",
    "password": "123456",
    "roleId": 1,
    "elderId": 1,
    "kinId": null,
    "staffId": null,
    "createTime": "2025-02-25T20:06:56.000+00:00",
    "updateTime": "2025-02-25T20:20:13.000+00:00"
}
     */
    elderInfo: {
      id: '',
      username: '',
      roleId: '',
      elderId: '',
      kinId: '',
      staffId: '',
      createTime: '',
      updateTime: '',
    },
    roleMap: {
      1: 'elder',
      2: 'kin',
      3: 'staff',
      4: 'admin',
      5: 'guest',
    },
  }),
  getters: {
    isAuthenticated: (state) => !!state.elderInfo,
    userRole: (state) => state.roleMap[state.elderInfo.roleId],
  },
  actions: {
    async login(credentials) {
      try {
        const response = await axios.post('/api/login', credentials);
        this.setElderInfo(response.data);
      } catch (error) {
        console.error('登录失败:', error);
      }
    },
    getElderInfo() {
      return this.elderInfo;
    },
    setElderInfo(elderInfo) {
      this.elderInfo = elderInfo;
      this.elderInfo.roleId = elderInfo.roleId || '1';
    },
    clearElderInfo() {
      this.elderInfo = { name: '', avatar: '', roleId: '1' };
    },
  },
})