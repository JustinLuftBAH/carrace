import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { userApi } from '../services/api'
import './Auth.css'

function Register({ onRegister }) {
  const [username, setUsername] = useState('')
  const [initialBalance, setInitialBalance] = useState(1000)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await userApi.create(username, initialBalance)
      onRegister(response.data)
    } catch (err) {
      if (err.response?.status === 409) {
        setError('Username already exists')
      } else {
        setError('Registration failed. Please try again.')
      }
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
          <p className="auth-subtitle">Create Your Racing Account</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Choose a username"
              required
              autoFocus
              minLength={3}
            />
          </div>

          <div className="form-group">
            <label htmlFor="balance">Starting Balance</label>
            <input
              id="balance"
              type="number"
              value={initialBalance}
              onChange={(e) => setInitialBalance(Number(e.target.value))}
              placeholder="1000"
              min={100}
              max={10000}
              step={100}
            />
            <small>Starting credits: ${initialBalance}</small>
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account? <Link to="/login">Login here</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Register
