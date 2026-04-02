import { describe, test, expect, vi, beforeEach } from 'vitest';
import { uploadFile, getFiles, downloadFile } from './fileService';

describe('uploadFile', () => {
  beforeEach(() => {
    global.fetch = vi.fn();
  });

  test('sends a POST request to /api/files', async () => {
    global.fetch.mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ id: 1 })
    });
    const file = new File(['content'], 'test.pdf');
    await uploadFile(file);
    expect(global.fetch).toHaveBeenCalledWith(
      '/api/files',
      expect.objectContaining({ method: 'POST' })
    );
  });

  test('throws a user-friendly error on network failure', async () => {
    global.fetch.mockRejectedValue(new Error('network'));
    await expect(uploadFile(new File([''], 'test.pdf'))).rejects.toThrow(
      'Cannot reach the server'
    );
  });

  test('throws the server error message on HTTP error', async () => {
    global.fetch.mockResolvedValue({
      ok: false,
      status: 400,
      headers: { get: () => null },
      text: () => Promise.resolve('File is empty')
    });
    await expect(uploadFile(new File([''], 'test.pdf'))).rejects.toThrow('File is empty');
  });
});

describe('getFiles', () => {
  beforeEach(() => {
    global.fetch = vi.fn();
  });

  test('sends a GET request to /api/files', async () => {
    global.fetch.mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([])
    });
    await getFiles();
    expect(global.fetch).toHaveBeenCalledWith('/api/files');
  });

  test('throws a user-friendly error on network failure', async () => {
    global.fetch.mockRejectedValue(new Error('network'));
    await expect(getFiles()).rejects.toThrow('Cannot reach the server');
  });

  test('throws the server error message on HTTP error', async () => {
    global.fetch.mockResolvedValue({
      ok: false,
      status: 500,
      headers: { get: () => null },
      text: () => Promise.resolve('Internal Server Error')
    });
    await expect(getFiles()).rejects.toThrow('Internal Server Error');
  });
});

describe('downloadFile', () => {
  beforeEach(() => {
    global.fetch = vi.fn();
    global.URL.createObjectURL = vi.fn(() => 'blob:mock-url');
    global.URL.revokeObjectURL = vi.fn();
  });

  test('sends a GET request to /api/files/:id/download', async () => {
    global.fetch.mockResolvedValue({
      ok: true,
      blob: () => Promise.resolve(new Blob()),
      headers: { get: () => 'attachment; filename="report.pdf"' }
    });
    await downloadFile(42);
    expect(global.fetch).toHaveBeenCalledWith('/api/files/42/download');
  });

  test('throws a user-friendly error on network failure', async () => {
    global.fetch.mockRejectedValue(new Error('network'));
    await expect(downloadFile(1)).rejects.toThrow('Cannot reach the server');
  });

  test('throws the server error message on HTTP error', async () => {
    global.fetch.mockResolvedValue({
      ok: false,
      status: 404,
      headers: { get: () => null },
      text: () => Promise.resolve('File not found')
    });
    await expect(downloadFile(99)).rejects.toThrow('File not found');
  });
});
