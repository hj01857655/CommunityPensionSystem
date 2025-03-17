export function getAvatarUrl(avatarPath) {
  const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
  if (!avatarPath) return defaultAvatar;

  // 如果已经是完整的URL，直接返回
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath;
  }

  // 如果是相对路径，拼接服务器基础路径和 src/assets 目录
  const baseUrl = import.meta.env.PROD ? 'http://127.0.0.1:9000' : '';
  const fullPath = `${baseUrl}/src/assets${avatarPath}`;
  return fullPath;
}