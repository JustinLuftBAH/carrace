import React from 'react'
import './ReadyTimer.css'

function ReadyTimer({ countdown }) {
  return (
    <div className="ready-timer-overlay">
      <div className="ready-timer">
        <h2>All Players Ready!</h2>
        <div className="countdown">{countdown}</div>
        <p>Race starting soon...</p>
      </div>
    </div>
  )
}

export default ReadyTimer
