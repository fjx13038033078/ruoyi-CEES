import request from '@/utils/request'

export function getMyProfile() {
  return request({
    url: '/college/student/myProfile',
    method: 'get'
  })
}

export function getProfileDetail(profileId) {
  return request({
    url: '/college/student/detail/' + profileId,
    method: 'get'
  })
}

export function saveProfile(data) {
  return request({
    url: '/college/student/save',
    method: 'post',
    data: data
  })
}

export function listProfiles(query) {
  return request({
    url: '/college/student/list',
    method: 'get',
    params: query
  })
}

export function verifyProfile(profileId, verifyStatus) {
  return request({
    url: '/college/student/verify/' + profileId + '/' + verifyStatus,
    method: 'put'
  })
}

export function deleteProfile(profileId) {
  return request({
    url: '/college/student/delete/' + profileId,
    method: 'delete'
  })
}
