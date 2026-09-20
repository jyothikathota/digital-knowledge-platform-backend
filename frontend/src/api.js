const baseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000').replace(/\/$/, '')
const authUrl = (import.meta.env.VITE_AUTH_API_URL || '').replace(/\/$/, '')
const contentUrl = (import.meta.env.VITE_CONTENT_API_URL || baseUrl).replace(/\/$/, '')

function authHeaders() {
  const token = localStorage.getItem('dkp_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...options.headers
    }
  })

  if (!response.ok) {
    let details = {}
    try {
      details = await response.json()
    } catch {
      // Keep the status message when the service does not return JSON.
    }
    const validation = details.messages && Object.values(details.messages).join(' ')
    throw new Error(validation || details.message || details.error || `Request failed (${response.status})`)
  }

  return response.status === 204 ? null : response.json()
}

export const api = {
  login: (payload) => request(`${authUrl}/api/auth/login`, { method: 'POST', body: JSON.stringify(payload) }),
  register: (payload) => request(`${authUrl}/api/auth/register`, { method: 'POST', body: JSON.stringify(payload) }),
  listContent: () => request(`${contentUrl}/api/content`),
  searchContent: (keyword) => request(`${contentUrl}/api/content/search?keyword=${encodeURIComponent(keyword)}`),
  getContent: (id) => request(`${contentUrl}/api/content/${id}`),
  createContent: (payload) => request(`${contentUrl}/api/content`, { method: 'POST', body: JSON.stringify(payload) }),
  updateContent: (id, payload) => request(`${contentUrl}/api/content/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteContent: (id) => request(`${contentUrl}/api/content/${id}`, { method: 'DELETE' })
}