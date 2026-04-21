import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const userApi = {
  create: (username, balance = 1000) => 
    api.post('/user/create', { username, balance }),
  
  get: (userId) => 
    api.get(`/user/${userId}`),
  
  login: (username) => 
    api.get(`/user/login/${username}`),
}

export const raceApi = {
  create: () => 
    api.post('/race/create'),
  
  get: (raceId) => 
    api.get(`/race/${raceId}`),
  
  start: (raceId) => 
    api.post(`/race/${raceId}/start`),
  
  join: (raceId, userId) => 
    api.post(`/race/${raceId}/join`, { userId }),
  
  leave: (raceId, userId) => 
    api.post(`/race/${raceId}/leave`, { userId }),
  
  ready: (raceId, userId) => 
    api.post(`/race/${raceId}/ready`, { userId }),
  
  unready: (raceId, userId) => 
    api.post(`/race/${raceId}/unready`, { userId }),
  
  getParticipants: (raceId) => 
    api.get(`/race/${raceId}/participants`),
}

export const betApi = {
  place: (userId, raceId, carId, amount) => 
    api.post('/bet/place', { userId, raceId, carId, amount }),
}

export const shopApi = {
  getAll: (userId) => 
    api.get(`/shop?userId=${userId}`),
  
  getByType: (type, userId) => 
    api.get(`/shop/type/${type}?userId=${userId}`),
  
  purchase: (userId, itemId) => 
    api.post(`/shop/purchase?userId=${userId}&itemId=${itemId}`),
  
  equip: (userId, itemId) => 
    api.post(`/shop/equip?userId=${userId}&itemId=${itemId}`),
}

export const reactionApi = {
  send: (userId, raceId, emoji) => 
    api.post(`/reactions/send?userId=${userId}&raceId=${raceId}&emoji=${encodeURIComponent(emoji)}`),
}

export default api
