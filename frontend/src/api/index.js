import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export const batchApi = {
  getAll: () => request.get('/batches'),
  create: (data) => request.post('/batches', data),
  updateMaturity: (id, level) => request.put(`/batches/${id}/maturity?level=${level}`)
}

export const orderApi = {
  getAll: () => request.get('/orders'),
  create: (data) => request.post('/orders', data),
  cancel: (id) => request.put(`/orders/${id}/cancel`),
  confirmPartialShip: (id, remark) => request.put(`/orders/${id}/partial-ship?remark=${remark || ''}`),
  confirmDelay: (id, remark) => request.put(`/orders/${id}/delay?remark=${remark || ''}`)
}

export const taskApi = {
  getAll: () => request.get('/tasks'),
  generate: () => request.post('/tasks/generate'),
  assign: (id, assignee) => request.put(`/tasks/${id}/assign?assignee=${assignee}`),
  complete: (id, quantity) => request.put(`/tasks/${id}/complete?actualQuantity=${quantity}`)
}

export const deliveryApi = {
  getVehicles: () => request.get('/delivery/vehicles'),
  getAll: () => request.get('/delivery'),
  create: (vehicleId) => request.post(`/delivery/create?vehicleId=${vehicleId}`),
  complete: (id) => request.put(`/delivery/${id}/complete`)
}