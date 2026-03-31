import { Upload, message } from 'antd'
import { InboxOutlined } from '@ant-design/icons'
import { uploadFile } from '../services/fileService'

const { Dragger } = Upload

function FileDragger() {
  const uploadProps = {
    name: 'file',
    multiple: true,
    customRequest: async ({ file, onSuccess, onError }) => {
      try {
        const result = await uploadFile(file)
        onSuccess(result)
      } catch (error) {
        onError(error)
      }
    },
    onChange(info) {
      const { status } = info.file
      if (status === 'done') {
        message.success(`${info.file.name} uploaded successfully`)
      } else if (status === 'error') {
        message.error(info.file.error?.message ?? `${info.file.name} upload failed`)
      }
    },
  }

  return (
    <Dragger {...uploadProps}>
      <p className="ant-upload-drag-icon">
        <InboxOutlined />
      </p>
      <p className="ant-upload-text">Click or drag a file to this area to upload</p>
      <p className="ant-upload-hint">
        Any file type supported. Files are scanned for viruses before becoming available for download.
      </p>
    </Dragger>
  )
}

export default FileDragger

