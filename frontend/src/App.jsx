import React, { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/Login'
import Register from './components/Register'
import RaceLobby from './components/RaceLobby'
import './App.css'

function App() {
  const [user, setUser] = useState(null)

  useEffect(() => {
    // Check if user is logged in
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      setUser(JSON.parse(storedUser))
    }
  }, [])

  const handleLogin = (userData) => {
    setUser(userData)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('user')
  }

  return (
    <Router>
      <div className="app">
        <Routes>
          <Route 
            path="/login" 
            element={user ? <Navigate to="/lobby" /> : <Login onLogin={handleLogin} />} 
          />
          <Route 
            path="/register" 
            element={user ? <Navigate to="/lobby" /> : <Register onRegister={handleLogin} />} 
          />
          <Route 
            path="/lobby" 
            element={user ? <RaceLobby user={user} onLogout={handleLogout} onUpdateUser={setUser} /> : <Navigate to="/login" />} 
          />
          <Route path="/" element={<Navigate to={user ? "/lobby" : "/login"} />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
