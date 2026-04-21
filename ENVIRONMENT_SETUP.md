# Environment Setup Guide

## Quick Setup

1. **Copy the environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` and add your Confluent Cloud credentials:**
   - Get these from https://confluent.cloud/
   - See CONFLUENT_SETUP_GUIDE.md for detailed instructions

3. **Start the application:**
   ```bash
   ./start-fullstack.sh
   ```

## What's in .env?

The `.env` file contains sensitive credentials that should NEVER be committed to git:

- `CONFLUENT_BOOTSTRAP_SERVERS`: Your Kafka cluster endpoint
- `CONFLUENT_API_KEY`: Your API key (like a username)
- `CONFLUENT_API_SECRET`: Your API secret (like a password)

## Security Features

✅ **`.env` is git-ignored** - Your credentials won't be committed to git  
✅ **`.env.example` provided** - Template with placeholder values  
✅ **Data folder git-ignored** - Your local database won't be committed  
✅ **Log files git-ignored** - Backend logs stay local  

## Database Storage

The application now uses **persistent storage** in the `./data` folder:

- Database file: `./data/carracedb.mv.db`
- User accounts and race history persist between restarts
- Automatically created on first run

## New Features

### 🎉 Confetti Celebration
When you win a race, confetti automatically celebrates your victory!

### 💾 Persistent Storage
Your user account, balance, and history are now saved locally.

### 🔐 Environment Variables
Sensitive Confluent Cloud credentials are now in `.env` (git-ignored).

## Troubleshooting

### "Missing required environment variables"
Make sure you've created `.env` and filled in all values from `.env.example`.

### Backend won't start
1. Check that `.env` exists and has valid credentials
2. Check `backend.log` for error details
3. Verify Confluent Cloud credentials are correct

### Database errors
Delete the `./data` folder to reset the database (you'll lose saved data).

## Files You Can Safely Commit

✅ `.env.example` - Template with placeholders  
✅ `application-confluent.properties` - Now uses environment variables  
✅ All source code  
✅ Documentation files  

## Files You Should NEVER Commit

❌ `.env` - Contains your actual API keys  
❌ `data/` - Local database files  
❌ `backend.log` - Application logs  
❌ `target/` - Build artifacts  
