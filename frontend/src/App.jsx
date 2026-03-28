import { useState } from 'react'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>
        <h1>SecureFiles</h1>
        <p>Welcome to the Secure File Management Application</p>
      </div>
    </>
  )
}

export default App
