import axios from '@/utils/axios'

//获取老人健康档案
export const getHealthRecords = async (elderId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/health-records/getHealthRecords', {
            headers: {
                Authorization: `${token}`
            },
            params: {
                elderId: elderId
            }
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('获取健康记录失败:', error);
        return { success: false, error: error.message };
    }
};

//更新老人健康档案
export const updateHealthRecords = async (healthRecords) => {
    try {
        const token = localStorage.getItem('token');
        //@PutMapping("/{id}")
        const response = await axios.put('/health-records', healthRecords, {
            headers: {
                Authorization: `${token}`
            }
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('更新健康记录失败:', error);
        return { success: false, error: error.message };
    }
};


