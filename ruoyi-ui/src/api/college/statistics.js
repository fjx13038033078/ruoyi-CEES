import request from '@/utils/request'

export function getUniversityOptions() {
  return request({
    url: '/college/statistics/universityOptions',
    method: 'get'
  })
}

export function getMajorTrend(universityId) {
  return request({
    url: '/college/statistics/majorTrend',
    method: 'get',
    params: { universityId }
  })
}

export function getAllProvinces() {
  return request({
    url: '/college/statistics/provinces',
    method: 'get'
  })
}

export function getUniversityDist() {
  return request({
    url: '/college/statistics/universityDist',
    method: 'get'
  })
}
