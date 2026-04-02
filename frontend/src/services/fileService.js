export async function uploadFile (file) {
  const formData = new FormData()
  formData.append('file', file)

  let response
  try {
    response = await fetch('/api/files', { method: 'POST', body: formData })
  } catch {
    throw new Error('Cannot reach the server. Make sure the backend is running.')
  }

  if (!response.ok) {
    const errorMessage = await response.text()
    throw new Error(errorMessage || `Upload failed (${response.status})`)
  }

  return response.json()
}

export async function getFiles () {
  let response
  try {
    response = await fetch('/api/files')
  } catch {
    throw new Error('Cannot reach the server. Make sure the backend is running.')
  }

  if (!response.ok) {
    const errorMessage = await response.text()
    throw new Error(errorMessage || `Failed to fetch files (${response.status})`)
  }

  return response.json()
}

export async function downloadFile (id) {
  let response
  try {
    response = await fetch(`/api/files/${id}/download`)
  } catch {
    throw new Error('Cannot reach the server. Make sure the backend is running.')
  }

  if (!response.ok) {
    const errorMessage = await response.text()
    throw new Error(errorMessage || `Download failed (${response.status})`)
  }

  const blob = await response.blob()
  const contentDisposition = response.headers.get('Content-Disposition')
  const filename = contentDisposition?.match(/filename="(.+)"/)?.[1] ?? 'download'

  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = filename
  anchor.click()
  URL.revokeObjectURL(url)
}
