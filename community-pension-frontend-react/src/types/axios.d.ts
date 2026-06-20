import 'axios'

/**
 * Axios 模块增强
 *
 * request.ts 的响应拦截器在成功时 `return response.data`（即后端的 ApiResponse<T> 信封），
 * 因此实例方法在运行时 resolve 的是 T 本身，而非默认的 AxiosResponse<T>。
 * 这里重写实例方法签名，让类型与拦截器的真实返回保持一致——
 * 调用方写 `request.get<ApiResponse<Foo>>()` 即可拿到 `ApiResponse<Foo>`，无需 `.data` 解包。
 */
declare module 'axios' {
  interface AxiosInstance {
    request<T = unknown>(config: AxiosRequestConfig): Promise<T>
    get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
    delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
    head<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
    options<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
    post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
    put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
    patch<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  }
}
