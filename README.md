# 🏁 CarRace - Multiplayer Racing Game

Real-time multiplayer car racing with betting, built with Spring Boot, Kafka, and React.

## 🎮 Features

- **Real-time racing** - Watch cars race live with WebSocket updates
- **Multiplayer betting** - Place bets and win money
- **Balanced odds** - Every car has a fair chance to win (~35-40% for favorite)
- **Shop system** - Customize your profile with emojis and colors
- **Persistent data** - User accounts saved in local database

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+ (for local frontend dev)
- Confluent Cloud account (free tier)

### 1. Configure Kafka
```bash
cp .env.example .env
# Edit .env with your Confluent Cloud credentials
# Get free account at: https://confluent.cloud
```

Your `.env` should have:
```bash
CONFLUENT_BOOTSTRAP_SERVERS=pkc-xxxxx.region.cloud.confluent.cloud:9092
CONFLUENT_API_KEY=your-api-key
CONFLUENT_API_SECRET=your-secret
```

### 2. Start Application
```bash
# Build and start everything (backend + frontend)
./start-fullstack.sh
```

Then open: **http://localhost:3000**

### 3. Play!
1. Register a username
2. Join a race
3. Place a bet on a car
4. Click "READY UP"
5. Race starts when all betters are ready!

## 🛑 Stop Application
```bash
# Stop frontend: Ctrl+C
# Stop backend: pkill -f carrace
```

## 🌐 Multiplayer (LAN)

Other computers on your network can connect to:
```
http://YOUR_IP:8080
```

Find your IP:
```bash
ipconfig getifaddr en0  # macOS
```

## 📁 Project Structure

```
carrace/
├── src/main/java/         # Backend (Spring Boot)
├── frontend/src/          # Frontend (React)
├── data/                  # Database files (auto-created)
├── start-fullstack.sh     # Start script
└── .env                   # Kafka credentials (git-ignored)
```

## 🔧 Development

### Backend Only
```bash
export $(cat .env | grep -v '^#' | xargs)
mvn clean package -DskipTests
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
```
Backend runs on: http://localhost:8080

### Frontend Only
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: http://localhost:3000

## 🎯 Tech Stack

**Backend:** Spring Boot 3.2, Java 17, Kafka Streams, WebSocket, H2 Database  
**Frontend:** React 18, Vite, Axios, STOMP/SockJS  
**Messaging:** Confluent Cloud Kafka

## 📝 API Endpoints

- `POST /api/user/create` - Register user
- `GET /api/user/login/{username}` - Login
- `POST /api/race/create` - Create race
- `POST /api/bet/place` - Place bet
- `POST /api/race/{id}/ready` - Ready up
- `GET /api/shop/items` - Get shop items

WebSocket: `/ws` → Subscribe to `/topic/race/{raceId}` and `/topic/lobby/{raceId}`

## 🐛 Troubleshooting

**Backend won't start?**
- Check .env file has valid Kafka credentials
- Make sure port 8080 is free

**Frontend can't connect?**
- Backend must be running on port 8080
- Check browser console for errors

**Race won't start?**
- All players with bets must click "READY UP"
- Wait for 5-second countdown

## 📄 License

MIT
