import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { userApi } from '../services/api'
import './Auth.css'

function Login({ onLogin }) {
  const [username, setUsername] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      // Try to login (find existing user)
      const response = await userApi.login(username)
      onLogin(response.data)
    } catch (err) {
      setError('User not found. Please register first.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-background">
        <div className="racing-lines"></div>
      </div>
      
      <div className="auth-card fade-in">
        <div className="auth-header">
          <h1 className="auth-title">🏁 CAR RACE</h1>
          <p className="auth-subtitle">Real-time Multiplayer Racing & Betting</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter your username"
              required
              autoFocus
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Don't have an account? <Link to="/register">Register here</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login
