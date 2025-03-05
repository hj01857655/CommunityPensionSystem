import axios from '@/utils/axios'

// 分页获取角色列表
export const getRoleList = (params) => {
    return axios.get('/api/roles', {
        params: {
            page: params?.page ?? 1,
            size: params?.size ?? 10
        }
    }).then(response => response.data)
      .catch(error => {
          return {
              status: error.response.status,
              message: error.response.data.message
          };
      });
}

// 创建角色
export const createRole = async (data) => {
    try {
        const response = await axios.post('/api/roles', data);
        return response.data;
    } catch (error) {
        if (error.response.status === 400) {
            return {
                status: 400,
                message: "角色名称已存在"
            };
        }
        return {
            status: error.response.status,
            message: error.response.data.message
        };
    }
}

// 更新角色
export const updateRole = (id, data) => {
    return axios.put(`/api/roles/${id}`, data)
        .then(response => response.data)
        .catch(error => {
            return {
                status: error.response.status,
                message: error.response.data.message
            };
        });
}

// 删除角色
export const deleteRole = (id) => {
    return axios.delete(`/api/roles/${id}`)
        .then(response => response.data)
        .catch(error => {
            return {
                status: error.response.status,
                message: error.response.data.message
            };
        });
}

// 获取角色详情
export const getRoleDetail = (id) => {
    return axios.get(`/api/roles/${id}`)
        .then(response => response.data)
        .catch(error => {
            return {
                status: error.response.status,
                message: error.response.data.message
            };
        });
}