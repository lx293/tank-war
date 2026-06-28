/**
 * 小说网站公共JS
 */

// ============================================
// 获取URL参数
// ============================================
function getUrlParam(name) {
    const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
    const r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURIComponent(r[2]);
    return null;
}

// ============================================
// 时间格式化
// ============================================
function formatTime(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// ============================================
// 数字格式化（万为单位）
// ============================================
function formatNumber(num) {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
    }
    return num.toString();
}

// ============================================
// 显示提示消息
// ============================================
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        padding: 12px 24px;
        border-radius: 25px;
        color: white;
        font-size: 14px;
        z-index: 9999;
        animation: slideDown 0.3s ease;
        background: ${type === 'success' ? '#4CAF50' : type === 'error' ? '#f44336' : '#2196F3'};
    `;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideUp 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 2000);
}

// ============================================
// 防抖函数
// ============================================
function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}

// ============================================
// 节流函数
// ============================================
function throttle(func, wait) {
    let previous = 0;
    return function() {
        const now = Date.now();
        if (now - previous > wait) {
            func.apply(this, arguments);
            previous = now;
        }
    };
}

// ============================================
// 随机颜色（用于封面）
// ============================================
const coverColors = [
    '#667eea', '#764ba2', '#f093fb', '#f5576c',
    '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
    '#fa709a', '#fee140', '#30cfd0', '#330867',
    '#a8edea', '#fed6e3', '#5ee7df', '#b490ca'
];

function getRandomColor() {
    return coverColors[Math.floor(Math.random() * coverColors.length)];
}

// ============================================
// 页面回到顶部
// ============================================
function scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ============================================
// 检查登录状态
// ============================================
function checkLoginStatus() {
    return fetch('/api/user/info')
        .then(response => response.json())
        .then(result => {
            return result.code === 200 ? result.data : null;
        })
        .catch(() => null);
}

// ============================================
// 添加动画样式
// ============================================
const style = document.createElement('style');
style.textContent = `
    @keyframes slideDown {
        from { transform: translate(-50%, -100%); opacity: 0; }
        to { transform: translate(-50%, 0); opacity: 1; }
    }
    @keyframes slideUp {
        from { transform: translate(-50%, 0); opacity: 1; }
        to { transform: translate(-50%, -100%); opacity: 0; }
    }
`;
document.head.appendChild(style);