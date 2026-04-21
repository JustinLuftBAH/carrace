# API
POST /race/create -> {raceId}
POST /race/{id}/start -> 200
POST /race/{id}/join -> 200
POST /bet/place {userId,raceId,carId,amount}
GET /race/{id} -> {status,cars,leaderboard}
Errors: 400 invalid,404 not found,409 state conflict
