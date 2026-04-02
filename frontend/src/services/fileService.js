async function extractErrorMessage (response) {
  const contentType = response.headers?.get('Content-Type') ?? ''
  const body = await response.text()

  if (contentType.includes('application/json')) {
    try {
      const json = JSON.parse(body)
      return json.message ?? json.error ?? `Request failed (${response.status})`
    } catch {
      // fall through
    }
  }

  if (response.status === 413) {
    return 'File is too large. Maximum allowed size is 100 MB.'
  }

  if (body.trim().startsWith('<')) {
    return `Request failed (${response.status})`
  }

  return body || `Request failed (${response.status})`
}

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
    throw new Error(await extractErrorMessage(response))
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
    throw new Error(await extractErrorMessage(response))
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
    throw new Error(await extractErrorMessage(response))
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


