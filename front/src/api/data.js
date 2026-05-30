import request from '@/utils/request'  // 这里假设你已经封装了 axios 实例

// 获取某酒店每月总收入
export function getMonthlyIncome(hotelId) {
    return request({
        url: '/data/monthlyIncome',
        method: 'get',
        params: { hotelId }
    })
}
