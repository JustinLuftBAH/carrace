# 🌐 Multiplayer Setup - Connect Multiple Players on Same WiFi

This guide explains how to allow other people on your WiFi network to connect to your Car Race app.

## 📋 Quick Steps

### 1. Find Your Computer's IP Address

On **macOS/Linux**:
```bash
ipconfig getifaddr en0
# or
ifconfig | grep "inet " | grep -v 127.0.0.1
```

On **Windows**:
```bash
ipconfig
# Look for "IPv4 Address" under your WiFi adapter
```

Your IP will look like: `192.168.1.100` or `10.0.0.50`

### 2. Start Frontend with Network Access

Instead of the default localhost-only mode, start the frontend with network access:

```bash
cd frontend
npm run dev -- --host
```

This will show you URLs like:
```
➜  Local:   http://localhost:3000/
➜  Network: http://192.168.1.100:3000/
```

### 3. Backend is Already Network Accessible

The Spring Boot backend (port 8080) is already accessible on your network by default.

### 4. Share the Network URL

Give other players the **Network URL** from step 2, for example:
```
http://192.168.1.100:3000
```

They can open this on their phone, tablet, or computer while connected to the same WiFi.

---

## 🚀 Full Stack Network Mode

To start everything with network access:

### Option 1: Use the Modified Startup Script

Edit `start-fullstack.sh` to start frontend with `--host` flag:

```bash
# Around line 69, change:
npm run dev

# To:
npm run dev -- --host
```

### Option 2: Manual Start

**Terminal 1 - Backend:**
```bash
./start.sh
# Backend automatically listens on all network interfaces
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev -- --host
```

### Option 3: Create a Network Startup Script

Create `start-network.sh`:

```bash
#!/bin/bash

echo "🌐 Starting Car Race in Network Mode"
echo "======================================"
echo ""

# Get local IP
LOCAL_IP=$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || echo "Unknown")

# Start backend
if [ ! -f "target/carrace-1.0.0.jar" ]; then
    echo "📦 Building backend..."
    mvn clean package -DskipTests
fi

echo "🚀 Starting backend..."
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent > backend.log 2>&1 &
BACKEND_PID=$!

echo "✅ Backend started (PID: $BACKEND_PID)"
sleep 5

# Start frontend with network access
echo "🎨 Starting frontend with network access..."
cd frontend
npm run dev -- --host &

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Application running in network mode!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Share this URL with other players:"
echo "http://$LOCAL_IP:3000"
echo ""
echo "Backend API: http://$LOCAL_IP:8080"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
```

Make it executable:
```bash
chmod +x start-network.sh
```

---

## 🎮 Testing with Multiple Devices

### Setup:
1. Start the app in network mode (use methods above)
2. Find your network URL (e.g., `http://192.168.1.100:3000`)

### On Each Device:
1. Connect to the same WiFi network
2. Open the network URL in a browser
3. Register with a different username
4. All players will join the same race!

### Example:
- **Player 1** (host computer): `http://192.168.1.100:3000` → Register as "Alice"
- **Player 2** (phone): `http://192.168.1.100:3000` → Register as "Bob"
- **Player 3** (tablet): `http://192.168.1.100:3000` → Register as "Charlie"

All three will see each other in the lobby, can place bets, ready up, and race together!

---

## 🔒 Firewall Configuration

### macOS
If other devices can't connect, you may need to allow incoming connections:

1. **System Preferences** → **Security & Privacy** → **Firewall**
2. Click **Firewall Options**
3. Make sure "Block all incoming connections" is **unchecked**
4. Or add specific rules for Java and Node

### Windows
1. **Windows Defender Firewall** → **Allow an app**
2. Add Java and Node.js to allowed apps
3. Or temporarily disable firewall for testing

### Linux
```bash
# Allow ports 3000 and 8080
sudo ufw allow 3000
sudo ufw allow 8080
```

---

## 🐛 Troubleshooting

### "Can't connect" from other devices

**Problem**: Other devices can't access the app

**Solutions**:
1. **Verify same WiFi**: All devices must be on the same network
2. **Check IP**: Use the correct IP address (not localhost or 127.0.0.1)
3. **Firewall**: Temporarily disable firewall on host computer
4. **Port forwarding**: Some routers have isolation - check router settings
5. **VPN**: Disable VPN on host computer (it can interfere)

### Backend connection issues

If frontend loads but can't connect to backend:

1. **Check backend is running**: `curl http://YOUR_IP:8080/api/user/create`
2. **Verify Vite proxy**: The proxy in `vite.config.js` should work automatically
3. **Check CORS**: Backend should allow all origins (it does by default)

### WebSocket not connecting

**Problem**: Race updates don't work

**Solution**: WebSocket connections use the same URL as the app, so if HTTP works, WebSocket should too. Check browser console for WebSocket errors.

---

## 📱 Mobile-Friendly

The React frontend is responsive and works great on phones and tablets! Players can:
- Register and login on mobile
- Place bets with touch controls
- See the race on smaller screens
- Everything scales automatically

---

## 🎯 Production Deployment

For hosting beyond your local network:

### Option 1: Cloud Deployment
- Deploy backend to: Heroku, AWS, Google Cloud, Azure
- Deploy frontend to: Vercel, Netlify, GitHub Pages
- Update frontend API URL to point to cloud backend

### Option 2: Ngrok (Quick Public Access)
```bash
# Expose backend
ngrok http 8080

# Expose frontend
ngrok http 3000
```

Share the ngrok URLs with anyone, anywhere!

### Option 3: Docker Compose
Create `docker-compose.yml` for easy deployment anywhere.

---

## 🎉 Summary

**For Local Multiplayer (Same WiFi):**
```bash
# Terminal 1: Start backend
./start.sh

# Terminal 2: Start frontend with network access
cd frontend
npm run dev -- --host

# Share the Network URL shown in terminal 2
```

**Players connect to**: `http://YOUR_LOCAL_IP:3000`

Everyone on the same WiFi can now race together! 🏎️💨
