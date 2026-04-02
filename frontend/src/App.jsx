import { useState } from 'react'
import { Layout } from 'antd'
import FileDragger from './components/FileDragger'
import FileTable from './components/FileTable'
import Page from './components/Page'
import { PAGE } from './constants'
import './App.css'

const { Content } = Layout

function App () {
  const [uploadCount, setUploadCount] = useState(0)

  const handleUploadSuccess = () => setUploadCount((count) => count + 1)

  return (
    <Layout className="layout">
      <Content className="layout__content">
        <Page>
          <Page.Header
            title={PAGE.UPLOAD_TITLE}
            subtitle={PAGE.UPLOAD_SUBTITLE}
          />
          <FileDragger onUploadSuccess={handleUploadSuccess} />
          <Page.Section>
            <Page.Header title={PAGE.FILES_TITLE} subtitle={PAGE.FILES_SUBTITLE} />
            <FileTable refreshTrigger={uploadCount} />
          </Page.Section>
        </Page>
      </Content>
    </Layout>
  )
}

export default App
