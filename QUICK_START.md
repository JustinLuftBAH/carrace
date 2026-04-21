# 🚀 Car Race - Quick Start Guide

**The fastest way to get your racing app running!**

## ✅ System Verified

Your system has:
- ✅ Java 17.0.18 (OpenJDK)
- ✅ Maven 3.9.14
- ✅ Backend source code ready
- ✅ Frontend React app ready
- ✅ Confluent Cloud credentials configured

**Missing:** Node.js (required for frontend)

---

## 📋 Prerequisites Check

Before starting, make sure you have:

```bash
# Check Java (✅ Already installed)
java -version
# Should show: openjdk version "17.x.x"

# Check Maven (✅ Already installed)
mvn -version
# Should show: Apache Maven 3.9.x

# Check Node.js (⚠️ Need to install)
node -version
# Should show: v16.x.x or higher

# Check npm (⚠️ Need to install)
npm -version
# Should show: 8.x.x or higher
```

### Install Node.js (if needed)

```bash
# macOS with Homebrew
brew install node

# Or download from: https://nodejs.org/
```

---

## 🎯 Three Ways to Start

### Option 1: Full Stack (Easiest) ⭐

**One command to start everything!**

```bash
# From the carrace folder:
./start-fullstack.sh
```

Then open: **http://localhost:3000**

---

### Option 2: Separate Terminals

**Terminal 1 - Backend:**
```bash
# From the carrace folder:
./start.sh
```

**Terminal 2 - Frontend:**
```bash
# From the carrace folder:
cd frontend
npm install     # First time only
npm run dev
```

Then open: **http://localhost:3000**

---

### Option 3: Manual (Step-by-Step)

**Step 1: Build Backend**
```bash
# From the carrace folder:
mvn clean package -DskipTests
```

**Step 2: Start Backend**
```bash
# From the carrace folder:
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
```

**Step 3: Install Frontend Dependencies**
```bash
# From the carrace folder:
cd frontend
npm install
```

**Step 4: Start Frontend**
```bash
# From the frontend folder:
npm run dev
```

Then open: **http://localhost:3000**

---

## 🎮 How to Use

1. **Register** - Create an account with username and starting balance
2. **Join Race** - Automatically placed in a race lobby
3. **Place Bet** - Select a car and bet amount
4. **Ready Up** - Click ready when bet is placed
5. **Race Starts** - When all players ready for 5 seconds
6. **Watch Race** - See cars race in real-time
7. **Win/Lose** - Balance updates automatically
8. **New Race** - Click button to play again

---

## 🔍 Verify It's Working

### Backend Check
```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### Frontend Check
- Open: http://localhost:3000
- Should see login page with "CAR RACE" title

### WebSocket Check
1. Open browser DevTools (F12)
2. Go to Network tab
3. Filter by "WS"
4. Should see connection to `/ws`

---

## 🛑 How to Stop

### Stop Frontend
```bash
# Press Ctrl+C in the terminal where frontend is running
```

### Stop Backend
```bash
pkill -f carrace-1.0.0.jar

# Or if started with ./start.sh or manually:
# Press Ctrl+C in the backend terminal
```

---

## 🆘 Common Issues

### "Unable to access jarfile"
**Fix:** Build the backend first:
```bash
mvn clean package -DskipTests
```

### "Port 8080 already in use"
**Fix:** Kill existing process:
```bash
lsof -ti:8080 | xargs kill -9
```

### "npm: command not found"
**Fix:** Install Node.js:
```bash
brew install node
```

### "Failed to fetch"
**Fix:** Make sure backend is running:
```bash
curl http://localhost:8080/actuator/health
```

### Kafka authentication errors
**Fix:** Check credentials in:
```bash
src/main/resources/application-confluent.properties
```

---

## 📁 Project Structure

```
carrace/
├── README.md               ← Full documentation
├── QUICK_START.md          ← This file!
├── start.sh                ← Backend startup script
├── start-fullstack.sh      ← Full stack startup script
├── pom.xml                 ← Maven config
├── src/                    ← Backend code
│   └── main/
│       ├── java/           ← Java source
│       └── resources/      ← Config files
└── frontend/               ← React app
    ├── package.json        ← npm dependencies
    ├── src/                ← React components
    └── vite.config.js      ← Vite config
```

---

## 🌐 URLs

| Service | URL |
|---------|-----|
| **Frontend** | http://localhost:3000 |
| **Backend API** | http://localhost:8080 |
| **Health Check** | http://localhost:8080/actuator/health |
| **H2 Console** | http://localhost:8080/h2-console |

---

## 📖 More Documentation

- **[README.md](README.md)** - Complete documentation with all details
- **[FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)** - Frontend-specific guide
- **[CONFLUENT_SETUP_GUIDE.md](CONFLUENT_SETUP_GUIDE.md)** - Confluent Cloud setup
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Project overview

---

## ✅ Checklist

Before you start:

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] Node.js 16+ installed (`node -version`)
- [ ] npm installed (`npm -version`)
- [ ] Confluent Cloud credentials configured
- [ ] Terminal open in `carrace` folder

Ready to go? Run: `./start-fullstack.sh`

---

**Need help?** See [README.md](README.md) for detailed troubleshooting!

**Happy Racing! 🏎️💨**
