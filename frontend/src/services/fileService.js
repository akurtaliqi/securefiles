export async function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  let response
  try {
    response = await fetch('/api/files', { method: 'POST', body: formData })
  } catch {
    throw new Error('Cannot reach the server. Make sure the backend is running.')
  }

  if (!response.ok) {
    throw new Error(`Upload rejected by server (${response.status})`)
  }

  return response.json()
}

