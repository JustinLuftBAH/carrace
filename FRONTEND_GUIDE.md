# 🏁 Car Race - React Frontend Quick Start

## What You Have

A **modern, full-featured React frontend** with:

- ✅ Beautiful cyberpunk-themed UI
- ✅ User registration & login
- ✅ Interactive betting system
- ✅ Ready-up system with 5-second countdown
- ✅ Live race visualization
- ✅ Real-time player list
- ✅ Balance display & logout
- ✅ WebSocket real-time updates

## Quick Start (2 Steps!)

### 1. Start the Backend

```bash
# Make sure you configured Confluent Cloud first!
# See: src/main/resources/application-confluent.properties

./start.sh
```

### 2. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Then open: **http://localhost:3000**

## Or Start Everything Together

```bash
./start-fullstack.sh
```

This will:
1. Build & start the backend (port 8080)
2. Install frontend dependencies
3. Start the frontend dev server (port 3000)

## How to Use

### 1. Register an Account
- Open http://localhost:3000
- Click "Register here"
- Choose username and starting balance (default: $1000)
- Click "Create Account"

### 2. Join a Race
- You'll automatically be placed in a race lobby
- See available cars with different colors
- Your balance is shown in the top right

### 3. Place a Bet
- Click on a car to select it
- Choose your bet amount (use quick buttons or type custom)
- See potential win amount (2x your bet)
- Click "Place Bet"
- Your balance updates immediately

### 4. Ready Up
- After placing a bet, click "READY UP" button
- Your status changes to "✓ Ready" in the player list
- Watch other players in the right sidebar

### 5. Race Starts
- When ALL players are ready for 5 seconds:
  - Big countdown appears (5, 4, 3, 2, 1)
  - Race automatically starts!
- Watch cars race in real-time with progress bars
- Live leaderboard updates every 200ms

### 6. Race Finishes
- First car to 1000m wins
- Winner highlighted in gold with trophy 🏆
- Your balance updates automatically
- Click "New Race" to play again

## Features in Detail

### 🎨 Modern UI
- Cyberpunk neon green theme
- Smooth animations
- Glassmorphic elements
- Responsive design

### 👥 Player List
- Shows all participants
- Ready status indicators
- Bet information
- Highlights your user

### 💰 Balance Management
- Real-time updates
- Click to manually refresh
- Persistent across pages
- Deducted on bet, paid on win

### 🎲 Betting Panel
- Visual car selection
- Quick bet buttons ($50, $100, $250, $500)
- Custom amount input
- Shows potential win
- Can't bet more than balance

### 🏁 Race Track
- Visual progress bars
- Car emojis that bounce
- Real-time position updates
- Speed display
- Finish line indicator

### ⏱️ Ready Timer
- Full-screen countdown overlay
- Pulsing animation
- Shows when all players ready
- 5-second delay before start

### 🔄 Real-time Sync
- WebSocket connections
- Instant updates for all players
- Synchronized race state
- No refresh needed

## Multiple Players

To test with multiple players:

1. Open http://localhost:3000 in multiple browser windows/tabs
2. Register different users in each
3. All will join the same race
4. Place bets in each window
5. Click ready in each
6. Race starts when all ready!

## Testing Alone

You can also test alone:
1. Register and login
2. Place a bet
3. Click ready
4. After 5 seconds, race auto-starts
5. Watch your bet outcome

## Keyboard Shortcuts

None currently, but you could add:
- `R` to ready/unready
- `1-4` to select cars
- `Enter` to place bet

## Ports

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **H2 Console**: http://localhost:8080/h2-console

## Troubleshooting

### "Failed to fetch" errors
**Problem**: Frontend can't reach backend

**Solution**:
```bash
# Check if backend is running
curl http://localhost:8080/actuator/health

# If not, start it
./start.sh
```

### WebSocket not connecting
**Problem**: Real-time updates not working

**Solution**:
- Check browser console for errors
- Verify backend is running
- Try refreshing the page
- Check firewall settings

### Players not syncing
**Problem**: Ready status not updating

**Solution**:
- Open browser dev tools (F12)
- Check Network tab for WebSocket connection
- Look for /ws/info or /ws connections
- Verify no WebSocket errors

### Balance not updating
**Problem**: Credits don't change after race

**Solution**:
- Click on the balance display to manually refresh
- Check backend logs for payout processing
- Verify Kafka is configured correctly

### Race won't start
**Problem**: All ready but countdown doesn't begin

**Solution**:
- Check that ALL players (in player list) show "✓ Ready"
- Look in backend logs for countdown messages
- Verify race status is still "CREATED"

## File Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Login.jsx          # Login page
│   │   ├── Register.jsx       # Sign up page  
│   │   ├── RaceLobby.jsx      # Main game screen
│   │   ├── Header.jsx         # Top bar with balance/logout
│   │   ├── BettingPanel.jsx   # Bet placement UI
│   │   ├── RaceTrack.jsx      # Visual race display
│   │   ├── PlayerList.jsx     # Right sidebar with users
│   │   ├── ReadyTimer.jsx     # Countdown overlay
│   │   └── *.css             # Component styles
│   ├── services/
│   │   ├── api.js            # REST API calls
│   │   └── websocket.js      # WebSocket connection
│   ├── App.jsx               # Router & auth
│   └── main.jsx              # React entry
├── index.html
├── package.json
└── vite.config.js
```

## Environment Variables

You can customize the API URL:

```bash
# In frontend/.env
VITE_API_URL=http://localhost:8080
```

## Production Build

```bash
cd frontend
npm run build

# Serve with any static server
npx serve -s dist
```

Or copy `dist/` contents to Spring Boot's `static/` folder to serve from backend.

## Next Steps

1. ✅ Configure Confluent Cloud (if not done yet)
2. ✅ Start backend: `./start.sh`
3. ✅ Start frontend: `cd frontend && npm run dev`
4. ✅ Open http://localhost:3000
5. ✅ Register/login
6. ✅ Place bet & ready up
7. ✅ Watch the race!

## Need Help?

- **Frontend issues**: Check `frontend/README.md`
- **Backend issues**: Check `README.md`
- **Confluent setup**: Check `CONFLUENT_SETUP_GUIDE.md`
- **API docs**: Check `api-examples.http`

Enjoy racing! 🏎️💨
