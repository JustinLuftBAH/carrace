# KafkaEvents
Topics:
race-events(key=raceId)
bet-events(key=raceId)
payout-events(key=raceId)
Events:
RaceStarted{raceId}
CarPositionUpdated{raceId,carId,position,speed,timestamp}
RaceFinished{raceId,winnerCarId}
BetPlaced{userId,raceId,carId,amount}
PayoutCalculated{userId,raceId,amount}
Ordering guaranteed per raceId.
