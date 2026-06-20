import axios, { AxiosError } from 'axios'
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { message } from 'antd'
import { useAuthStore } from '@/store/authStore'
import type { ApiResponse } from '@/types/response'
import Cookies from 'js-cookie'

/**
 * Axios Instance Configuration
 * 
 * Security Features:
 * - Access Token in Authorization header
 * - Refresh Token in HttpOnly Cookie (handled by backend)
 * - Automatic token rotation on 401
 * - Token blacklist validation
 * - CSRF protection with credentials
 */

// Create axios instance
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  withCredentials: true, // Enable HttpOnly Cookie (Refresh Token)
})

// Token refresh lock (prevent multiple simultaneous refresh requests)
let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

// Subscribe to token refresh
const subscribeTokenRefresh = (callback: ((token: string) => void)) => {
  refreshSubscribers.push(callback)
}

// Notify all subscribers when token refreshed
const onRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token))
  refreshSubscribers = []
}

/**
 * Request Interceptor
 */
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Add Access Token to Authorization header
    const { accessToken } = useAuthStore.getState()
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }

    // Add CSRF token if available
    const csrfToken = Cookies.get('XSRF-TOKEN')
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken
    }

    return config
  },
  (error: AxiosError) => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

/**
 * Response Interceptor
 */
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message: msg } = response.data

    // Handle business error codes
    if (code !== undefined && code !== 200) {
      message.error(msg || 'Request failed')
      return Promise.reject(new Error(msg || 'Request failed'))
    }

    return response.data
  },
  async (error: AxiosError) => {
    const { response, config } = error

    // Network error
    if (!response) {
      message.error('Network error, please check your connection')
      return Promise.reject(error)
    }

    const { status } = response

    // Handle 401 Unauthorized - Token expired or invalid
    if (status === 401) {
      const originalRequest = config as InternalAxiosRequestConfig & { _retry?: boolean }

      // Prevent infinite retry loop
      if (originalRequest._retry) {
        // Token refresh failed, redirect to login
        const { clearAuth } = useAuthStore.getState()
        clearAuth()
        window.location.href = '/login'
        return Promise.reject(error)
      }

      // Token refresh in progress - queue the request
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`
            }
            resolve(request(originalRequest))
          })
        })
      }

      // Start token refresh
      originalRequest._retry = true
      isRefreshing = true

      try {
        // Call refresh token API (Refresh Token is in HttpOnly Cookie)
        // 注意：此处用裸 axios.post，绕过了 request 实例的响应拦截器，
        // 故返回的是完整 AxiosResponse（需 .data.data 解包），与拦截器内 request 调用不同。
        const refreshResponse = await axios.post<AxiosResponse<ApiResponse<{ accessToken: string }>>>(
          `${import.meta.env.VITE_API_BASE_URL || '/api'}/auth/refresh`,
          {},
          { withCredentials: true }
        )

        const { accessToken } = refreshResponse.data.data

        // Update Access Token in store
        const { setAccessToken } = useAuthStore.getState()
        setAccessToken(accessToken)

        // Update Authorization header
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
        }

        // Notify all queued requests
        onRefreshed(accessToken)
        isRefreshing = false

        // Retry original request
        return request(originalRequest)
      } catch (refreshError) {
        // Token refresh failed, logout
        isRefreshing = false
        refreshSubscribers = []
        
        const { clearAuth } = useAuthStore.getState()
        clearAuth()
        
        message.error('Session expired, please login again')
        window.location.href = '/login'
        
        return Promise.reject(refreshError)
      }
    }

    // Handle 403 Forbidden
    if (status === 403) {
      message.error('Access denied')
    }

    // Handle 404 Not Found
    if (status === 404) {
      message.error('Resource not found')
    }

    // Handle 500 Internal Server Error
    if (status === 500) {
      message.error('Server error, please try again later')
    }

    return Promise.reject(error)
  }
)

export default request
