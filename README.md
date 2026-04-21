# 🏁 Car Race - Real-time Multiplayer Racing with Betting

A full-stack real-time multiplayer car racing application with betting, built with Spring Boot, Apache Kafka (Confluent Cloud), WebSocket, and React.

## ✨ Features

### 🎉 New Features
- **🎊 Confetti Celebration**: Win a race and get showered with confetti!
- **💾 Persistent Storage**: User accounts and balances saved locally in `./data` folder
- **🔐 Secure Credentials**: API keys stored in `.env` file (git-ignored for safety)

### Frontend (React)
- 🎨 Modern cyberpunk-themed UI
- 🔐 User registration & login
- 💰 Real-time balance display
- 🎰 Interactive betting system
- ✅ Ready-up system with 5-second countdown
- 🏁 Live race visualization with animated cars
- 👥 Player list showing ready status
- 🔌 WebSocket real-time updates

### Backend (Spring Boot + Kafka)
- 🏎️ Automatic race simulation
- 💸 Betting and payout system
- 📊 Real-time leaderboard with Kafka Streams
- 🔄 Event-driven architecture
- 🛡️ Server-authoritative game state
- 🔌 WebSocket broadcasting

## 🏗️ Architecture

- **Frontend**: React 18 + Vite + React Router
- **Backend**: Spring Boot 3.2.4 with Java 17
- **Event Streaming**: Apache Kafka (Confluent Cloud)
- **Real-time Updates**: WebSocket with STOMP/SockJS
- **Database**: H2 (in-memory)
- **Stream Processing**: Kafka Streams

## 🚀 How to Run (From carrace Folder)

### ⚙️ First-Time Setup

**Before running, you need to configure Confluent Cloud credentials:**

1. **Copy the environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your Confluent Cloud credentials:**
   - Sign up for free at https://confluent.cloud/
   - Follow the guide: `CONFLUENT_SETUP_GUIDE.md`
   - Add your credentials to `.env`

3. **Your `.env` should look like:**
   ```bash
   CONFLUENT_BOOTSTRAP_SERVERS=pkc-xxxxx.us-east-1.aws.confluent.cloud:9092
   CONFLUENT_API_KEY=YOUR_ACTUAL_API_KEY
   CONFLUENT_API_SECRET=YOUR_ACTUAL_SECRET
   ```

📖 **See `ENVIRONMENT_SETUP.md` for detailed security and setup information.**

---

### Prerequisites

Make sure you have these installed:
- **Java 17+** - Check with: `java -version`
- **Maven 3.6+** - Check with: `mvn -version`
- **Node.js 16+** - Check with: `node -version`
- **npm** - Check with: `npm -version`
- **Confluent Cloud account** - Free tier at https://confluent.cloud/

---

### Option 1: Run Full Stack (Recommended) 🎯

**Runs both backend and frontend together - easiest way to start!**

```bash
# From the carrace folder:
./start-fullstack.sh
```

This script will:
1. ✅ Build the backend (if not built)
2. ✅ Start backend on port 8080 (in background)
3. ✅ Install frontend dependencies (if needed)
4. ✅ Start frontend on port 3000 (in foreground)

Then open your browser to: **http://localhost:3000**

To stop:
- Press `Ctrl+C` to stop frontend
- Kill backend: `pkill -f carrace-1.0.0.jar`

---

### Option 2: Run Backend and Frontend Separately 🔧

#### Terminal 1 - Backend

```bash
# From the carrace folder:
./start.sh
```

This will:
1. Build the application with Maven
2. Start the Spring Boot backend on port 8080
3. Connect to Confluent Cloud
4. Start WebSocket server

**Backend will be ready when you see:**
```
Started CarRaceApplication in X.XXX seconds
```

Backend runs at: **http://localhost:8080**

#### Terminal 2 - Frontend

```bash
# From the carrace folder:
cd frontend
npm install          # First time only
npm run dev
```

