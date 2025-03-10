import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getElderById, updateElder } from '@/api/elder';

export const useElderStore = defineStore('elder', () => {
    // 老人信息
    const elderInfo = ref({
        id: '',
        name: '',
        age: 0,
        gender: '',
        birthday: '',
        idCard: '',
        phone: '',
        address: '',
        avatar: '',
        healthCondition: '',
        emergencyContactName: '',
        emergencyContactPhone: '',
        remarks: ''
    });

    // 计算属性
    const fullName = computed(() => {
        return elderInfo.value.name || '未登录';
    });
    // 头像
    const avatarUrl = computed(() => {
        if (elderInfo.value.avatar) {
            return `http://127.0.0.1:8081/src/assets/${elderInfo.value.avatar}`;
        }
        return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    });

    // 初始化老人信息（从localStorage获取）
    const initElderInfo = () => {
        //调用登录初始化老人信息
        const storedElderInfo = localStorage.getItem('elderInfo');
        if (storedElderInfo) {
            elderInfo.value = JSON.parse(storedElderInfo);
            return true;
        }
        return false;
    };

    // 设置老人信息
    const setElderInfo = (info) => {
        elderInfo.value = { ...elderInfo.value, ...info };
    };

    // 获取老人信息
    const getElderInfo = () => {
        return elderInfo.value;
    };

    const getElderId = () => {
        return elderInfo.value.id;
    };
    // 从服务器获取老人信息存储到
    const fetchElderInfo = async (elderId) => {
        try {
            const response = await getElderById({ id: elderId });
            if (response.success) {
                const elderData = response.data.data;
                setElderInfo(elderData);
                return { success: true, data: elderData };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '获取老人信息失败' };
        }
    };

    // 更新老人信息
    const updateElderInfo = async (elderData) => {
        try {
            const response = await updateElder(elderData);
            if (response.success) {
                setElderInfo(elderData);
                return { success: true };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '更新老人信息失败' };
        }
    };

    // 清空老人信息
    const clearElderInfo = () => {
        elderInfo.value = {
            id: '',
            name: '',
            age: 0,
            gender: '',
            birthday: '',
            idCard: '',
            phone: '',
            address: '',
            avatar: '',
            healthCondition: '',
            emergencyContactName: '',
            emergencyContactPhone: '',
            remarks: ''
        };
        localStorage.removeItem('elderInfo');
    };

    return {
        elderInfo,
        fullName,
        avatarUrl,
        initElderInfo,
        setElderInfo,
        getElderInfo,
        fetchElderInfo,
        updateElderInfo,
        clearElderInfo,
    };
});
