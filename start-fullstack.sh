#!/bin/bash

# Car Race - Full Stack Startup Script
# Starts both backend and frontend servers

echo "🏁 Car Race - Full Stack Startup"
echo "=================================="
echo ""

# Load environment variables from .env file
if [ -f .env ]; then
    echo "📋 Loading environment variables from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Environment variables loaded"
    echo ""
else
    echo "⚠️  WARNING: .env file not found!"
    echo "Please copy .env.example to .env and configure your Confluent Cloud credentials"
    echo ""
fi

# Check if in correct directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found"
    echo "Please run this script from the carrace directory"
    exit 1
fi

# Check if backend is already running
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
    echo "⚠️  Backend already running on port 8080"
else
    echo "🚀 Starting backend server..."
    echo ""
    
    # Check if jar exists
    if [ ! -f "target/carrace-1.0.0.jar" ]; then
        echo "📦 Building backend..."
        mvn clean package -DskipTests
    fi
    
    # Start backend in background
    nohup java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent > backend.log 2>&1 &
    BACKEND_PID=$!
    echo "✅ Backend started (PID: $BACKEND_PID)"
    echo "   Log: backend.log"
    echo ""
    
    # Wait for backend to be ready
    echo "⏳ Waiting for backend to start..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ Backend is ready!"
            break
        fi
        sleep 1
        echo -n "."
    done
    echo ""
fi

# Check if frontend directory exists
if [ ! -d "frontend" ]; then
    echo "❌ Error: frontend directory not found"
    exit 1
fi

# Check if node_modules exists
if [ ! -d "frontend/node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    cd frontend
    npm install
    cd ..
    echo ""
fi

echo "🎨 Starting frontend server..."
cd frontend

# Start frontend (this will keep running in foreground)
npm run dev -- --host

# Note: Frontend will run in foreground, backend in background
# To stop: Ctrl+C (stops frontend), kill backend with: pkill -f carrace-1.0.0.jar
