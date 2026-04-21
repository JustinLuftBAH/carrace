#!/bin/bash

# Car Race Application - Quick Start Script
# This script helps you build and run the application with Confluent Cloud

echo "🏁 Car Race Application - Quick Start"
echo "======================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Error: Java 17 or higher is required"
    echo "Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"
echo ""

# Load environment variables from .env file
if [ -f .env ]; then
    echo "📋 Loading environment variables from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Environment variables loaded"
else
    echo "⚠️  WARNING: .env file not found!"
    echo ""
    echo "Please follow these steps:"
    echo "1. Copy .env.example to .env:"
    echo "   cp .env.example .env"
    echo "2. Edit .env and fill in your Confluent Cloud credentials"
    echo "3. Get credentials from: https://confluent.cloud/"
    echo ""
    echo "Or read the detailed guide: CONFLUENT_SETUP_GUIDE.md"
    echo ""
    read -p "Have you created the .env file? (y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Please create .env file first, then run this script again."
        exit 1
    fi
    # Try loading again
    export $(cat .env | grep -v '^#' | xargs)
fi
echo ""

# Validate environment variables are set
if [ -z "$CONFLUENT_BOOTSTRAP_SERVERS" ] || [ -z "$CONFLUENT_API_KEY" ] || [ -z "$CONFLUENT_API_SECRET" ]; then
    echo "❌ Error: Missing required environment variables!"
    echo "Please check your .env file contains:"
    echo "  - CONFLUENT_BOOTSTRAP_SERVERS"
    echo "  - CONFLUENT_API_KEY"
    echo "  - CONFLUENT_API_SECRET"
    exit 1
fi

echo "✅ Confluent Cloud configuration validated"
echo ""

echo "📦 Building application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""
echo "🚀 Starting application with Confluent Cloud..."
echo "   Press Ctrl+C to stop"
echo ""
echo "Once started, you can:"
echo "  - View race viewer: http://localhost:8080/race-viewer.html"
echo "  - H2 Console: http://localhost:8080/h2-console"
echo "  - API docs in README.md"
echo ""
echo "Starting in 3 seconds..."
sleep 3

java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
