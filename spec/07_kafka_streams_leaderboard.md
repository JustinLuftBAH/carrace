# Streams
Input: race-events(CarPositionUpdated)
Group by raceId
Maintain KTable: carId->position
Sort desc by position
Emit leaderboard on change
Detect finish when max(position)>=1000 -> emit RaceFinished
