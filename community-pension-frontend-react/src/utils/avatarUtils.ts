/**
 * Avatar Utilities
 * 
 * Handles avatar URL processing and default avatar generation
 */

/**
 * Process avatar URL
 * - If avatar is a full URL (http/https), return as-is
 * - If avatar is a relative path, prepend base URL
 * - If avatar is empty, return default avatar
 */
export function getAvatarUrl(avatar: string | null | undefined): string {
  if (!avatar) {
    return getDefaultAvatar()
  }

  // If it's already a full URL, return as-is
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  // If it's a relative path, prepend the API base URL
  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  return `${baseURL}${avatar.startsWith('/') ? '' : '/'}${avatar}`
}

/**
 * Get default avatar URL
 */
export function getDefaultAvatar(): string {
  // You can return a default avatar image URL or a placeholder
  return 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png'
}

/**
 * Generate avatar placeholder based on name
 * Returns a data URL with the first character of the name
 */
export function generateAvatarPlaceholder(name: string, size = 100): string {
  const canvas = document.createElement('canvas')
  canvas.width = size
  canvas.height = size
  const ctx = canvas.getContext('2d')

  if (!ctx) return getDefaultAvatar()

  // Background colors
  const colors = [
    '#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1',
    '#13c2c2', '#eb2f96', '#fa8c16', '#a0d911', '#2f54eb'
  ]
  
  const firstChar = name.charAt(0).toUpperCase()
  const colorIndex = firstChar.charCodeAt(0) % colors.length
  const bgColor = colors[colorIndex]

  // Draw background circle
  ctx.fillStyle = bgColor
  ctx.beginPath()
  ctx.arc(size / 2, size / 2, size / 2, 0, Math.PI * 2)
  ctx.fill()

  // Draw text
  ctx.fillStyle = '#fff'
  ctx.font = `${size / 2}px Arial`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(firstChar, size / 2, size / 2)

  return canvas.toDataURL()
}
