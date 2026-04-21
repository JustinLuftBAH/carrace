import React, { useState } from 'react'
import './BettingPanel.css'

function BettingPanel({ cars, balance, myBet, onPlaceBet, disabled }) {
  const [selectedCar, setSelectedCar] = useState(null)
  const [betAmount, setBetAmount] = useState(100)
  const [isFreeBet, setIsFreeBet] = useState(false)
  const [placing, setPlacing] = useState(false)
  const [error, setError] = useState('')

  const handlePlaceBet = async () => {
    if (!selectedCar) {
      setError('Please select a car')
      return
    }

    if (!isFreeBet && betAmount > balance) {
      setError('Insufficient balance')
      return
    }

    if (!isFreeBet && betAmount < 10) {
      setError('Minimum bet is $10')
      return
    }

    setPlacing(true)
    setError('')

    try {
      await onPlaceBet(selectedCar, isFreeBet ? 0 : betAmount)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to place bet')
    } finally {
      setPlacing(false)
    }
  }

  if (myBet) {
    const betCar = cars.find(c => c.id === myBet.carId)
    const isFree = myBet.amount === 0
    const potentialWin = isFree ? 250 : (myBet.amount * (betCar?.winProbability || 2.0))
    return (
      <div className="betting-panel placed">
        <h3>Your Bet</h3>
        <div className="bet-info">
          <div className="bet-car">
            <div 
              className="car-color-dot" 
              style={{ background: betCar?.color }}
            />
            <span>{betCar?.name || `Car ${myBet.carId}`}</span>
            {betCar?.winProbability && (
              <span className="car-odds">{betCar.winProbability}x</span>
            )}
          </div>
          <div className="bet-amount">{isFree ? 'FREE' : `$${myBet.amount}`}</div>
        </div>
        <p className="bet-hint">
          Potential win: ${potentialWin.toFixed(2)}
        </p>
      </div>
    )
  }

  return (
    <div className="betting-panel">
      <h3>Place Your Bet</h3>
      
      <div className="car-selection">
        {cars.map(car => (
          <div
            key={car.id}
            className={`car-option ${selectedCar === car.id ? 'selected' : ''} ${disabled ? 'disabled' : ''}`}
            onClick={() => !disabled && setSelectedCar(car.id)}
          >
            <div 
              className="car-color" 
              style={{ background: car.color }}
            />
            <div className="car-details">
              <span className="car-name">{car.name}</span>
              {car.winProbability && (
                <span className="car-odds-small">{car.winProbability}x</span>
              )}
            </div>
          </div>
        ))}
      </div>

      <div className="bet-amount-section">
        <label>Bet Amount</label>
        <div className="bet-mode-toggle">
          <button 
            className={`mode-btn ${!isFreeBet ? 'active' : ''}`}
            onClick={() => setIsFreeBet(false)}
          >
            Regular Bet
          </button>
          <button 
            className={`mode-btn free ${isFreeBet ? 'active' : ''}`}
            onClick={() => setIsFreeBet(true)}
          >
            🎁 FREE BET
          </button>
        </div>
        
        {!isFreeBet && (
          <>
            <div className="amount-input-group">
              <button 
                className="amount-btn"
                onClick={() => setBetAmount(Math.max(10, betAmount - 50))}
                disabled={disabled}
              >
                -
              </button>
              <input
                type="number"
                value={betAmount}
                onChange={(e) => setBetAmount(Number(e.target.value))}
                min={10}
                max={balance}
                step={10}
                disabled={disabled}
              />
              <button 
                className="amount-btn"
                onClick={() => setBetAmount(Math.min(balance, betAmount + 50))}
                disabled={disabled}
              >
                +
              </button>
            </div>
            <div className="quick-bets">
              {[50, 100, 250, 500].map(amount => (
                <button
                  key={amount}
                  className="quick-bet-btn"
                  onClick={() => setBetAmount(amount)}
                  disabled={disabled || amount > balance}
                >
                  ${amount}
                </button>
              ))}
            </div>
          </>
        )}
        
        {isFreeBet && (
          <div className="free-bet-info">
            <p>✨ No risk, free entry!</p>
            <p>Win $250 if your car wins!</p>
          </div>
        )}
      </div>

      {error && <div className="bet-error">{error}</div>}

      <button
        className="btn-place-bet"
        onClick={handlePlaceBet}
        disabled={disabled || placing || !selectedCar}
      >
        {placing ? 'Placing Bet...' : isFreeBet ? 'Place Free Bet' : `Place Bet ($${betAmount})`}
      </button>

      <p className="bet-hint">
        {(() => {
          if (!selectedCar) return 'Select a car to see potential win'
          const car = cars.find(c => c.id === selectedCar)
          const odds = car?.winProbability || 2.0
          if (isFreeBet) return 'Potential win: $250.00'
          return `Potential win: $${(betAmount * odds).toFixed(2)} (${odds}x odds)`
        })()}
      </p>
    </div>
  )
}

export default BettingPanel
