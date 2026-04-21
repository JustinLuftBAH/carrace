import React, { useState, useEffect, useRef } from 'react'
import { shopApi } from '../services/api'
import './Shop.css'

function Shop({ user, onClose, onUpdateUser }) {
  const [items, setItems] = useState([])
  const [selectedType, setSelectedType] = useState('ALL')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const shopContentRef = useRef(null)

  useEffect(() => {
    fetchItems()
  }, [selectedType, user.userId])

  const fetchItems = async () => {
    try {
      setLoading(true)
      const response = selectedType === 'ALL'
        ? await shopApi.getAll(user.userId)
        : await shopApi.getByType(selectedType, user.userId)
      setItems(response.data)
    } catch (err) {
      setError('Failed to load shop items')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handlePurchase = async (itemId) => {
    try {
      await shopApi.purchase(user.userId, itemId)
      setSuccessMessage('Purchase successful!')
      setTimeout(() => setSuccessMessage(''), 3000)
      fetchItems()
      onUpdateUser() // Refresh user balance
    } catch (err) {
      setError(err.response?.data?.error || 'Purchase failed')
      setTimeout(() => setError(''), 3000)
    }
  }

  const handleEquip = async (itemId) => {
    // Save scroll position
    const scrollPosition = shopContentRef.current?.scrollTop || 0
    
    try {
      await shopApi.equip(user.userId, itemId)
      setSuccessMessage('Item equipped!')
      setTimeout(() => setSuccessMessage(''), 3000)
      await onUpdateUser() // Refresh user data with new customizations
      await fetchItems() // Refresh shop to show updated ownership
      
      // Restore scroll position after re-render
      setTimeout(() => {
        if (shopContentRef.current) {
          shopContentRef.current.scrollTop = scrollPosition
        }
      }, 0)
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to equip item')
      setTimeout(() => setError(''), 3000)
    }
  }
  
  // Helper function to check if an item is equipped
  const isEquipped = (item) => {
    if (item.type === 'PROFILE_PICTURE') {
      return user.profilePicture === item.itemValue
    } else if (item.type === 'NAME_COLOR') {
      return user.nameColor === item.itemValue
    } else if (item.type === 'NAMETAG') {
      return user.customNametag === item.itemValue
    }
    return false
  }

  const groupedItems = items.reduce((acc, item) => {
    if (!acc[item.type]) acc[item.type] = []
    acc[item.type].push(item)
    return acc
  }, {})

  return (
    <div className="shop-overlay">
      <div className="shop-modal">
        <div className="shop-header">
          <h2>🛒 Cosmetic Shop</h2>
          <button className="shop-close" onClick={onClose}>✕</button>
        </div>

        <div className="shop-balance">
          Your Balance: <span className="balance-amount">${user.balance?.toFixed(2)}</span>
        </div>

        {error && <div className="shop-error">{error}</div>}
        {successMessage && <div className="shop-success">{successMessage}</div>}

        <div className="shop-filters">
          <button 
            className={selectedType === 'ALL' ? 'active' : ''}
            onClick={() => setSelectedType('ALL')}
          >
            All Items
          </button>
          <button 
            className={selectedType === 'PROFILE_PICTURE' ? 'active' : ''}
            onClick={() => setSelectedType('PROFILE_PICTURE')}
          >
            Profile Pictures
          </button>
          <button 
            className={selectedType === 'NAME_COLOR' ? 'active' : ''}
            onClick={() => setSelectedType('NAME_COLOR')}
          >
            Name Colors
          </button>
          <button 
            className={selectedType === 'NAMETAG' ? 'active' : ''}
            onClick={() => setSelectedType('NAMETAG')}
          >
            Nametags
          </button>
        </div>

        <div className="shop-content" ref={shopContentRef}>
          {loading ? (
            <div className="shop-loading">Loading items...</div>
          ) : (
            Object.entries(groupedItems).map(([type, typeItems]) => (
              <div key={type} className="shop-category">
                <h3>{type.replace(/_/g, ' ')}</h3>
                <div className="shop-items">
                  {typeItems.map(item => {
                    const equipped = isEquipped(item)
                    return (
                      <div key={item.id} className={`shop-item ${item.owned ? 'owned' : ''} ${equipped ? 'equipped' : ''}`}>
                        <div className="item-preview">
                          {type === 'PROFILE_PICTURE' && <span className="item-emoji">{item.itemValue}</span>}
                          {type === 'NAME_COLOR' && (
                            <div 
                              className="item-color-preview" 
                              style={{ background: item.itemValue }}
                            ></div>
                          )}
                          {type === 'NAMETAG' && <span className="item-tag">{item.itemValue}</span>}
                        </div>
                        <div className="item-info">
                          <h4>{item.name}</h4>
                          <p>{item.description}</p>
                          <div className="item-price">${item.price.toFixed(2)}</div>
                        </div>
                        <div className="item-actions">
                          {equipped ? (
                            <button className="btn-equipped" disabled>
                              ✓ Equipped
                            </button>
                          ) : item.owned ? (
                            <button 
                              className="btn-equip"
                              onClick={() => handleEquip(item.id)}
                            >
                              Equip
                            </button>
                          ) : (
                            <button 
                              className="btn-buy"
                              onClick={() => handlePurchase(item.id)}
                              disabled={user.balance < item.price}
                            >
                              Buy
                            </button>
                          )}
                        </div>
                      </div>
                    )
                  })}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}

export default Shop
