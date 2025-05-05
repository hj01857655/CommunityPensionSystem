/**
 * 空的SSE客户端服务（已禁用）
 */

class SseClient {
    constructor() {
        this.connected = false;
    }

    initStore() {
        // 空实现
    }

    async connect() {
        // 空实现
    }

    disconnect() {
        // 空实现
    }

    addEventListener() {
        // 空实现
    }

    removeEventListener() {
        // 空实现
    }

    dispatchEvent() {
        // 空实现
    }

    isConnected() {
        return false;
    }
}

// 创建空的SSE客户端实例
const sseClient = new SseClient();

// 修改为命名导出
export { sseClient };
