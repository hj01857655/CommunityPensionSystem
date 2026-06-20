import type { Config } from 'tailwindcss'
import { designTokens } from './src/theme/tokens'

const config: Config = {
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: designTokens.colors.primary,
        success: designTokens.colors.success,
        warning: designTokens.colors.warning,
        error: designTokens.colors.error,
        info: designTokens.colors.info,
        gray: designTokens.colors.gray,
      },
      spacing: designTokens.spacing,
      borderRadius: designTokens.borderRadius,
      fontSize: designTokens.fontSize,
      fontWeight: designTokens.fontWeight,
    },
  },
  // 使用 CSS Variables 模式，与 Ant Design 主题集成
  corePlugins: {
    preflight: false, // 禁用 Tailwind 的 base styles，避免与 Ant Design 冲突
  },
  plugins: [],
}

export default config
