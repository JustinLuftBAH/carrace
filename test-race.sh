#!/bin/bash

# Car Race Application - Test Script
# Creates a complete race scenario for testing

BASE_URL="http://localhost:8080"

echo "🏁 Car Race - Test Script"
echo "========================"
echo ""
echo "This script will:"
echo "1. Create 2 test users"
echo "2. Create a race"
echo "3. Place bets from both users"
echo "4. Start the race"
echo "5. Display race info"
echo ""

# Check if server is running
echo "Checking if application is running..."
if ! curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Application is not running!"
    echo "Please start the application first:"
    echo "  ./start.sh"
    echo "or"
    echo "  mvn spring-boot:run"
    exit 1
fi
echo "✅ Application is running"
echo ""

# Create User 1
echo "1️⃣  Creating user: Alice (balance: $1000)"
USER1=$(curl -s -X POST "$BASE_URL/user/create" \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_test","balance":1000}')
USER1_ID=$(echo $USER1 | grep -o '"userId":[0-9]*' | grep -o '[0-9]*')
echo "   Created: User ID $USER1_ID"
echo ""

# Create User 2
echo "2️⃣  Creating user: Bob (balance: $1000)"
USER2=$(curl -s -X POST "$BASE_URL/user/create" \
  -H "Content-Type: application/json" \
  -d '{"username":"bob_test","balance":1000}')
USER2_ID=$(echo $USER2 | grep -o '"userId":[0-9]*' | grep -o '[0-9]*')
echo "   Created: User ID $USER2_ID"
echo ""

# Create Race
echo "3️⃣  Creating a new race..."
RACE=$(curl -s -X POST "$BASE_URL/race/create")
RACE_ID=$(echo $RACE | grep -o '"raceId":[0-9]*' | grep -o '[0-9]*')
echo "   Created: Race ID $RACE_ID"
echo ""

# Get race details to see cars
echo "4️⃣  Getting race details..."
RACE_DETAILS=$(curl -s "$BASE_URL/race/$RACE_ID")
echo "   $RACE_DETAILS" | python3 -m json.tool 2>/dev/null || echo "$RACE_DETAILS"
echo ""

# Place bet for User 1 on Car 1
echo "5️⃣  Alice bets $100 on Car 1"
BET1=$(curl -s -X POST "$BASE_URL/bet/place" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER1_ID,\"raceId\":$RACE_ID,\"carId\":1,\"amount\":100}")
echo "   $BET1"
echo ""

# Place bet for User 2 on Car 2
echo "6️⃣  Bob bets $200 on Car 2"
BET2=$(curl -s -X POST "$BASE_URL/bet/place" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER2_ID,\"raceId\":$RACE_ID,\"carId\":2,\"amount\":200}")
echo "   $BET2"
echo ""

# Start the race
echo "7️⃣  Starting the race..."
curl -s -X POST "$BASE_URL/race/$RACE_ID/start"
echo "   ✅ Race started!"
echo ""

echo "🏁 Race is now running!"
echo ""
echo "Watch the race live at:"
echo "   http://localhost:8080/race-viewer.html"
echo ""
echo "Race ID: $RACE_ID"
echo ""
echo "Check race status with:"
echo "   curl $BASE_URL/race/$RACE_ID"
echo ""
echo "Check user balances after race finishes:"
echo "   Alice: curl $BASE_URL/user/$USER1_ID"
echo "   Bob:   curl $BASE_URL/user/$USER2_ID"
echo ""
echo "The race will finish in about 20-30 seconds when a car reaches position 1000"
echo ""
