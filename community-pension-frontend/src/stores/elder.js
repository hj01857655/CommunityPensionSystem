import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useElderStore = defineStore('elder', () => {
    // 老人信息
    const elderInfo = ref({
        name: '',
        age: 0,
        gender: '',
        healthStatus: '',
        contact: '',
    });

    // 设置老人信息
    const setElderInfo = (info) => {
        elderInfo.value = { ...elderInfo.value, ...info };
    };

    // 获取老人信息
    const getElderInfo = () => {
        return elderInfo.value;
    };

    // 清空老人信息
    const clearElderInfo = () => {
        elderInfo.value = {
            name: '',
            age: 0,
            gender: '',
            healthStatus: '',
            contact: '',
        };
    };

    return {
        elderInfo,
        setElderInfo,
        getElderInfo,
        clearElderInfo,
    };
});
