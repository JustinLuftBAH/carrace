# Car Race Frontend

Modern React frontend for the Car Race multiplayer racing and betting application.

## Features

- 🎨 Modern, cyberpunk-inspired UI design
- 🔐 User registration and login
- 💰 Real-time balance display
- 🎰 Interactive betting system
- ✅ Ready-up system with countdown
- 🏁 Live race visualization with animated cars
- 👥 Player list showing ready status
- 🔌 WebSocket integration for real-time updates
- 📱 Responsive design

## Prerequisites

- Node.js 16+ and npm
- Backend server running on http://localhost:8080

## Installation

```bash
cd frontend
npm install
```

## Development

Start the development server:

```bash
npm run dev
```

The app will be available at http://localhost:3000

## Build for Production

```bash
npm run build
```

The build output will be in the `dist/` directory.

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Login.jsx              # Login page
│   │   ├── Register.jsx           # Registration page
│   │   ├── RaceLobby.jsx          # Main race lobby
│   │   ├── Header.jsx             # Top header with balance
│   │   ├── BettingPanel.jsx       # Bet placement UI
│   │   ├── RaceTrack.jsx          # Visual race display
│   │   ├── PlayerList.jsx         # List of participants
│   │   ├── ReadyTimer.jsx         # Countdown overlay
│   │   └── *.css                  # Component styles
│   ├── services/
│   │   ├── api.js                 # REST API service
│   │   └── websocket.js           # WebSocket service
│   ├── App.jsx                    # Main app component
│   ├── main.jsx                   # Entry point
│   └── index.css                  # Global styles
├── index.html
├── vite.config.js
└── package.json
```

## How It Works

### 1. Registration/Login
- Users register with a username and starting balance
- Or login with existing username
- Session stored in localStorage

### 2. Race Lobby
- Automatically creates/joins a race
- Displays available cars with colors
- Shows current balance in top right

### 3. Betting
- Select a car from the grid
- Choose bet amount (quick buttons or custom)
- Potential win displayed (2x bet)
- Balance updated immediately

### 4. Ready System
- After betting, click "READY UP"
- Player list shows ready status for all users
- When all players ready for 5 seconds, race auto-starts
- Countdown overlay displayed

### 5. Race
- Cars move forward in real-time
- Progress bars show position
- Leaderboard updates live
- Race finishes when car reaches 1000m

### 6. Results
- Winner highlighted in gold
- Balance automatically updated
- "New Race" button to start over

## API Integration

The frontend communicates with the backend via:

### REST API
- `POST /api/user/create` - Register new user
- `GET /api/user/login/:username` - Login
- `GET /api/user/:id` - Get user details
- `POST /api/race/create` - Create race
- `GET /api/race/:id` - Get race details
- `POST /api/race/:id/join` - Join race
- `POST /api/race/:id/ready` - Mark ready
- `POST /api/race/:id/unready` - Unmark ready
- `GET /api/race/:id/participants` - Get participants
- `POST /api/bet/place` - Place bet

### WebSocket
- `/topic/race/:id` - Race updates (positions, status)
- `/topic/lobby/:id` - Lobby updates (participants, ready status)

## Configuration

The frontend is configured to proxy API requests to the backend:

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
  '/ws': {
    target: 'http://localhost:8080',
    ws: true,
  }
}
```

## Styling

The app uses CSS custom properties for theming:

- Primary color: `#00ff88` (neon green)
- Background: `#0a0e27` (dark blue)
- Surface: `#1a1f3a` (lighter blue)
- Fonts: Orbitron (headings), Roboto (body)

## Technologies

- **React 18** - UI framework
- **React Router 6** - Routing
- **Vite** - Build tool
- **SockJS** - WebSocket client
- **STOMP** - WebSocket protocol
- **Axios** - HTTP client

## Troubleshooting

### Backend connection issues
- Ensure backend is running on port 8080
- Check browser console for errors
- Verify proxy configuration in vite.config.js

### WebSocket not connecting
- Check if backend WebSocket endpoint is accessible
- Ensure CORS is properly configured on backend
- Look for errors in browser console

### Users not syncing
- Verify participants endpoint is working
- Check WebSocket subscription in RaceLobby.jsx
- Ensure lobby broadcasts are working

## Development Tips

### Hot Reload
Vite provides instant hot reload - changes appear immediately without page refresh.

### Component Development
Each component is self-contained with its own CSS file for easy modification.

### State Management
Uses React hooks (useState, useEffect, useCallback) - no external state library needed.

### WebSocket Reconnection
The WebSocket service auto-reconnects on page load. For production, consider adding auto-reconnect on disconnect.

## Future Enhancements

- Add user profiles and avatars
- Multiple races running simultaneously
- Race history and statistics
- Chat system between players
- Sound effects and music
- Mobile app version
- Tournament mode

## License

Part of the Car Race application. See main project README for details.