**Frontend will be ready when you see:**
```
VITE vX.X.X  ready in XXX ms

➜  Local:   http://localhost:3000/
```

Frontend runs at: **http://localhost:3000**

---

### Option 3: Manual Step-by-Step (For Debugging) 🛠️

#### Step 1: Configure Confluent Cloud ☁️

**⚠️ IMPORTANT: Do this BEFORE starting the app!**

1. Open: `src/main/resources/application-confluent.properties`
2. Follow the detailed step-by-step guide inside that file
3. Fill in these 3 values:
   ```properties
   spring.kafka.bootstrap-servers=YOUR_BOOTSTRAP_SERVER
   confluent.cloud.api.key=YOUR_API_KEY
   confluent.cloud.api.secret=YOUR_API_SECRET
   ```

**Need help?** See detailed guide: [CONFLUENT_SETUP_GUIDE.md](CONFLUENT_SETUP_GUIDE.md)

#### Step 2: Build the Backend

```bash
# From the carrace folder:
mvn clean package -DskipTests
```

You should see:
```
BUILD SUCCESS
```

This creates: `target/carrace-1.0.0.jar`

#### Step 3: Start the Backend

```bash
# From the carrace folder:
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
```

Wait until you see:
```
Started CarRaceApplication in X.XXX seconds
```

Backend is now running on **http://localhost:8080**

#### Step 4: Install Frontend Dependencies

```bash
# From the carrace folder:
cd frontend
npm install
```

This downloads all React dependencies (only needed once).

#### Step 5: Start the Frontend

```bash
# From the frontend folder:
npm run dev
```

Wait until you see:
```
➜  Local:   http://localhost:3000/
```

Frontend is now running on **http://localhost:3000**

---

## 🎮 How to Play

### 1. Open the App
Navigate to: **http://localhost:3000**

### 2. Register an Account
- Click "Register here"
- Enter a username
- Set starting balance (default: $1000)
- Click "Create Account"

### 3. Join a Race
- You'll automatically join a race lobby
- See 4 cars with different colors
- Your balance is shown in the top-right corner

### 4. Place a Bet
- Click on a car to select it
- Choose bet amount (quick buttons: $50, $100, $250, $500)
- See potential win (2x your bet)
- Click "Place Bet"
- Your balance updates immediately

### 5. Ready Up
- After betting, click "READY UP" button
- Your status changes to "✓ Ready"
- See other players' ready status in the player list

### 6. Race Starts
- When ALL players are ready for 5 seconds:
  - Full-screen countdown appears (5, 4, 3, 2, 1)
  - Race automatically starts!
- Watch cars race in real-time with progress bars
- Leaderboard updates live

### 7. Race Finishes
- First car to 1000m wins
- Winner highlighted in gold 🏆
- Balance automatically updated
- Click "New Race" to play again

---

## 🧪 Testing with Multiple Players

Open **multiple browser tabs/windows**:

```bash
# Each tab at: http://localhost:3000
```

1. Register different users in each tab
2. All join the same race automatically
3. Place bets in each window
4. Click ready in each
5. When all ready, race auto-starts!
6. Watch synchronized race in all windows

---

## 🔍 Verify Everything is Working

### Check Backend is Running

