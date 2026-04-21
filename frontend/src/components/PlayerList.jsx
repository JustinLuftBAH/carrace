import React, { useState } from 'react'
import { reactionApi } from '../services/api'
import './PlayerList.css'

function PlayerList({ participants, currentUserId, raceId, reactions }) {
  const [showReactionMenu, setShowReactionMenu] = useState(null)
  const availableReactions = ['👍', '🔥', '😂']

  if (!participants || participants.length === 0) {
    return (
      <div className="player-list">
        <h3>Players</h3>
        <div className="no-players">
          <p>Waiting for players...</p>
        </div>
      </div>
    )
  }

  const handleReactionClick = async (emoji) => {
    try {
      await reactionApi.send(currentUserId, raceId, emoji)
      setShowReactionMenu(null)
    } catch (err) {
      console.error('Failed to send reaction:', err)
    }
  }

  const getPlayerReaction = (userId) => {
    if (!reactions) return null
    return reactions.find(r => r.userId === userId && 
      Date.now() - r.timestamp < 3000) // Show for 3 seconds
  }

  return (
    <div className="player-list">
      <h3>
        Players ({participants.length})
      </h3>
      
      <div className="players">
        {participants.map((participant) => {
          const reaction = getPlayerReaction(participant.userId)
          const isCurrentUser = participant.userId === currentUserId
          
          return (
            <div 
              key={participant.userId}
              className={`player-item ${isCurrentUser ? 'current-user' : ''} ${participant.ready ? 'ready' : ''}`}
            >
              <div className="player-left">
                {isCurrentUser && (
                  <div className="reaction-trigger">
                    <button 
                      className="reaction-button"
                      onClick={() => setShowReactionMenu(showReactionMenu === participant.userId ? null : participant.userId)}
                    >
                      😊
                    </button>
                    {showReactionMenu === participant.userId && (
                      <div className="reaction-menu">
                        {availableReactions.map(emoji => (
                          <button
                            key={emoji}
                            className="reaction-option"
                            onClick={() => handleReactionClick(emoji)}
                          >
                            {emoji}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                )}
                
                <div 
                  className="player-avatar"
                  title={participant.profilePicture || '🏎️'}
                >
                  {participant.profilePicture || participant.username?.charAt(0).toUpperCase() || '?'}
                </div>
              </div>
              
              <div className="player-info">
                <div className="player-name-container">
                  <div 
                    className="player-name"
                    style={{ color: participant.nameColor || '#FFFFFF' }}
                  >
                    {participant.username}
                    {isCurrentUser && <span className="you-badge">(You)</span>}
                  </div>
                  {participant.customNametag && (
                    <span className="player-nametag">{participant.customNametag}</span>
                  )}
                </div>
                
                <div className="player-balance">
                  💰 ${participant.balance?.toFixed(2) || '0.00'}
                </div>
                
                {participant.bet && (
                  <div className="player-bet">
                    {participant.bet.amount === 0 ? (
                      <span>🎁 FREE BET on {participant.bet.carName}</span>
                    ) : (
                      <span>🎲 ${participant.bet.amount.toFixed(2)} on {participant.bet.carName}</span>
                    )}
                  </div>
                )}
              </div>

              <div className="player-status">
                {reaction && (
                  <span className="player-reaction">{reaction.emoji}</span>
                )}
                {participant.ready ? (
                  <span className="status-ready">✓ Ready</span>
                ) : (
                  <span className="status-waiting">⏱ Waiting</span>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default PlayerList
