import { useEffect, useState } from 'react'
import { Table, Button, message } from 'antd'
import { DownloadOutlined } from '@ant-design/icons'
import { downloadFile, getFiles } from '../services/fileService'
import { TABLE } from '../constants'

const POLLING_INTERVAL_MS = 5000

const formatFileSize = (bytes) => {
  if (bytes >= 1024 * 1024) return TABLE.SIZE_MB((bytes / (1024 * 1024)).toFixed(2))
  return TABLE.SIZE_KB((bytes / 1024).toFixed(2))
}

const STATIC_COLUMNS = [
  {
    title: TABLE.COL_FILE_NAME,
    dataIndex: 'originalFilename',
    key: 'originalFilename',
  },
  {
    title: TABLE.COL_SIZE,
    dataIndex: 'fileSize',
    key: 'fileSize',
    render: (size) => formatFileSize(size),
  },
]

function FileTable({ refreshTrigger }) {
  const [files, setFiles] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    getFiles()
      .then(setFiles)
      .catch((err) => message.error(err.message))
      .finally(() => setLoading(false))

    const intervalId = setInterval(() => {
      getFiles()
        .then(setFiles)
        .catch(() => {})
    }, POLLING_INTERVAL_MS)

    return () => clearInterval(intervalId)
  }, [refreshTrigger])

  const columns = [
    ...STATIC_COLUMNS,
    {
      title: TABLE.COL_ACTIONS,
      key: 'actions',
      render: (_, record) => (
        <Button
          type="primary"
          icon={<DownloadOutlined />}
          onClick={async () => {
            try {
              await downloadFile(record.id)
            } catch (err) {
              message.error(err.message)
            }
          }}
        />
      ),
    },
  ]

  return (
    <Table
      columns={columns}
      dataSource={files}
      rowKey="id"
      loading={loading}
      pagination={{
        placement: 'bottomCenter',
        showSizeChanger: true,
        pageSizeOptions: [5, 10, 25, 50],
      }}
    />
  )
}

export default FileTable
