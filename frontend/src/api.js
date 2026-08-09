import axios from 'axios'

const API_BASE_URL = import.meta.env.PROD
  ? 'https://distributed-file-storage-system-production.up.railway.app/api'
  : '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
})

export const getFiles = async () => {
  const response = await api.get('/files')
  return response.data
}

export const uploadFile = async (file) => {
  const formData = new FormData()
  formData.append('file', file)

  const response = await api.post('/files/upload', formData)

  return response.data
}

export const deleteFile = async (fileId) => {
  const response = await api.delete(`/files/${fileId}`)
  return response.data
}

export const downloadFile = async (fileId) => {
  const response = await api.get(`/files/download/${fileId}`, {
    responseType: 'blob',
  })

  return response.data
}

export const getHealth = async () => {
  const response = await api.get('/health')
  return response.data
}

export default api