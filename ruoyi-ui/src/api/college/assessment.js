import request from '@/utils/request'

export function getQuestions() {
  return request({
    url: '/college/assessment/questions',
    method: 'get'
  })
}

export function startTest() {
  return request({
    url: '/college/assessment/start',
    method: 'post'
  })
}

export function getLatestRecord() {
  return request({
    url: '/college/assessment/latestRecord',
    method: 'get'
  })
}

export function submitAnswers(data) {
  return request({
    url: '/college/assessment/submit',
    method: 'post',
    data: data
  })
}

export function getResult(recordId) {
  return request({
    url: '/college/assessment/result/' + recordId,
    method: 'get'
  })
}

export function getLatestResult() {
  return request({
    url: '/college/assessment/latestResult',
    method: 'get'
  })
}

export function getResultHistory() {
  return request({
    url: '/college/assessment/resultHistory',
    method: 'get'
  })
}
