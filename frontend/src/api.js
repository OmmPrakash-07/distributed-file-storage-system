import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
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