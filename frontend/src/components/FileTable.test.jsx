import { render, screen, waitFor, act } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import FileTable from './FileTable'
import { getFiles, downloadFile } from '../services/fileService'

vi.mock('../services/fileService', () => ({
  getFiles: vi.fn(),
  downloadFile: vi.fn()
}))

const TEST_FILES = [
  { id: 1, originalFilename: 'report.pdf', fileSize: 2_480_320 },
  { id: 2, originalFilename: 'photo.png', fileSize: 512_000 }
]

beforeEach(() => {
  vi.clearAllMocks()
})

afterEach(() => {
  vi.restoreAllMocks()
})

test('renders file names returned by the API', async () => {
  getFiles.mockResolvedValue(TEST_FILES)
  render(<FileTable refreshTrigger={0} />)
  await waitFor(() => {
    expect(screen.getByText('report.pdf')).toBeInTheDocument()
    expect(screen.getByText('photo.png')).toBeInTheDocument()
  })
})

test('renders formatted file sizes', async () => {
  getFiles.mockResolvedValue(TEST_FILES)
  render(<FileTable refreshTrigger={0} />)
  await waitFor(() => {
    expect(screen.getByText('2.37 MB')).toBeInTheDocument()
    expect(screen.getByText('500.00 KB')).toBeInTheDocument()
  })
})

test('renders a download button for each file', async () => {
  getFiles.mockResolvedValue(TEST_FILES)
  render(<FileTable refreshTrigger={0} />)
  await waitFor(() => {
    expect(screen.getAllByRole('button', { name: 'download' })).toHaveLength(TEST_FILES.length)
  })
})

test('calls downloadFile with the correct id when clicking download', async () => {
  getFiles.mockResolvedValue(TEST_FILES)
  downloadFile.mockResolvedValue(undefined)
  render(<FileTable refreshTrigger={0} />)
  await waitFor(() => screen.getAllByRole('button', { name: 'download' }))
  await userEvent.click(screen.getAllByRole('button', { name: 'download' })[0])
  expect(downloadFile).toHaveBeenCalledWith(1)
})

test('refetches files when refreshTrigger changes', async () => {
  getFiles.mockResolvedValue(TEST_FILES)
  const { rerender } = render(<FileTable refreshTrigger={0} />)
  await waitFor(() => expect(getFiles).toHaveBeenCalledTimes(1))
  rerender(<FileTable refreshTrigger={1} />)
  await waitFor(() => expect(getFiles).toHaveBeenCalledTimes(2))
})

test('polls the API every 5 seconds to detect external uploads', async () => {
  const setIntervalSpy = vi.spyOn(global, 'setInterval')
  getFiles.mockResolvedValue(TEST_FILES)
  await act(async () => {
    render(<FileTable refreshTrigger={0} />)
  })
  expect(setIntervalSpy).toHaveBeenCalledWith(expect.any(Function), 5000)
})

test('clears the polling interval on unmount', async () => {
  const clearIntervalSpy = vi.spyOn(global, 'clearInterval')
  getFiles.mockResolvedValue(TEST_FILES)
  let unmount
  await act(async () => {
    const result = render(<FileTable refreshTrigger={0} />)
    unmount = result.unmount
  })
  unmount()
  expect(clearIntervalSpy).toHaveBeenCalled()
})
