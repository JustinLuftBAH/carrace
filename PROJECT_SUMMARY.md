# Car Race Application - Project Summary

## What You Have

A complete, production-ready real-time multiplayer car racing application with betting functionality.

## Project Structure

```
carrace/
├── spec/                          # Your original specifications
│   ├── 00_system_overview.md
│   ├── 01_domain_model.md
│   ├── 02_game_state_machine.md
│   ├── 03_multiplayer_lifecycle.md
│   ├── 04_betting_rules.md
│   ├── 05_api_contracts.md
│   ├── 06_kafka_event_contracts.md
│   ├── 07_kafka_streams_leaderboard.md
│   ├── 08_websocket_contracts.md
│   ├── 09_security_and_fairness_rules.md
│   └── 10_error_handling.md
│
├── src/main/java/com/carrace/
│   ├── CarRaceApplication.java    # Main application entry point
│   │
│   ├── config/                     # Configuration classes
│   │   ├── DataInitializer.java   # Sample data creation
│   │   ├── KafkaConsumerConfig.java
│   │   ├── KafkaStreamsConfig.java
│   │   ├── KafkaTopicConfig.java
│   │   └── WebSocketConfig.java
│   │
│   ├── controller/                 # REST API endpoints
│   │   ├── BettingController.java # POST /bet/place
│   │   ├── RaceController.java    # Race CRUD operations
│   │   └── UserController.java    # User management
│   │
│   ├── dto/                        # Data Transfer Objects
│   │   ├── CreateRaceResponse.java
│   │   ├── PlaceBetRequest.java
│   │   └── RaceDetailsResponse.java
│   │
│   ├── entity/                     # JPA Database Entities
│   │   ├── Bet.java               # Betting records
│   │   ├── Car.java               # Car data
│   │   ├── Race.java              # Race data
│   │   └── User.java              # User accounts
│   │
│   ├── event/                      # Kafka Event DTOs
│   │   ├── BetPlacedEvent.java
│   │   ├── CarPositionUpdatedEvent.java
│   │   ├── LeaderboardUpdateEvent.java
│   │   ├── PayoutCalculatedEvent.java
│   │   ├── RaceFinishedEvent.java
│   │   └── RaceStartedEvent.java
│   │
│   ├── exception/                  # Error handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InsufficientBalanceException.java
│   │   ├── InvalidStateException.java
│   │   ├── RaceNotFoundException.java
│   │   └── UserNotFoundException.java
│   │
│   ├── kafka/                      # Kafka integration
│   │   ├── KafkaEventProducer.java    # Sends events to Kafka
│   │   └── RaceEventConsumer.java     # Consumes events from Kafka
│   │
│   ├── model/                      # Enums and models
│   │   ├── BetStatus.java         # PENDING, WON, LOST
│   │   └── RaceStatus.java        # CREATED, RUNNING, FINISHED
│   │
│   ├── repository/                 # Database access
│   │   ├── BetRepository.java
│   │   ├── CarRepository.java
│   │   ├── RaceRepository.java
│   │   └── UserRepository.java
│   │
│   ├── service/                    # Business logic
│   │   ├── BettingService.java    # Bet placement logic
│   │   ├── PayoutService.java     # Payout calculations
│   │   ├── RaceService.java       # Race management
│   │   └── RaceSimulationService.java # Auto race simulation
│   │
│   ├── streams/                    # Kafka Streams
│   │   └── LeaderboardStreamsProcessor.java # Real-time leaderboard
│   │
│   └── websocket/                  # WebSocket support
│       └── WebSocketService.java  # Push updates to clients
│
├── src/main/resources/
│   ├── application.properties              # Main config
│   ├── application-confluent.properties    # Confluent Cloud config (detailed guide)
│   └── static/
│       └── race-viewer.html               # Live race viewer UI
│
├── pom.xml                        # Maven dependencies
├── README.md                      # Main documentation
├── CONFLUENT_SETUP_GUIDE.md      # Step-by-step Confluent Cloud setup
├── api-examples.http             # API request examples
├── start.sh                      # Quick start script
└── test-race.sh                 # Test race script
```

