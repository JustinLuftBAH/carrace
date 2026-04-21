import React from 'react'
import './RaceTrack.css'

function RaceTrack({ cars, leaderboard, raceStatus, winnerCarId }) {
  const FINISH_LINE = 1000
  
  // Keep cars in original order, but merge position data from leaderboard
  const raceCars = cars.map(car => {
    const leaderboardData = leaderboard.find(l => l.carId === car.id)
    return {
      ...car,
      position: leaderboardData?.position || 0,
      speed: leaderboardData?.speed || 0
    }
  })

  const getProgress = (position) => {
    return Math.min((position / FINISH_LINE) * 100, 100)
  }

  const isWinner = (carId) => {
    return winnerCarId && carId === winnerCarId
  }

  return (
    <div className="race-track-container">
      <div className="race-track-header">
        <h2>🏁 RACE TRACK</h2>
        <div className="race-status-badge" data-status={raceStatus}>
          {raceStatus || 'CREATED'}
        </div>
      </div>

      <div className="race-track">
        {raceCars.length === 0 ? (
          <div className="no-cars">
            <p>Waiting for race to initialize...</p>
          </div>
        ) : (
          <>
            {raceCars.map((car, index) => {
              const position = car.position || 0
              const speed = car.speed || 0
              const progress = getProgress(position)
              const winner = isWinner(car.id)

              return (
                <div 
                  key={car.id} 
                  className={`race-lane ${winner ? 'winner' : ''}`}
                >
                  <div className="lane-header">
                    <div className="car-info">
                      <div 
                        className="car-marker" 
                        style={{ background: car.color }}
                      />
                      <span className="car-name">{car.name}</span>
                      {car.winProbability && (
                        <span className="win-probability">{car.winProbability}x</span>
                      )}
                      {winner && <span className="winner-badge">🏆</span>}
                    </div>
                    <div className="car-stats">
                      <span className="position">{position}m</span>
                      {raceStatus === 'RUNNING' && (
                        <span className="speed">{speed.toFixed(1)} m/s</span>
                      )}
                    </div>
                  </div>

                  <div className="track-line">
                    <div className="track-background">
                      <div className="finish-line" />
                    </div>
                    <div 
                      className="car-progress"
                      style={{ 
                        width: `${progress}%`,
                        background: car.color 
                      }}
                    >
                      <div className="car-icon">🏎️</div>
                    </div>
                  </div>
                </div>
              )
            })}
          </>
        )}
      </div>

      <div className="track-footer">
        <div className="finish-marker">
          <span>START</span>
          <span>FINISH ({FINISH_LINE}m)</span>
        </div>
      </div>
    </div>
  )
}

export default RaceTrack