```bash
# From any terminal:
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### Check Frontend is Running

Open browser to: **http://localhost:3000**

You should see the login page with:
- "CAR RACE" title
- Username input field
- "LOGIN" and "Register here" buttons

### Check WebSocket Connection

1. Open browser to http://localhost:3000
2. Open browser DevTools (F12)
3. Go to Network tab
4. Filter by "WS" (WebSocket)
5. You should see a connection to `/ws`

### Check Kafka is Connected

Look in backend terminal/logs for:
```
Connected to Confluent Cloud
Kafka Streams started successfully
```

If you see errors about authentication, check your Confluent Cloud credentials.

---

## 📂 Project Structure

```
carrace/
├── src/                           # Backend source code
│   ├── main/
│   │   ├── java/                  # Java source files
│   │   │   └── com/carrace/
│   │   │       ├── controller/    # REST & WebSocket controllers
│   │   │       ├── service/       # Business logic
│   │   │       ├── model/         # JPA entities
│   │   │       ├── dto/           # Data transfer objects
│   │   │       └── config/        # Configuration classes
│   │   └── resources/
│   │       ├── application.properties           # Default config
│   │       └── application-confluent.properties # Confluent config
│   └── test/                      # Backend tests
├── frontend/                      # React frontend
│   ├── src/
│   │   ├── components/            # React components
│   │   │   ├── Login.jsx          # Login page
│   │   │   ├── Register.jsx       # Registration page
│   │   │   ├── RaceLobby.jsx      # Main game lobby
│   │   │   ├── Header.jsx         # Top bar with balance
│   │   │   ├── BettingPanel.jsx   # Bet placement UI
│   │   │   ├── RaceTrack.jsx      # Visual race display
│   │   │   ├── PlayerList.jsx     # Player list sidebar
│   │   │   └── ReadyTimer.jsx     # Countdown overlay
│   │   ├── services/
│   │   │   ├── api.js             # REST API client
│   │   │   └── websocket.js       # WebSocket client
│   │   ├── App.jsx                # Main app component
│   │   └── main.jsx               # React entry point
│   ├── index.html                 # HTML template
│   ├── package.json               # npm dependencies
│   └── vite.config.js             # Vite configuration
├── target/                        # Build output
│   └── carrace-1.0.0.jar          # Executable JAR
├── spec/                          # Original specifications
├── start.sh                       # Backend startup script
├── start-fullstack.sh             # Full stack startup script
├── test-race.sh                   # Test script
├── pom.xml                        # Maven configuration
├── README.md                      # This file
├── FRONTEND_GUIDE.md              # Frontend details
├── CONFLUENT_SETUP_GUIDE.md       # Confluent Cloud setup
└── PROJECT_SUMMARY.md             # Complete overview
```

---

## 🌐 Ports & URLs

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| Frontend | 3000 | http://localhost:3000 | React app (Vite dev server) |
| Backend API | 8080 | http://localhost:8080 | Spring Boot REST API |
| WebSocket | 8080 | ws://localhost:8080/ws | Real-time updates |
| H2 Console | 8080 | http://localhost:8080/h2-console | Database admin |
| Health Check | 8080 | http://localhost:8080/actuator/health | App health status |

---

## 🔌 API Endpoints

### User Management

**Create User**
```bash
POST http://localhost:8080/api/user/create
Content-Type: application/json

{
  "username": "player1",
  "balance": 1000
}
```

**Login User**
```bash
GET http://localhost:8080/api/user/login/{username}
```

**Get User**
```bash
GET http://localhost:8080/api/user/{userId}
```

### Race Management

**Create Race**
```bash
POST http://localhost:8080/api/race/create
```

**Get Race Details**
```bash
GET http://localhost:8080/api/race/{raceId}
```

**Join Race**
```bash
POST http://localhost:8080/api/race/{raceId}/join
Content-Type: application/json

{
  "userId": 1
}
```

**Ready Up**
```bash
POST http://localhost:8080/api/race/{raceId}/ready?userId=1
```

**Unready**
```bash
POST http://localhost:8080/api/race/{raceId}/unready?userId=1
```

**Get Participants**
```bash
GET http://localhost:8080/api/race/{raceId}/participants
```

**Start Race**
```bash
POST http://localhost:8080/api/race/{raceId}/start
```

### Betting

**Place Bet**
```bash
POST http://localhost:8080/api/bet/place
Content-Type: application/json

