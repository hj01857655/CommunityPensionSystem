/**
 * Enums for the application
 * 
 * IMPORTANT: These enums must match backend definitions exactly!
 */

export enum UserRole {
  Admin = 'admin',
  Staff = 'staff',
  Elder = 'elder',
  Kin = 'kin',
}

/**
 * Activity Status Enum
 * Backend: dict_data (activity_status)
 * 0-筹备中(Preparing) 1-报名中(Registering) 2-进行中(InProgress) 3-已结束(Ended) 4-已取消(Cancelled)
 */
export enum ActivityStatus {
  Preparing = 0,
  Registering = 1,
  InProgress = 2,
  Ended = 3,
  Cancelled = 4,
}

/**
 * Activity Register Status Enum
 * 0-待审核 1-已通过 2-已拒绝 3-已取消
 */
export enum ActivityRegisterStatus {
  Pending = 0,
  Approved = 1,
  Rejected = 2,
  Cancelled = 3,
}

/**
 * Activity Register Type Enum
 * 0-自主报名(Self) 1-代理报名(Proxy)
 */
export enum ActivityRegisterType {
  Self = 0,
  Proxy = 1,
}

/**
 * Service Order Status Enum
 * Backend: ServiceOrderStatus.java
 * 0-待审核(Pending) 1-已派单(Assigned) 2-服务中(InProgress) 3-已完成(Completed) 4-已取消(Cancelled) 5-已拒绝(Rejected)
 */
export enum ServiceOrderStatus {
  Pending = 0,
  Assigned = 1,
  InProgress = 2,
  Completed = 3,
  Cancelled = 4,
  Rejected = 5,
}

/**
 * Service Item Status Enum
 * 0-正常(Active) 1-停用(Disabled)
 */
export enum ServiceItemStatus {
  Active = 0,
  Disabled = 1,
}
