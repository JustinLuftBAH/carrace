#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Kill any existing backend
ps aux | grep "[j]ava.*carrace" | awk '{print $2}' | xargs -r kill
sleep 2

# Clean database
rm -f data/*.db

# Start the application with confluent profile
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent > backend.log 2>&1 &

echo "Starting backend with Confluent Cloud..."
echo "Process ID: $!"
echo "Waiting for startup..."

# Wait and check if started
sleep 45
if grep -q "Started CarRaceApplication" backend.log; then
    echo "✓ Backend started successfully and connected to Confluent Cloud!"
    grep "Started CarRaceApplication" backend.log
else
    echo "✗ Backend failed to start. Check backend.log for errors:"
    tail -30 backend.log
fi
