# System
Real-time multiplayer racing + betting.
Backend: Spring Boot. Events: Kafka (Confluent). Realtime: WebSocket.
Flow: create race→join→bet→start→emit car updates→compute leaderboard→finish→payout.
Rules: server authoritative, deterministic per raceId.
