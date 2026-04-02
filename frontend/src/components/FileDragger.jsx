import { useState } from 'react';
import { Upload, message } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import { uploadFile } from '../services/fileService';
import { DRAGGER } from '../utils/constants';

const { Dragger } = Upload;
const REMOVE_DELAY_MS = 5000;

function FileDragger ({ onUploadSuccess }) {
  const [fileList, setFileList] = useState([]);

  const removeFileAfterDelay = (fileUid) => {
    setTimeout(() => {
      setFileList((current) => current.filter((f) => f.uid !== fileUid));
    }, REMOVE_DELAY_MS);
  };

  const handleUpload = async ({ file, onSuccess, onError }) => {
    try {
      const result = await uploadFile(file);
      onSuccess(result);
    } catch (error) {
      onError(error);
    }
  };

  const handleChange = (info) => {
    setFileList(info.fileList);
    const { status } = info.file;
    if (status === 'done') {
      message.success(`${info.file.name} uploaded successfully`);
      onUploadSuccess?.();
      removeFileAfterDelay(info.file.uid);
    } else if (status === 'error') {
      message.error(info.file.error?.message ?? `${info.file.name} upload failed`);
      removeFileAfterDelay(info.file.uid);
    }
  };

  return (
    <Dragger
      name="file"
      multiple
      fileList={fileList}
      customRequest={handleUpload}
      onChange={handleChange}
    >
      <p className="ant-upload-drag-icon">
        <InboxOutlined />
      </p>
      <p className="ant-upload-text">{DRAGGER.TEXT}</p>
      <p className="ant-upload-hint">{DRAGGER.HINT}</p>
    </Dragger>
  );
}

export default FileDragger;
