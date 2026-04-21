import React from 'react'
import './Header.css'

function Header({ user, onLogout, onRefresh, onOpenShop }) {
  return (
    <header className="app-header">
      <div className="header-left">
        <h1 className="header-title">🏁 CAR RACE</h1>
      </div>
      
      <div className="header-right">
        <button className="btn-shop" onClick={onOpenShop} title="Open Shop">
          🛒 Shop
        </button>
        
        <div className="user-balance" onClick={onRefresh} title="Click to refresh">
          <span className="balance-label">Credits</span>
          <span className="balance-amount">${user.balance?.toFixed(2) || '0.00'}</span>
        </div>
        
        <div className="user-info">
          <span className="user-avatar">{user.profilePicture || '🏎️'}</span>
          <span 
            className="username" 
            style={{ color: user.nameColor || '#FFFFFF' }}
          >
            {user.username}
          </span>
          {user.customNametag && (
            <span className="user-nametag">{user.customNametag}</span>
          )}
        </div>
        
        <button className="btn-logout" onClick={onLogout}>
          Logout
        </button>
      </div>
    </header>
  )
}

export default Header
