import FileDragger from './components/FileDragger'
import './App.css'

function App() {
  return (
    <div className="page">
      <div className="page__header">
        <h1 className="page__title">Upload a file</h1>
        <p className="page__subtitle">Files are scanned automatically before being available.</p>
      </div>
      <FileDragger />
    </div>
  )
}

export default App
