# Domain
User{id,balance}
Race{id,status[CREATED|RUNNING|FINISHED]}
Car{id,raceId,position,speed}
Bet{id,userId,raceId,carId,amount,status[PENDING|WON|LOST]}
RaceEvent{raceId,carId,position,speed,timestamp}
Relations: Race has Cars; User places Bet on Car in Race.
