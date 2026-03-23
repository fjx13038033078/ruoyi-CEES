import request from '@/utils/request'

export function getFormList() {
  return request({
    url: '/college/volunteer/formList',
    method: 'get'
  })
}

export function getFormDetail(formId) {
  return request({
    url: '/college/volunteer/form/' + formId,
    method: 'get'
  })
}

export function addForm(data) {
  return request({
    url: '/college/volunteer/form/add',
    method: 'post',
    data: data
  })
}

export function updateForm(data) {
  return request({
    url: '/college/volunteer/form/update',
    method: 'put',
    data: data
  })
}

export function deleteForm(formId) {
  return request({
    url: '/college/volunteer/form/delete/' + formId,
    method: 'delete'
  })
}

export function getItems(formId) {
  return request({
    url: '/college/volunteer/items/' + formId,
    method: 'get'
  })
}

export function addItem(data) {
  return request({
    url: '/college/volunteer/item/add',
    method: 'post',
    data: data
  })
}

export function updateItem(data) {
  return request({
    url: '/college/volunteer/item/update',
    method: 'put',
    data: data
  })
}

export function deleteItem(itemId) {
  return request({
    url: '/college/volunteer/item/delete/' + itemId,
    method: 'delete'
  })
}

export function updateItemSort(data) {
  return request({
    url: '/college/volunteer/item/sort',
    method: 'put',
    data: data
  })
}

export function checkForm(formId) {
  return request({
    url: '/college/volunteer/check/' + formId,
    method: 'get'
  })
}
