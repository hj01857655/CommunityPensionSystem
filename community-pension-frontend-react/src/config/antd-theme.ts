import type { ThemeConfig } from 'antd'
import { designTokens, darkTokens } from '@/theme/tokens'

/**
 * Ant Design Theme Configuration
 * 使用 CSS Variables 模式以支持动态主题切换
 */
export const lightTheme: ThemeConfig = {
  token: {
    colorPrimary: designTokens.colors.primary,
    colorSuccess: designTokens.colors.success,
    colorWarning: designTokens.colors.warning,
    colorError: designTokens.colors.error,
    colorInfo: designTokens.colors.info,
    
    // 圆角
    borderRadius: 6,
    borderRadiusLG: 8,
    borderRadiusSM: 4,
    
    // 字体
    fontSize: 16,
    fontSizeHeading1: 30,
    fontSizeHeading2: 24,
    fontSizeHeading3: 20,
    fontSizeHeading4: 18,
    fontSizeHeading5: 16,
    
    // 间距
    padding: 16,
    paddingLG: 24,
    paddingSM: 12,
    paddingXS: 8,
    
    // 无障碍优化（适老化设计）
    controlHeight: 40, // 增大控件高度，便于点击
    controlHeightLG: 48,
    controlHeightSM: 32,
  },
  components: {
    Button: {
      controlHeight: 40,
      controlHeightLG: 48,
      fontSize: 16,
      paddingContentHorizontal: 24,
    },
    Input: {
      controlHeight: 40,
      fontSize: 16,
    },
    Select: {
      controlHeight: 40,
      fontSize: 16,
    },
    Table: {
      fontSize: 16,
      cellPaddingBlock: 16,
    },
  },
}

export const darkTheme: ThemeConfig = {
  ...lightTheme,
  token: {
    ...lightTheme.token,
    colorPrimary: darkTokens.colors.primary,
  },
  algorithm: undefined, // Will be set to theme.darkAlgorithm dynamically
}
