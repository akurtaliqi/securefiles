import { render, screen } from '@testing-library/react'
import { vi } from 'vitest'
import FileDragger from './FileDragger'

vi.mock('../services/fileService', () => ({
  uploadFile: vi.fn()
}))

test('renders the drag-and-drop instruction text', () => {
  render(<FileDragger onUploadSuccess={() => {}} />)
  expect(screen.getByText('Click or drag a file to this area to upload')).toBeInTheDocument()
})

test('renders the antivirus scan hint', () => {
  render(<FileDragger onUploadSuccess={() => {}} />)
  expect(screen.getByText(/Files are scanned for viruses/)).toBeInTheDocument()
})

