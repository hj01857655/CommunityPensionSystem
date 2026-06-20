/**
 * Design Tokens - 设计令牌（统一的设计变量）
 * 用于 Ant Design + Tailwind CSS 主题统一管理
 */

export const designTokens = {
  // Seed Tokens（基础令牌）
  colors: {
    primary: '#1890ff',
    success: '#52c41a',
    warning: '#faad14',
    error: '#f5222d',
    info: '#1890ff',
    
    // 灰度色系
    gray: {
      50: '#fafafa',
      100: '#f5f5f5',
      200: '#e8e8e8',
      300: '#d9d9d9',
      400: '#bfbfbf',
      500: '#8c8c8c',
      600: '#595959',
      700: '#434343',
      800: '#262626',
      900: '#1f1f1f',
    }
  },
  
  spacing: {
    xs: '8px',
    sm: '12px',
    md: '16px',
    lg: '24px',
    xl: '32px',
    '2xl': '48px',
  },
  
  borderRadius: {
    sm: '2px',
    base: '4px',
    md: '6px',
    lg: '8px',
    xl: '12px',
  },
  
  fontSize: {
    xs: '12px',
    sm: '14px',
    base: '16px',
    lg: '18px',
    xl: '20px',
    '2xl': '24px',
    '3xl': '30px',
  },
  
  fontWeight: {
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  }
} as const;

// Dark Mode Tokens
export const darkTokens = {
  colors: {
    primary: '#177ddc',
    gray: {
      50: '#1f1f1f',
      100: '#262626',
      200: '#434343',
      300: '#595959',
      400: '#8c8c8c',
      500: '#bfbfbf',
      600: '#d9d9d9',
      700: '#e8e8e8',
      800: '#f5f5f5',
      900: '#fafafa',
    }
  }
};

export type DesignTokens = typeof designTokens;