{
  "userId": 1,
  "raceId": 1,
  "carId": 2,
  "amount": 100
}
```

### WebSocket Topics

**Race Updates**
```
/topic/race/{raceId}
```

Receives: Car positions, race status, winner

**Lobby Updates**
```
/topic/lobby/{raceId}
```

Receives: Participants, ready status, countdown

---

## 🚨 Troubleshooting

### Backend Won't Start

**Problem**: `Error: Unable to access jarfile target/carrace-1.0.0.jar`

**Solution**: Build the application first
```bash
mvn clean package -DskipTests
```

---

**Problem**: Backend starts but crashes with Kafka errors

**Solution**: Check Confluent Cloud configuration
1. Verify credentials in `src/main/resources/application-confluent.properties`
2. Make sure bootstrap server URL is correct
3. Verify API key and secret are valid
4. Check Confluent Cloud cluster is running

---

**Problem**: `Port 8080 already in use`

**Solution**: Kill the existing process
```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or for carrace specifically
pkill -f carrace-1.0.0.jar
```

---

### Frontend Won't Start

**Problem**: `npm: command not found`

**Solution**: Install Node.js
```bash
# macOS with Homebrew
brew install node

# Or download from: https://nodejs.org/
```

---

**Problem**: Dependencies won't install

**Solution**: Clear cache and reinstall
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

---

**Problem**: `Port 3000 already in use`

**Solution**: Kill the process or use different port
```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9

# Or run on different port
PORT=3001 npm run dev
```

---

### Connection Issues

**Problem**: Frontend shows "Failed to fetch" errors

**Solution**: 
1. Check backend is running: `curl http://localhost:8080/actuator/health`
2. If not running, start it: `./start.sh`
3. Check for CORS errors in browser console
4. Verify Vite proxy configuration in `frontend/vite.config.js`

---

**Problem**: WebSocket not connecting

**Solution**:
1. Open browser DevTools (F12) → Network → WS filter
2. Look for connection attempts to `/ws`
3. Check for errors in console
4. Verify backend WebSocket endpoint is accessible: `curl http://localhost:8080/ws/info`
5. Try refreshing the page

---

**Problem**: Players not syncing / ready status not updating

**Solution**:
1. Verify all players are in the same race (check race ID)
2. Check WebSocket connection is active (see above)
3. Look for errors in backend logs
4. Check Kafka Streams is running (backend logs should show "Kafka Streams started")

---

**Problem**: Balance not updating after race

**Solution**:
1. Check Kafka is connected (backend logs)
2. Verify payout events are being processed
3. Click on balance to manually refresh
4. Check backend logs for payout calculation messages

---

### Kafka/Confluent Cloud Issues

**Problem**: `Authentication failed`

**Solution**: Check API credentials
```properties
# In application-confluent.properties
confluent.cloud.api.key=YOUR_ACTUAL_KEY_HERE
confluent.cloud.api.secret=YOUR_ACTUAL_SECRET_HERE
```

---

**Problem**: `Could not connect to bootstrap server`

**Solution**: Verify bootstrap server URL
```properties
# Should look like this:
spring.kafka.bootstrap-servers=pkc-xxxxx.region.provider.confluent.cloud:9092
```

---

**Problem**: Topics not auto-creating

**Solution**: Create manually in Confluent Cloud
1. Go to your cluster → Topics
2. Create these topics:
   - `race-events`
   - `bet-events`
   - `payout-events`
   - `leaderboard-updates`

---

### Database Issues

**Problem**: H2 Console not accessible

**Solution**: Check application.properties has H2 console enabled
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

**Problem**: Data not persisting between restarts

**Solution**: This is expected! H2 is in-memory for development.
For production, configure a persistent database:
```properties
spring.datasource.url=jdbc:h2:file:./data/carrace
```

---

## 📚 Additional Documentation

- **[FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)** - Detailed frontend documentation
- **[CONFLUENT_SETUP_GUIDE.md](CONFLUENT_SETUP_GUIDE.md)** - Step-by-step Confluent Cloud setup
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Complete project overview
- **[api-examples.http](api-examples.http)** - API request examples
- **[spec/](spec/)** - Original specifications

---