## Key Features Implemented

### ✅ All Specifications Met

1. **System Overview (00)**: Complete event-driven architecture
2. **Domain Model (01)**: All entities implemented with JPA
3. **State Machine (02)**: Race status transitions enforced
4. **Multiplayer Lifecycle (03)**: WebSocket integration
5. **Betting Rules (04)**: All constraints enforced
6. **API Contracts (05)**: All endpoints implemented
7. **Kafka Events (06)**: All events published/consumed
8. **Kafka Streams (07)**: Leaderboard processing
9. **WebSocket (08)**: Real-time updates every 200ms
10. **Security (09)**: Server-authoritative, input validation
11. **Error Handling (10)**: Global exception handling

### 🎯 Core Functionality

- **Race Creation**: Auto-creates 4 cars with random colors
- **Betting System**: Users can bet on cars before race starts
- **Race Simulation**: Automatic car movement with random speeds
- **Real-time Updates**: WebSocket pushes leaderboard every 200ms
- **Kafka Integration**: All events go through Confluent Cloud
- **Kafka Streams**: Real-time leaderboard aggregation
- **Payout System**: Automatic payout calculation (2x multiplier)
- **Balance Management**: Optimistic locking for concurrent updates

### 🛡️ Production-Ready Features

- Global exception handling
- Input validation (Bean Validation)
- Optimistic locking for race conditions
- Transaction management
- Comprehensive logging
- Health checks (Spring Actuator)
- H2 console for debugging
- Sample data initialization

## How to Use

### Quick Start (3 steps!)

1. **Configure Confluent Cloud**
   ```bash
   # Open and follow the instructions in:
   src/main/resources/application-confluent.properties
   
   # Or read the detailed guide:
   cat CONFLUENT_SETUP_GUIDE.md
   ```

2. **Build and Run**
   ```bash
   ./start.sh
   ```

3. **Test It**
   ```bash
   # In another terminal:
   ./test-race.sh
   ```

### Manual Testing

```bash
# 1. Create a user
curl -X POST http://localhost:8080/user/create \
  -H "Content-Type: application/json" \
  -d '{"username":"test","balance":1000}'

# 2. Create a race
curl -X POST http://localhost:8080/race/create

# 3. Place a bet
curl -X POST http://localhost:8080/bet/place \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"raceId":1,"carId":2,"amount":100}'

# 4. Start the race
curl -X POST http://localhost:8080/race/1/start

# 5. Watch live at:
open http://localhost:8080/race-viewer.html
```

## Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Backend Framework | Spring Boot 3.2.4 | Application foundation |
| Language | Java 17 | Programming language |
| Build Tool | Maven | Dependency management |
| Database | H2 (in-memory) | Data persistence |
| ORM | Spring Data JPA | Database access |
| Event Streaming | Kafka (Confluent Cloud) | Event-driven architecture |
| Stream Processing | Kafka Streams | Real-time aggregation |
| Real-time Updates | WebSocket (STOMP) | Push notifications |
| Validation | Bean Validation | Input validation |
| Serialization | Jackson | JSON processing |
| Utilities | Lombok | Reduce boilerplate |

## What Makes This Special

### 1. Confluent Cloud Ready
- **Detailed configuration guide** with step-by-step instructions
- Comments assume you've never used Confluent Cloud
- Clear explanation of every configuration property
- Security best practices included

### 2. Event-Driven Architecture
- All state changes produce Kafka events
- Events are ordered by raceId (Kafka partitioning)
- Idempotent event processing
- Exactly-once semantics where possible

### 3. Real-time Processing
- Kafka Streams for leaderboard aggregation
- WebSocket for instant UI updates
- 200ms update interval (configurable)
- Smooth race progression

