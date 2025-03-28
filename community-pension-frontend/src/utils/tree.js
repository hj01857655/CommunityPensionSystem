/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 * @returns {Array} 树形结构数据
 */
export function handleTree(data, id, parentId, children) {
  // 默认配置
  const config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  // 存储所有节点的映射
  const childrenListMap = {};
  const tree = [];

  // 第一次遍历：建立节点映射
  for (const item of data) {
    const nodeId = item[config.id];
    childrenListMap[nodeId] = item;
    
    // 确保每个节点都有 children 数组
    if (!item[config.childrenList]) {
      item[config.childrenList] = [];
    }
  }

  // 第二次遍历：构建树形结构
  for (const item of data) {
    const parentId = item[config.parentId];
    const parentNode = childrenListMap[parentId];
    
    if (!parentNode) {
      // 如果没有父节点，则作为根节点
      tree.push(item);
    } else {
      // 将当前节点添加到父节点的 children 数组中
      parentNode[config.childrenList].push(item);
    }
  }

  return tree;
}

/**
 * 查找树形结构中的节点
 * @param {Array} tree 树形结构数据
 * @param {Function} predicate 判断函数
 * @param {string} childrenKey 子节点键名
 * @returns {Object|null} 找到的节点或null
 */
export function findTreeNode(tree, predicate, childrenKey = 'children') {
  if (!Array.isArray(tree)) return null;
  
  for (const node of tree) {
    if (predicate(node)) return node;
    
    if (node[childrenKey]) {
      const found = findTreeNode(node[childrenKey], predicate, childrenKey);
      if (found) return found;
    }
  }
  
  return null;
}

/**
 * 遍历树形结构
 * @param {Array} tree 树形结构数据
 * @param {Function} callback 回调函数
 * @param {string} childrenKey 子节点键名
 */
export function traverseTree(tree, callback, childrenKey = 'children') {
  if (!Array.isArray(tree)) return;
  
  for (const node of tree) {
    callback(node);
    if (node[childrenKey]) {
      traverseTree(node[childrenKey], callback, childrenKey);
    }
  }
}