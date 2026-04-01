import request from '@/utils/request'

export function recommendMajors() {
  return request({
    url: '/university/recommendation/listAll',
    method: 'get',
  })
}

export function getTieredRecommendations(limit) {
  return request({
    url: '/university/recommendation/tiered',
    method: 'get',
    params: { limit }
  })
}