### 4. Production Patterns
- Repository pattern for data access
- Service layer for business logic
- DTO pattern for API contracts
- Global exception handling
- Optimistic locking for concurrency
- Transaction boundaries

## Configuration Highlights

### application-confluent.properties

This file is **extensively documented** with:
- ✅ Step-by-step Confluent Cloud account creation
- ✅ How to create a Kafka cluster
- ✅ Where to find your bootstrap server
- ✅ How to create API keys
- ✅ Security warnings and best practices
- ✅ Verification checklist
- ✅ Troubleshooting section

**Every single property is explained** as if you've never used Kafka or Confluent Cloud before!

## Race Flow Example

```
1. User creates account (balance: $1000)
   ↓
2. User creates race
   - Race ID: 1
   - Status: CREATED
   - 4 cars created automatically
   ↓
3. User places bet
   - Bet $100 on Car 2
   - Balance deducted: $900
   - Bet status: PENDING
   ↓
4. User starts race
   - Status: CREATED → RUNNING
   - RaceStartedEvent → Kafka
   - Betting locked
   ↓
5. Race simulation begins
   - Every 200ms: cars move forward
   - CarPositionUpdatedEvent → Kafka
   - Kafka Streams aggregates leaderboard
   - WebSocket pushes updates to clients
   ↓
6. Car reaches position 1000
   - Status: RUNNING → FINISHED
   - RaceFinishedEvent → Kafka
   - Winner determined
   ↓
7. Payout calculation (Kafka consumer)
   - If bet on winner: payout $200 (2x)
   - Balance updated: $900 + $200 = $1100
   - PayoutCalculatedEvent → Kafka
   ↓
8. User checks balance
   - GET /user/1
   - Balance: $1100 (won!)
```

## Files You Need to Configure

### MUST Configure
- `src/main/resources/application-confluent.properties`
  - Add your Confluent Cloud bootstrap server
  - Add your API key
  - Add your API secret

### Already Configured (No Changes Needed)
- `pom.xml` - Dependencies
- `application.properties` - App settings
- All Java source files

## Testing the Application

### 1. Live Race Viewer
```bash
# Start app, then open browser:
http://localhost:8080/race-viewer.html

# Enter race ID and click "Connect to Race"
# You'll see real-time car positions!
```

### 2. H2 Database Console
```bash
# While app is running:
http://localhost:8080/h2-console

# JDBC URL: jdbc:h2:mem:carracedb
# Username: sa
# Password: (leave empty)

# Then run SQL:
SELECT * FROM RACES;
SELECT * FROM CARS;
SELECT * FROM BETS;
SELECT * FROM USERS;
```

### 3. Confluent Cloud Console
```bash
# Check messages in topics:
1. Go to https://confluent.cloud/
2. Select your cluster
3. Click "Topics"
4. Click "race-events"
5. Click "Messages" tab
6. Click "Play" to see live events
```

## Next Steps

1. ✅ Configure Confluent Cloud (follow CONFLUENT_SETUP_GUIDE.md)
2. ✅ Run the application (./start.sh)
3. ✅ Test it (./test-race.sh)
4. ✅ Watch a race (race-viewer.html)
5. ✅ Check Kafka messages (Confluent Cloud console)

## Customization Ideas

- Change payout multiplier in `application.properties`
- Adjust race finish position (default: 1000)
- Change number of cars per race
- Modify car speed range in `RaceSimulationService`
- Add more car colors
- Implement user authentication
- Add race history
- Create a frontend UI
- Add more bet types (e.g., top 3)

## Support Resources

- Main README: `README.md`
- Confluent Setup: `CONFLUENT_SETUP_GUIDE.md`
- API Examples: `api-examples.http`
- Specification: `spec/` directory
- Code Comments: Extensive inline documentation

Enjoy racing! 🏁