## 🔐 H2 Database Console

Access the H2 console at: **http://localhost:8080/h2-console**

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

**Tables:**
- `USER` - User accounts and balances
- `RACE` - Race details
- `CAR` - Car information
- `BET` - Betting records

---

## 🎯 Sample Users (Auto-created)

The application creates these sample users on startup:

| Username | User ID | Starting Balance |
|----------|---------|------------------|
| alice    | 1       | $1000           |
| bob      | 2       | $1000           |
| charlie  | 3       | $1000           |

You can use these for testing, or create your own!

---

## ⚡ Quick Testing Commands

**Create and test a full race:**
```bash
# From carrace folder:
./test-race.sh
```

This script automatically:
1. Creates a race
2. Places bets for sample users
3. Starts the race
4. Shows you the race ID to watch

**Watch live updates:**
```bash
# Open the race viewer in browser:
open http://localhost:8080/race-viewer.html?raceId=1
```

---

## 🏗️ Build & Development

**Build without tests:**
```bash
mvn clean package -DskipTests
```

**Build with tests:**
```bash
mvn clean package
```

**Run tests only:**
```bash
mvn test
```

**Run in development mode:**
```bash
# Backend
mvn spring-boot:run

# Frontend
cd frontend
npm run dev
```

**Build frontend for production:**
```bash
cd frontend
npm run build
```

Outputs to: `frontend/dist/`

---

## 🚀 Production Deployment

### Backend

1. Build JAR:
```bash
mvn clean package -DskipTests
```

2. Run with production profile:
```bash
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent,prod
```

3. Set environment variables:
```bash
export CONFLUENT_API_KEY=your_key
export CONFLUENT_API_SECRET=your_secret
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-db
```

### Frontend

1. Build:
```bash
cd frontend
npm run build
```

2. Serve with any static server:
```bash
npx serve -s dist -l 3000
```

Or copy `dist/` contents to Spring Boot's `src/main/resources/static/`

---

## 🎨 Technology Stack

### Frontend
- React 18.2.0
- React Router 6.20.0
- Vite 5.0.0
- SockJS Client 1.6.1
- STOMP.js 2.3.3
- Axios 1.6.0

### Backend
- Spring Boot 3.2.4
- Java 17
- Spring Kafka
- Kafka Streams
- Spring WebSocket
- H2 Database
- Hibernate/JPA
- Lombok
- Jackson

### Infrastructure
- Apache Kafka (Confluent Cloud)
- Maven 3.6+
- npm/Node.js 16+

---

## 📝 License

This is a demonstration project for learning purposes.

---

## 🤝 Contributing

Feel free to fork and enhance! Some ideas:
- Add user profiles with avatars
- Multiple simultaneous races
- Chat between players
- Race history and statistics
- Leaderboards across all races
- Tournament mode
- Sound effects
- Mobile app version

---

## 📞 Support

For issues or questions:
1. Check this README
2. Review [FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)
3. Check [CONFLUENT_SETUP_GUIDE.md](CONFLUENT_SETUP_GUIDE.md)
4. Look at backend logs in terminal or `backend.log`
5. Check browser console (F12) for frontend errors

---

## ✅ Quick Reference Card

**From carrace folder:**

| Task | Command |
|------|---------|
| Start everything | `./start-fullstack.sh` |
| Start backend only | `./start.sh` |
| Start frontend only | `cd frontend && npm run dev` |
| Build backend | `mvn clean package -DskipTests` |
| Install frontend | `cd frontend && npm install` |
| Test race | `./test-race.sh` |
| Check backend health | `curl localhost:8080/actuator/health` |
| Stop backend | `pkill -f carrace-1.0.0.jar` |
| Stop frontend | Press `Ctrl+C` in terminal |
| View logs | `tail -f backend.log` |
| Open frontend | Open http://localhost:3000 |
| Open H2 console | Open http://localhost:8080/h2-console |

---

**Happy Racing! 🏎️💨**
