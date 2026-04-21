import React, { useState, useEffect, useCallback } from 'react'
import { userApi, raceApi, betApi } from '../services/api'
import websocket from '../services/websocket'
import confetti from 'canvas-confetti'
import Header from './Header'
import BettingPanel from './BettingPanel'
import RaceTrack from './RaceTrack'
import PlayerList from './PlayerList'
import ReadyTimer from './ReadyTimer'
import Shop from './Shop'
import './RaceLobby.css'

function RaceLobby({ user, onLogout, onUpdateUser }) {
  const [currentUser, setCurrentUser] = useState(user)
  const [race, setRace] = useState(null)
  const [cars, setCars] = useState([])
  const [participants, setParticipants] = useState([])
  const [myBet, setMyBet] = useState(null)
  const [isReady, setIsReady] = useState(false)
  const [allReady, setAllReady] = useState(false)
  const [readyCountdown, setReadyCountdown] = useState(0)
  const [leaderboard, setLeaderboard] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showShop, setShowShop] = useState(false)
  const [reactions, setReactions] = useState([])
  const [winnerAnnouncement, setWinnerAnnouncement] = useState(null)

  // Fetch updated user balance
  const refreshUser = useCallback(async () => {
    try {
      const response = await userApi.get(currentUser.userId)
      const updatedUser = response.data
      setCurrentUser(updatedUser)
      onUpdateUser(updatedUser)
      localStorage.setItem('user', JSON.stringify(updatedUser))
    } catch (err) {
      console.error('Failed to refresh user:', err)
    }
  }, [currentUser.userId, onUpdateUser])

  // Initialize race and WebSocket
  useEffect(() => {
    let raceId = null

    const initialize = async () => {
      try {
        // Create or get race
        const raceResponse = await raceApi.create()
        raceId = raceResponse.data.raceId
        
        // Get race details
        const raceDetails = await raceApi.get(raceId)
        setRace(raceDetails.data)
        setCars(raceDetails.data.cars || [])

        // Join race
        await raceApi.join(raceId, currentUser.userId)

        // Get participants
        const participantsResponse = await raceApi.getParticipants(raceId)
        setParticipants(participantsResponse.data || [])

        setLoading(false)

        // Connect WebSocket
        websocket.connect(
          () => {
            // Subscribe to race updates
            websocket.subscribeToRace(raceId, handleRaceUpdate)
            // Subscribe to lobby updates
            websocket.subscribeToLobby(raceId, handleLobbyUpdate)
          },
          (error) => {
            console.error('WebSocket error:', error)
            setError('Failed to connect to race server')
          }
        )
      } catch (err) {
        console.error('Failed to initialize:', err)
        setError('Failed to initialize race')
        setLoading(false)
      }
    }

    initialize()

    return () => {
      // Cleanup: Leave race when component unmounts
      if (raceId && currentUser.userId) {
        raceApi.leave(raceId, currentUser.userId).catch(err => 
          console.error('Failed to leave race:', err)
        )
      }
      websocket.disconnect()
    }
  }, [currentUser.userId])

  const handleRaceUpdate = (update) => {
    console.log('Race update:', update)
    
    if (update.leaderboard) {
      setLeaderboard(update.leaderboard)
    }

    if (update.raceId && update.status) {
      setRace(prev => ({ ...prev, status: update.status, winnerCarId: update.winnerCarId }))
    }

    // If race finished, refresh user balance
    if (update.status === 'FINISHED' || update.type === 'RACE_FINISHED') {
      setTimeout(refreshUser, 1000)
      
      // Show winner announcement
      if (update.winnerCarId) {
        const winnerCar = cars.find(c => c.id === update.winnerCarId)
        if (winnerCar) {
          setWinnerAnnouncement(`🏆 ${winnerCar.name} wins the race!`)
          setTimeout(() => setWinnerAnnouncement(null), 5000)
        }
      }
      
      // Trigger confetti if user won
      if (update.winnerCarId && myBet && myBet.carId === update.winnerCarId) {
        celebrateWin()
      }
    }
  }

  const celebrateWin = () => {
    // Fire confetti from multiple angles
    const duration = 3000
    const end = Date.now() + duration

    const colors = ['#FFD700', '#FFA500', '#FF6347', '#00FF00', '#00CED1']

    const frame = () => {
      confetti({
        particleCount: 5,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: colors
      })
      confetti({
        particleCount: 5,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: colors
      })

      if (Date.now() < end) {
        requestAnimationFrame(frame)
      }
    }

    // Initial burst
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    })

    frame()
  }

  const handleLobbyUpdate = (update) => {
    console.log('Lobby update:', update)
    
    if (update.participants) {
      setParticipants(update.participants)
      
      // Check if all ready
      const allPlayersReady = update.participants.length > 0 && 
        update.participants.every(p => p.ready)
      setAllReady(allPlayersReady)
    }

    if (update.readyCountdown !== undefined) {
      setReadyCountdown(update.readyCountdown)
    }

    if (update.raceStarting) {
      // Race is about to start
      console.log('Race starting!')
    }
    
    // Handle reactions
    if (update.type === 'REACTION' && update.reaction) {
      setReactions(prev => [...prev, update.reaction])
      // Remove reaction after 3 seconds
      setTimeout(() => {
        setReactions(prev => prev.filter(r => r.timestamp !== update.reaction.timestamp))
      }, 3000)
    }
  }

  const handlePlaceBet = async (carId, amount) => {
    try {
      await betApi.place(currentUser.userId, race.raceId, carId, amount)
      setMyBet({ carId, amount })
      
      // Refresh user balance
      await refreshUser()
    } catch (err) {
      console.error('Failed to place bet:', err)
      throw err
    }
  }

  const handleToggleReady = async () => {
    try {
      if (isReady) {
        await raceApi.unready(race.raceId, currentUser.userId)
        setIsReady(false)
      } else {
        if (!myBet) {
          alert('Please place a bet before readying up!')
          return
        }
        await raceApi.ready(race.raceId, currentUser.userId)
        setIsReady(true)
      }
    } catch (err) {
      console.error('Failed to toggle ready:', err)
      setError('Failed to update ready status')
    }
  }

  if (loading) {
    return (
      <div className="lobby-container">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading race...</p>
        </div>
      </div>
    )
  }

  if (error && !race) {
    return (
      <div className="lobby-container">
        <div className="error-container">
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            Retry
          </button>
        </div>
      </div>
    )
  }

  const isRaceRunning = race?.status === 'RUNNING'
  const isRaceFinished = race?.status === 'FINISHED'
  const canBet = race?.status === 'CREATED' && !myBet
  const canReady = race?.status === 'CREATED' && myBet && !isRaceRunning

  return (
    <div className="lobby-container">
      <Header 
        user={currentUser} 
        onLogout={onLogout}
        onRefresh={refreshUser}
        onOpenShop={() => setShowShop(true)}
      />

      {showShop && (
        <Shop 
          user={currentUser}
          onClose={async () => {
            setShowShop(false)
            // Refresh participants to show updated cosmetics
            try {
              const participantsResponse = await raceApi.getParticipants(race.raceId)
              setParticipants(participantsResponse.data || [])
            } catch (err) {
              console.error('Failed to refresh participants:', err)
            }
          }}
          onUpdateUser={refreshUser}
        />
      )}

      <div className="lobby-content">
        <div className="main-section">
          {/* Ready Timer */}
          {allReady && readyCountdown > 0 && !isRaceRunning && (
            <ReadyTimer countdown={readyCountdown} />
          )}

          {/* Winner Announcement */}
          {winnerAnnouncement && (
            <div className="winner-announcement">
              <h2>{winnerAnnouncement}</h2>
            </div>
          )}

          {/* Race Track */}
          <RaceTrack 
            cars={cars}
            leaderboard={leaderboard}
            raceStatus={race?.status}
            winnerCarId={race?.winnerCarId}
          />

          {/* Betting Panel */}
          {!isRaceRunning && !isRaceFinished && (
            <BettingPanel
              cars={cars}
              balance={currentUser.balance}
              myBet={myBet}
              onPlaceBet={handlePlaceBet}
              disabled={!canBet}
            />
          )}

          {/* Ready Button */}
          {canReady && (
            <div className="ready-section">
              <button
                className={`btn-ready ${isReady ? 'ready' : ''}`}
                onClick={handleToggleReady}
              >
                {isReady ? '✓ READY' : 'READY UP'}
              </button>
              {!myBet && (
                <p className="ready-hint">Place a bet first!</p>
              )}
            </div>
          )}

          {/* Race Status */}
          {isRaceRunning && (
            <div className="race-status running">
              <h2>🏁 RACE IN PROGRESS</h2>
              <p>Watch the cars compete!</p>
            </div>
          )}

          {isRaceFinished && (
            <div className="race-status finished">
              <h2>🏆 RACE FINISHED!</h2>
              {race.winnerCarId && (
                <p>
                  Winner: {cars.find(c => c.id === race.winnerCarId)?.name || `Car ${race.winnerCarId}`}
                </p>
              )}
              <button 
                className="btn-primary"
                onClick={() => window.location.reload()}
              >
                New Race
              </button>
            </div>
          )}
        </div>

        {/* Player List Sidebar */}
        <div className="sidebar-section">
          <PlayerList 
            participants={participants}
            currentUserId={currentUser.userId}
            raceId={race?.raceId}
            reactions={reactions}
          />
        </div>
      </div>
    </div>
  )
}

export default RaceLobby
