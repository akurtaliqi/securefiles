export const PAGE = {
  UPLOAD_TITLE: 'Upload a file',
  UPLOAD_SUBTITLE: 'Files are scanned automatically before being available.',
  FILES_TITLE: 'All files available',
  FILES_SUBTITLE: 'Files successfully stored and scanned.'
}

export const DRAGGER = {
  TEXT: 'Click or drag a file to this area to upload',
  HINT: 'Any file type supported. Files are scanned for viruses before becoming available for download.',
  UPLOAD_SUCCESS: (name) => `${name} uploaded successfully`,
  UPLOAD_ERROR: (name) => `${name} upload failed`
}

export const TABLE = {
  COL_FILE_NAME: 'File Name',
  COL_SIZE: 'Size',
  COL_ACTIONS: 'Actions',
  SIZE_MB: (value) => `${value} MB`,
  SIZE_KB: (value) => `${value} KB`
}

export const API = {
  FILES: '/api/files',
  FILE_DOWNLOAD: (id) => `/api/files/${id}/download`,
  UPLOAD_FIELD: 'file',
  CONTENT_DISPOSITION_HEADER: 'Content-Disposition',
  DEFAULT_FILENAME: 'download'
}

export const ERRORS = {
  SERVER_UNREACHABLE: 'Cannot reach the server. Make sure the backend is running.',
  UPLOAD_FAILED: (status) => `Upload failed (${status})`,
  FETCH_FAILED: (status) => `Failed to fetch files (${status})`,
  DOWNLOAD_FAILED: (status) => `Download failed (${status})`
}
