import {utils, writeFile} from 'xlsx';

/**
 * 将数据导出为Excel文件
 * @param {Object} data 要导出的数据
 * @param {string} filename 文件名（不包含扩展名）
 */
export const exportToExcel = (data, filename) => {
    try {
        // 转换数据为工作表可用格式
        const workSheetData = convertDataToWorksheet(data);

        // 创建工作簿
        const workBook = utils.book_new();

        // 创建工作表
        const workSheet = utils.json_to_sheet(workSheetData);

        // 设置列宽
        workSheet['!cols'] = calculateColumnWidths(workSheetData);

        // 将工作表添加到工作簿
        utils.book_append_sheet(workBook, workSheet, 'Sheet1');

        // 导出文件
        writeFile(workBook, `${filename}.xlsx`);

        return true;
    } catch (error) {
        console.error('导出Excel失败:', error);
        throw new Error('导出失败');
    }
};

/**
 * 计算列宽
 * @param {Array} data 工作表数据
 * @returns {Array} 列宽配置
 */
const calculateColumnWidths = (data) => {
    if (!data || data.length === 0) return [];

    const columnWidths = {};

    // 遍历所有数据行
    data.forEach(row => {
        Object.keys(row).forEach(key => {
            const value = String(row[key] || '');
            // 计算当前值的长度（中文字符计为2，其他字符计为1）
            const length = value.split('').reduce((acc, char) => {
                return acc + (char.match(/[\u4e00-\u9fa5]/) ? 2 : 1);
            }, 0);

            // 更新最大列宽
            columnWidths[key] = Math.max(columnWidths[key] || 0, length);
        });
    });

    // 转换为xlsx需要的格式，并限制最大宽度
    return Object.values(columnWidths).map(width => ({
        wch: Math.min(Math.max(width + 2, 8), 50) // 最小8，最大50
    }));
};

/**
 * 将数据转换为工作表格式
 * @param {Object} data 原始数据
 * @returns {Array} 转换后的数据数组
 */
const convertDataToWorksheet = (data) => {
    if (!data) return [];

    // 如果是图表数据
    if (data.title && data.data) {
        return convertChartData(data);
    }

    // 如果是多个图表的数据
    if (typeof data === 'object' && !Array.isArray(data)) {
        return convertDashboardData(data);
    }

    return [];
};

/**
 * 转换单个图表数据
 * @param {Object} chartData 图表数据
 * @returns {Array} 转换后的数据数组
 */
const convertChartData = (chartData) => {
    const {title, data, filters, period, exportTime} = chartData;

    // 添加标题行
    const result = [{
        '数据类型': title,
        '导出时间': formatDateTime(exportTime),
        '时间周期': period,
        '筛选条件': JSON.stringify(filters, null, 2)
    }];

    // 添加空行
    result.push({});

    // 如果有数据，添加数据行
    if (Array.isArray(data) && data.length > 0) {
        // 添加表头
        const headers = Object.keys(data[0]);
        const headerRow = {};
        headers.forEach(header => {
            headerRow[header] = header;
        });
        result.push(headerRow);

        // 添加数据行
        data.forEach(item => {
            const row = {};
            headers.forEach(header => {
                row[header] = item[header];
            });
            result.push(row);
        });
    }

    return result;
};

/**
 * 转换仪表盘所有数据
 * @param {Object} dashboardData 仪表盘数据
 * @returns {Array} 转换后的数据数组
 */
const convertDashboardData = (dashboardData) => {
    const result = [];

    // 添加导出信息
    result.push({
        '导出时间': formatDateTime(new Date()),
        '导出内容': '仪表盘数据汇总'
    });
    result.push({});

    // 遍历所有图表数据
    Object.entries(dashboardData).forEach(([chartName, chartData]) => {
        // 添加分隔行
        if (result.length > 0) {
            result.push({});
            result.push({});
        }

        // 添加图表数据
        result.push(...convertChartData(chartData));
    });

    return result;
};

/**
 * 格式化日期时间
 * @param {Date|string} date 日期对象或日期字符串
 * @returns {string} 格式化后的日期字符串
 */
const formatDateTime = (date) => {
    const d = new Date(date);
    return d.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}; 