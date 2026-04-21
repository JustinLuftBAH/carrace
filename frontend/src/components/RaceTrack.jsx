import React, { useState, useEffect } from 'react'
import './RaceTrack.css'

function RaceTrack({ cars, leaderboard, raceStatus, winnerCarId }) {
  const FINISH_LINE = 1000
  const [speedLines, setSpeedLines] = useState([])
  
  // Keep cars in original order, but merge position data from leaderboard
  const raceCars = cars.map(car => {
    const leaderboardData = leaderboard.find(l => l.carId === car.id)
    return {
      ...car,
      position: leaderboardData?.position || 0,
      speed: leaderboardData?.speed || 0
    }
  })

  // Sort by position for ranking
  const sortedCars = [...raceCars].sort((a, b) => b.position - a.position)

  const getProgress = (position) => {
    return Math.min((position / FINISH_LINE) * 100, 100)
  }

  const isWinner = (carId) => {
    return winnerCarId && carId === winnerCarId
  }

  const getRank = (carId) => {
    return sortedCars.findIndex(c => c.id === carId) + 1
  }

  // Generate speed lines effect when racing
  useEffect(() => {
    if (raceStatus === 'RUNNING') {
      const interval = setInterval(() => {
        setSpeedLines(prev => {
          const newLines = Array(3).fill(0).map(() => ({
            id: Math.random(),
            top: Math.random() * 100,
            duration: 0.5 + Math.random() * 0.5
          }))
          return [...prev, ...newLines].slice(-20)
        })
      }, 100)
      return () => clearInterval(interval)
    }
  }, [raceStatus])

  // SVG Car Component - Professional Racing Car Design
  const SVGCar = ({ color, speed }) => {
    const isMoving = speed > 0
    return (
      <svg 
        className={`svg-car ${isMoving ? 'moving' : ''}`}
        width="110" 
        height="50" 
        viewBox="0 0 140 60" 
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          {/* Gradients for metallic effect */}
          <linearGradient id={`bodyGradient-${color}`} x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style={{ stopColor: color, stopOpacity: 1 }} />
            <stop offset="40%" style={{ stopColor: color, stopOpacity: 0.85 }} />
            <stop offset="60%" style={{ stopColor: color, stopOpacity: 0.9 }} />
            <stop offset="100%" style={{ stopColor: color, stopOpacity: 1 }} />
          </linearGradient>
          
          <linearGradient id={`darkGradient-${color}`} x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style={{ stopColor: '#000', stopOpacity: 0.3 }} />
            <stop offset="100%" style={{ stopColor: '#000', stopOpacity: 0.1 }} />
          </linearGradient>
          
          {/* Shine effect */}
          <linearGradient id={`shine-${color}`} x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style={{ stopColor: 'white', stopOpacity: 0 }} />
            <stop offset="40%" style={{ stopColor: 'white', stopOpacity: 0.5 }} />
            <stop offset="60%" style={{ stopColor: 'white', stopOpacity: 0.5 }} />
            <stop offset="100%" style={{ stopColor: 'white', stopOpacity: 0 }} />
          </linearGradient>
          
          {/* Shadow */}
          <radialGradient id="carShadow">
            <stop offset="0%" style={{ stopColor: '#000', stopOpacity: 0.4 }} />
            <stop offset="100%" style={{ stopColor: '#000', stopOpacity: 0 }} />
          </radialGradient>
        </defs>
        
        {/* Ground Shadow */}
        <ellipse cx="70" cy="54" rx="50" ry="5" fill="url(#carShadow)" />
        
        {/* ===== MAIN CAR BODY ===== */}
        
        {/* Rear wing/spoiler */}
        <rect x="15" y="20" width="3" height="12" fill="#222" stroke="#000" strokeWidth="0.6" />
        <rect x="10" y="18" width="13" height="4" rx="1" fill={color} stroke="#000" strokeWidth="1" />
        <rect x="10" y="17" width="13" height="2" fill={`url(#darkGradient-${color})`} />
        
        {/* Main body - aerodynamic F1 style */}
        <path
          d="M 25 32 L 30 26 L 45 22 L 65 20 L 85 20 L 105 23 L 120 27 L 130 30 L 135 33 L 25 33 Z"
          fill={`url(#bodyGradient-${color})`}
          stroke="#000"
          strokeWidth="1.5"
        />
        
        {/* Top highlight/shine on body */}
        <path
          d="M 45 22.5 L 65 20.5 L 85 20.5 L 105 23.5 L 105 24.5 L 85 21.5 L 65 21.5 L 45 23.5 Z"
          fill={`url(#shine-${color})`}
          opacity="0.7"
        />
        
        {/* Side body panel */}
        <path
          d="M 25 33 L 28 30 L 125 30 L 132 33 L 135 37 L 25 37 Z"
          fill={color}
          stroke="#000"
          strokeWidth="1.5"
        />
        
        {/* Dark accent line */}
        <line x1="30" y1="33" x2="130" y2="33" stroke="#000" strokeWidth="1" opacity="0.3" />
        
        {/* Cockpit/Driver area */}
        <ellipse
          cx="75"
          cy="24"
          rx="18"
          ry="5"
          fill="rgba(20, 40, 80, 0.7)"
          stroke="#000"
          strokeWidth="1"
        />
        <ellipse
          cx="75"
          cy="23"
          rx="16"
          ry="4"
          fill="rgba(60, 120, 200, 0.4)"
        />
        
        {/* Windscreen reflection */}
        <ellipse
          cx="75"
          cy="22"
          rx="14"
          ry="2.5"
          fill="rgba(255, 255, 255, 0.3)"
        />
        
        {/* Air intake */}
        <rect x="70" y="26" width="10" height="4" rx="1" fill="#111" stroke="#000" strokeWidth="0.8" />
        <rect x="71" y="27" width="8" height="1.5" fill="#000" />
        
        {/* Side vents */}
        <g opacity="0.6">
          <line x1="90" y1="31" x2="95" y2="31" stroke="#000" strokeWidth="1.2" />
          <line x1="90" y1="33" x2="95" y2="33" stroke="#000" strokeWidth="1.2" />
          <line x1="90" y1="35" x2="95" y2="35" stroke="#000" strokeWidth="1.2" />
        </g>
        
        {/* Front nose cone */}
        <path
          d="M 120 27 L 135 30 L 137 32 L 137 34 L 135 35 L 120 32 Z"
          fill={color}
          stroke="#000"
          strokeWidth="1.2"
        />
        
        {/* Front wing */}
        <rect x="133" y="34" width="6" height="2" rx="0.5" fill="#222" stroke="#000" strokeWidth="0.8" />
        <rect x="133" y="37" width="6" height="1.5" rx="0.5" fill={color} stroke="#000" strokeWidth="0.8" opacity="0.8" />
        
        {/* Racing number */}
        <text x="60" y="36" fontSize="8" fontWeight="bold" fill="#fff" stroke="#000" strokeWidth="0.3" fontFamily="Arial">1</text>
        
        {/* Racing stripes */}
        <rect x="50" y="22" width="2" height="13" fill="rgba(255,255,255,0.4)" rx="0.5" />
        <rect x="90" y="22" width="2" height="11" fill="rgba(255,255,255,0.4)" rx="0.5" />
        
        {/* ===== WHEELS ===== */}
        
        {/* Rear wheel */}
        <g>
          {/* Tire */}
          <ellipse cx="40" cy="40" rx="8" ry="9" fill="#1a1a1a" stroke="#000" strokeWidth="1.5" />
          {/* Rim */}
          <ellipse cx="40" cy="40" rx="6" ry="7" fill="#2a2a2a" />
          <ellipse cx="40" cy="40" rx="4.5" ry="5.5" fill="#3a3a3a" />
          {/* Hub */}
          <ellipse cx="40" cy="40" rx="2.5" ry="3" fill="#4a4a4a" />
          <circle cx="40" cy="40" r="1.5" fill="#666" />
          {/* Spokes - animate opacity when moving to simulate spinning */}
          <line x1="40" y1="35" x2="40" y2="45" stroke="#555" strokeWidth="1">
            {isMoving && <animate attributeName="opacity" values="1;0.3;1" dur="0.6s" repeatCount="indefinite" />}
          </line>
          <line x1="35" y1="40" x2="45" y2="40" stroke="#555" strokeWidth="1">
            {isMoving && <animate attributeName="opacity" values="0.3;1;0.3" dur="0.6s" repeatCount="indefinite" />}
          </line>
          <line x1="37" y1="37" x2="43" y2="43" stroke="#555" strokeWidth="0.8" opacity="0.7">
            {isMoving && <animate attributeName="opacity" values="0.7;0.2;0.7" dur="0.6s" begin="0.15s" repeatCount="indefinite" />}
          </line>
          <line x1="37" y1="43" x2="43" y2="37" stroke="#555" strokeWidth="0.8" opacity="0.7">
            {isMoving && <animate attributeName="opacity" values="0.2;0.7;0.2" dur="0.6s" begin="0.15s" repeatCount="indefinite" />}
          </line>
          {/* Brake disc */}
          <ellipse cx="40" cy="40" rx="3.5" ry="4" fill="none" stroke="#888" strokeWidth="0.5" opacity="0.5" />
        </g>
        
        {/* Front wheel */}
        <g>
          {/* Tire */}
          <ellipse cx="115" cy="40" rx="8" ry="9" fill="#1a1a1a" stroke="#000" strokeWidth="1.5" />
          {/* Rim */}
          <ellipse cx="115" cy="40" rx="6" ry="7" fill="#2a2a2a" />
          <ellipse cx="115" cy="40" rx="4.5" ry="5.5" fill="#3a3a3a" />
          {/* Hub */}
          <ellipse cx="115" cy="40" rx="2.5" ry="3" fill="#4a4a4a" />
          <circle cx="115" cy="40" r="1.5" fill="#666" />
          {/* Spokes - animate opacity when moving to simulate spinning */}
          <line x1="115" y1="35" x2="115" y2="45" stroke="#555" strokeWidth="1">
            {isMoving && <animate attributeName="opacity" values="1;0.3;1" dur="0.6s" repeatCount="indefinite" />}
          </line>
          <line x1="110" y1="40" x2="120" y2="40" stroke="#555" strokeWidth="1">
            {isMoving && <animate attributeName="opacity" values="0.3;1;0.3" dur="0.6s" repeatCount="indefinite" />}
          </line>
          <line x1="112" y1="37" x2="118" y2="43" stroke="#555" strokeWidth="0.8" opacity="0.7">
            {isMoving && <animate attributeName="opacity" values="0.7;0.2;0.7" dur="0.6s" begin="0.15s" repeatCount="indefinite" />}
          </line>
          <line x1="112" y1="43" x2="118" y2="37" stroke="#555" strokeWidth="0.8" opacity="0.7">
            {isMoving && <animate attributeName="opacity" values="0.2;0.7;0.2" dur="0.6s" begin="0.15s" repeatCount="indefinite" />}
          </line>
          {/* Brake disc */}
          <ellipse cx="115" cy="40" rx="3.5" ry="4" fill="none" stroke="#888" strokeWidth="0.5" opacity="0.5" />
        </g>
        
        {/* Headlights */}
        <circle cx="133" cy="31" r="2.5" fill="#FFE600" opacity="0.9" stroke="#FFA500" strokeWidth="0.8">
          {isMoving && <animate attributeName="opacity" values="0.9;1;0.9" dur="1.5s" repeatCount="indefinite" />}
        </circle>
        <circle cx="133" cy="35" r="2.5" fill="#FFE600" opacity="0.9" stroke="#FFA500" strokeWidth="0.8">
          {isMoving && <animate attributeName="opacity" values="0.9;1;0.9" dur="1.5s" repeatCount="indefinite" />}
        </circle>
        
        {/* LED lights */}
        <rect x="131" y="29" width="1" height="1" fill="#00FFFF" opacity="0.8" />
        <rect x="131" y="36.5" width="1" height="1" fill="#00FFFF" opacity="0.8" />
        
        {/* Tail lights */}
        <rect x="24" y="30" width="2.5" height="2" rx="0.5" fill="#FF0000" opacity="0.85" stroke="#8B0000" strokeWidth="0.5" />
        <rect x="24" y="34" width="2.5" height="2" rx="0.5" fill="#FF0000" opacity="0.85" stroke="#8B0000" strokeWidth="0.5" />
        
        {/* Exhaust pipes */}
        <ellipse cx="26" cy="36" rx="1.5" ry="1.8" fill="#1a1a1a" stroke="#000" strokeWidth="0.8" />
        <ellipse cx="26" cy="36" rx="1" ry="1.2" fill="#2a2a2a" />
        
        {/* Exhaust smoke/flames when moving */}
        {isMoving && (
          <g className="exhaust-effects">
            <ellipse cx="18" cy="36" rx="3" ry="2.5" fill="#888" opacity="0.25" className="exhaust-smoke">
              <animate attributeName="rx" values="3;5;6" dur="1.2s" repeatCount="indefinite" />
              <animate attributeName="opacity" values="0.25;0.15;0" dur="1.2s" repeatCount="indefinite" />
            </ellipse>
            <ellipse cx="13" cy="36" rx="2.5" ry="2" fill="#888" opacity="0.2" className="exhaust-smoke">
              <animate attributeName="rx" values="2.5;4;5" dur="1.2s" begin="0.3s" repeatCount="indefinite" />
              <animate attributeName="opacity" values="0.2;0.1;0" dur="1.2s" begin="0.3s" repeatCount="indefinite" />
            </ellipse>
            {speed > 5 && (
              <>
                <ellipse cx="21" cy="36" rx="2" ry="1.5" fill="#ff6600" opacity="0.6" className="exhaust-flame">
                  <animate attributeName="rx" values="2;3;2" dur="0.5s" repeatCount="indefinite" />
                  <animate attributeName="opacity" values="0.6;0.8;0.6" dur="0.5s" repeatCount="indefinite" />
                </ellipse>
                <ellipse cx="22" cy="36" rx="1.5" ry="1" fill="#ffaa00" opacity="0.8" className="exhaust-flame">
                  <animate attributeName="rx" values="1.5;2.5;1.5" dur="0.4s" repeatCount="indefinite" />
                  <animate attributeName="opacity" values="0.8;1;0.8" dur="0.4s" repeatCount="indefinite" />
                </ellipse>
              </>
            )}
          </g>
        )}
      </svg>
    )
  }

  return (
    <div className="race-track-container">
      {/* Atmospheric background effects */}
      <div className="race-atmosphere">
        {speedLines.map(line => (
          <div
            key={line.id}
            className="speed-line"
            style={{
              top: `${line.top}%`,
              animationDuration: `${line.duration}s`
            }}
          />
        ))}
      </div>

      <div className="race-track-header">
        <div className="header-title-section">
          <div className="title-icon">🏁</div>
          <h2>RACING CIRCUIT</h2>
          <div className="title-icon">🏁</div>
        </div>
        <div className="race-status-badge" data-status={raceStatus}>
          <span className="status-indicator"></span>
          {raceStatus || 'CREATED'}
        </div>
      </div>

      <div className="race-track">
        {raceCars.length === 0 ? (
          <div className="no-cars">
            <div className="loading-spinner"></div>
            <p>Initializing race track...</p>
          </div>
        ) : (
          <>
            {raceCars.map((car, index) => {
              const position = car.position || 0
              const speed = car.speed || 0
              const progress = getProgress(position)
              const winner = isWinner(car.id)
              const rank = getRank(car.id)

              return (
                <div 
                  key={car.id} 
                  className={`race-lane ${winner ? 'winner' : ''} ${raceStatus === 'RUNNING' ? 'racing' : ''}`}
                >
                  <div className="lane-header">
                    <div className="car-info">
                      <div className={`rank-badge rank-${rank}`}>
                        #{rank}
                      </div>
                      <div 
                        className="car-marker" 
                        style={{ 
                          background: `linear-gradient(135deg, ${car.color}, ${car.color}dd)`,
                          boxShadow: `0 0 15px ${car.color}88`
                        }}
                      />
                      <span className="car-name">{car.name}</span>
                      {car.winProbability && (() => {
                        const minOdds = Math.min(...cars.map(c => c.winProbability || 2.0))
                        const maxOdds = Math.max(...cars.map(c => c.winProbability || 2.0))
                        const hasVariance = maxOdds !== minOdds
                        const isFavorite = hasVariance && car.winProbability === minOdds
                        const isLongshot = hasVariance && car.winProbability === maxOdds
                        
                        return (
                          <span className={`win-probability ${isFavorite ? 'favorite' : isLongshot ? 'longshot' : ''}`}>
                            <span className="odds-label">{isFavorite ? '⭐' : isLongshot ? '🎲' : ''}</span> {car.winProbability}x
                          </span>
                        )
                      })()}
                      {winner && (
                        <span className="winner-badge">
                          <span className="trophy-icon">🏆</span>
                          <span>WINNER</span>
                        </span>
                      )}
                    </div>
                    <div className="car-stats">
                      <div className="stat-item position-stat">
                        <span className="stat-label">Distance</span>
                        <span className="stat-value">{position.toFixed(0)}m</span>
                      </div>
                      {raceStatus === 'RUNNING' && (
                        <div className="stat-item speed-stat">
                          <span className="stat-label">Speed</span>
                          <span className="stat-value">{speed.toFixed(1)} m/s</span>
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="track-line">
                    {/* Asphalt road texture */}
                    <div className="track-background">
                      <div className="road-lines"></div>
                      <div className="finish-line" />
                      {/* Distance markers */}
                      <div className="distance-markers">
                        {[0, 250, 500, 750, 1000].map(dist => (
                          <div key={dist} className="marker" style={{ left: `${(dist / FINISH_LINE) * 100}%` }}>
                            <span>{dist}m</span>
                          </div>
                        ))}
                      </div>
                    </div>
                    
                    {/* Progress indicator with gradient */}
                    <div 
                      className="car-progress"
                      style={{ 
                        width: `${progress}%`
                      }}
                    >
                      <div className="progress-glow" style={{ background: `linear-gradient(90deg, transparent, ${car.color}44)` }}></div>
                    </div>

                    {/* Car */}
                    <div 
                      className="car-container"
                      style={{ 
                        left: `calc(${progress}% - 55px)`,
                      }}
                    >
                      <SVGCar color={car.color} speed={speed} />
                      {raceStatus === 'RUNNING' && speed > 0 && (
                        <div className="speed-particles">
                          <div className="particle"></div>
                          <div className="particle"></div>
                          <div className="particle"></div>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Progress bar underneath */}
                  <div className="progress-bar">
                    <div 
                      className="progress-fill"
                      style={{ 
                        width: `${progress}%`,
                        background: `linear-gradient(90deg, ${car.color}66, ${car.color})`
                      }}
                    >
                      <div className="progress-shine"></div>
                    </div>
                    <span className="progress-text">{progress.toFixed(1)}%</span>
                  </div>
                </div>
              )
            })}
          </>
        )}
      </div>

      <div className="track-footer">
        <div className="finish-marker">
          <div className="marker-item start-marker">
            <span className="marker-icon">🏁</span>
            <span className="marker-text">START LINE</span>
          </div>
          <div className="track-info">
            <span className="track-length">Track Length: {FINISH_LINE}m</span>
          </div>
          <div className="marker-item finish-marker-end">
            <span className="marker-text">FINISH LINE</span>
            <span className="marker-icon">🏁</span>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RaceTrack
