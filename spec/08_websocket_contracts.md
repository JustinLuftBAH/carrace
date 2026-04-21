# WebSocket
Endpoint: /ws/race/{raceId}
Messages:
RaceStarted{raceId}
RaceUpdate{raceId,leaderboard:[{carId,position}]}
RaceFinished{raceId,winnerCarId}
Push interval ~100-500ms
