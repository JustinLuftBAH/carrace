# Car Race Testing Guide

## Current Status

### ✅ Backend (Running on http://localhost:8080)
- User API with customization fields: `/api/user/{id}`
- Shop API with 9 items: `/api/shop?userId={id}`
- Race API: `/api/race/*`
- Betting API: `/api/bet/*`
- Reaction API: `/api/reactions/send`
- WebSocket: `ws://localhost:8080/ws`

### ✅ Frontend (Running on http://localhost:3001)
- React app with Vite
- WebSocket integration via SockJS + Stomp
- Shop, PlayerList, RaceTrack components

## Issues to Verify

### 1. Multiple Players in Player List
**Expected**: When 2+ users join the same race, all should appear in the player list
**How to Test**:
1. Open http://localhost:3001 in Browser Window 1
2. Register/login as "testuser1"
3. Open http://localhost:3001 in Incognito/Browser Window 2  
4. Register/login as "testuser2"
5. Both users should see each other in the right sidebar

**Potential Issue**: Race IDs might be different for each user

### 2. Shop Loading
**Expected**: Shop should display 9 items (3 per category)
**How to Test**:
1. Login as any user
2. Click "🛒 Shop" button in header
3. Should see 3 profile pictures, 3 colors, 3 nametags

**Potential Issue**: Browser cache or API path mismatch

### 3. Emoji Reactions
**Expected**: Click 😊 → select emoji → appears next to your name for 3 seconds
**How to Test**:
1. In player list, click the 😊 button (only visible next to your own name)
2. Click an emoji (👍 🔥 or 😂)
3. Emoji should appear next to your name in the "Status" column
4. Should disappear after 3 seconds

**Potential Issue**: WebSocket not broadcasting reaction updates to lobby channel

## API Endpoints Verification

```bash
# Test User API
curl http://localhost:8080/api/user/1

# Test Shop API  
curl "http://localhost:8080/api/shop?userId=1"

# Test Reaction API
curl -X POST "http://localhost:8080/api/reactions/send?userId=1&raceId=1&emoji=%F0%9F%94%A5"
```

## Database

- Location: `./data/carracedb.mv.db`
- Type: H2 file-based database
- Users: alice (ID:1), bob (ID:2), charlie (ID:3)
- Shop Items: 9 items total

## Debugging Tips

1. **Browser Console**: Open DevTools → Console to see errors
2. **Network Tab**: Check if API calls are successful (200 status)
3. **WebSocket**: Look for "WebSocket connected" message in console
4. **Backend Logs**: `tail -f backend.log` to see server-side issues
5. **Clear Cache**: Hard refresh (Cmd+Shift+R on Mac, Ctrl+Shift+R on Windows)

## Known Working Features

- ✅ User registration and login
- ✅ Race creation
- ✅ Betting system
- ✅ Car position updates during race
- ✅ Winner announcement with confetti
- ✅ Persistent database
- ✅ Shop item storage (9 items)
- ✅ User customizations in database
