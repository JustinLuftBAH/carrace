# Confluent Cloud Setup Guide

This is a detailed, step-by-step guide for setting up Confluent Cloud for the Car Race application.

## Why Confluent Cloud?

Confluent Cloud is a fully managed Kafka service. You don't need to:
- Install Kafka locally
- Manage servers
- Configure clusters
- Worry about scaling

It's perfect for development and production. The free tier gives you $400 in credits!

## Step-by-Step Setup

### 1. Create Account (5 minutes)

1. Go to https://confluent.cloud/
2. Click **"Start Free"** (top right)
3. Fill in:
   - Email address
   - Password
   - First name, Last name
   - Company (can be "Personal" or anything)
4. Check your email and verify your account
5. Log in to Confluent Cloud

**Checkpoint**: You should now see the Confluent Cloud dashboard with "Welcome to Confluent Cloud"

### 2. Create Environment (Optional, 2 minutes)

Environments help organize your clusters. A default one is already created.

1. In the left sidebar, click **"Environments"**
2. You'll see "default" environment
3. Click on **"default"** to enter it

You can create a new environment if you want:
- Click **"+ Add cloud environment"**
- Name it (e.g., "car-race-dev")
- Click **"Create"**

**Checkpoint**: You're now in an environment (either "default" or your custom one)

### 3. Create Kafka Cluster (10 minutes - includes provisioning time)

This is the core Kafka service.

1. Click **"+ Create cluster"** or **"Add cluster"**
2. Choose cluster type:
   - **Basic** (Recommended for development)
     - Cheapest option
     - Perfect for this project
     - Costs ~$0.11/hour when running
     - Free tier credits apply!
   - Skip Standard and Dedicated (more expensive)
3. Select cloud provider:
   - **AWS** (most common)
   - Or GCP/Azure if you prefer
4. Select region:
   - Choose one close to you
   - Example: `us-east-1` (N. Virginia)
   - Or `us-west-2` (Oregon)
   - Or `eu-west-1` (Ireland)
5. Give cluster a name:
   - Example: `carrace-cluster`
   - Or `my-dev-cluster`
6. Click **"Launch cluster"**
7. Wait 2-5 minutes for provisioning

**Checkpoint**: You should see your cluster with a green status indicator

### 4. Get Bootstrap Server (2 minutes)

This is the URL your application uses to connect to Kafka.

1. Make sure you're viewing your cluster (click on its name if not)
2. In the left sidebar, click **"Cluster Overview"** or **"Cluster settings"**
3. Look for **"Bootstrap server"**
   - It looks like: `pkc-xxxxx.us-east-1.aws.confluent.cloud:9092`
   - The `pkc-xxxxx` part is unique to your cluster
4. Click the **copy icon** next to it
5. Open `src/main/resources/application-confluent.properties`
6. Find this line:
   ```properties
   spring.kafka.bootstrap-servers=pkc-xxxxx.us-east-1.aws.confluent.cloud:9092
   ```
7. Replace with YOUR bootstrap server URL

**Checkpoint**: You've copied your bootstrap server URL to the config file

### 5. Create API Key and Secret (3 minutes)

API Keys are like username/password for your Kafka cluster.

1. In your cluster view, look in the left sidebar
2. Click **"API Keys"** (or **"Data integration"** → **"API Keys"**)
3. Click **"+ Create key"** or **"Add key"**
4. Choose scope:
   - **"Global access"** (Recommended - simplest)
   - This gives access to the whole cluster
   - For production, you'd use granular ACLs
5. Click **"Next"** or **"Continue"**
6. You'll see two values:

   ```
   API Key:     ABCDEFGH12345678
   API Secret:  xyzABC123randomChars456secretKey789
   ```

7. **⚠️ CRITICAL**: Copy BOTH values RIGHT NOW!
   - The secret is only shown ONCE
   - You cannot retrieve it later
   - Save them somewhere safe:
     - Password manager (recommended)
     - Notepad
     - Secure note app

8. Click **"Download and continue"** (optional, but recommended)
   - This saves them to a file
   - Store this file securely

9. Open `src/main/resources/application-confluent.properties`
10. Find these lines:
    ```properties
    confluent.cloud.api.key=YOUR_API_KEY_HERE
    confluent.cloud.api.secret=YOUR_API_SECRET_HERE
    ```
11. Replace with YOUR actual values:
    ```properties
    confluent.cloud.api.key=ABCDEFGH12345678
    confluent.cloud.api.secret=xyzABC123randomChars456secretKey789
    ```

**Checkpoint**: You've saved your API key and secret to the config file

### 6. Create Topics (Optional, 5 minutes)

Topics can be auto-created, but manual creation gives you more control.

1. In your cluster, click **"Topics"** in the left sidebar
2. Click **"+ Create topic"** or **"Add a topic"**
3. Create these 4 topics:

   **Topic 1: race-events**
   - Name: `race-events`
   - Partitions: 6 (default)
   - Retention: 7 days (default)
   - Click **"Create with defaults"**

   **Topic 2: bet-events**
   - Name: `bet-events`
   - Partitions: 6
   - Retention: 7 days
   - Click **"Create with defaults"**

   **Topic 3: payout-events**
   - Name: `payout-events`
   - Partitions: 6
   - Retention: 7 days
   - Click **"Create with defaults"**

   **Topic 4: leaderboard-updates**
   - Name: `leaderboard-updates`
   - Partitions: 6
   - Retention: 7 days
   - Click **"Create with defaults"**

**Note**: This step is OPTIONAL. The application will auto-create topics when it starts.

**Checkpoint**: You have 4 topics created (or will be auto-created)

### 7. Verify Configuration

Before running the app, verify your `application-confluent.properties`:

```properties
# Should look similar to this (with YOUR values):
spring.kafka.bootstrap-servers=pkc-abc123.us-east-1.aws.confluent.cloud:9092
confluent.cloud.api.key=YOUR_ACTUAL_KEY
confluent.cloud.api.secret=YOUR_ACTUAL_SECRET
```

Checklist:
- ✅ Bootstrap server URL is filled in (not the placeholder)
- ✅ API key is filled in (not YOUR_API_KEY_HERE)
- ✅ API secret is filled in (not YOUR_API_SECRET_HERE)
- ✅ No extra spaces before or after values
- ✅ Cluster is running (green status in Confluent Cloud console)

### 8. Run the Application

```bash
# Build the project
mvn clean package

# Run with Confluent Cloud profile
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
```

Or with environment variable:
```bash
export SPRING_PROFILES_ACTIVE=confluent
java -jar target/carrace-1.0.0.jar
```

Or with Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=confluent"
```

### 9. Verify It's Working

Look for these log messages:

```
✅ Started CarRaceApplication in X.XXX seconds
✅ Kafka producer created
✅ Kafka consumer subscribed to topics
✅ Created 3 sample users: alice, bob, charlie
```

Check Confluent Cloud Console:
1. Go to **Topics**
2. If you didn't create them manually, you should now see the 4 topics auto-created
3. Click on **"race-events"** topic
4. Go to **"Messages"** tab

Now test the app:
```bash
# Create a race
curl -X POST http://localhost:8080/race/create

# Start the race
curl -X POST http://localhost:8080/race/1/start
```

Back in Confluent Cloud:
1. Click **"Messages"** tab in the **race-events** topic
2. Click **"Play"** or **"Jump to offset"** → **"Latest"**
3. You should see RaceStartedEvent messages appearing!

**Checkpoint**: You see events in Confluent Cloud console = SUCCESS! 🎉

## Common Issues and Solutions

### Issue: "Authentication failed"
**Cause**: Wrong API key or secret

**Solution**:
1. Go to Confluent Cloud → API Keys
2. Verify your API key (starts with a letter, ~16 chars)
3. If wrong, delete the key and create a new one
4. Copy the NEW key and secret to your config file

### Issue: "Could not connect to broker"
**Cause**: Wrong bootstrap server URL or cluster is down

**Solution**:
1. Go to Confluent Cloud → Cluster Overview
2. Check cluster status (should be green/running)
3. Copy the bootstrap server URL again
4. Make sure it ends with `:9092`

### Issue: "Topic not found"
**Cause**: Topics haven't been created yet

**Solution**:
- Wait a few moments - they auto-create
- Or manually create them (Step 6 above)
- Check Confluent Cloud → Topics

### Issue: Topics not appearing in Confluent Cloud
**Cause**: App not running or configuration error

**Solution**:
1. Make sure app is actually running
2. Check for errors in application logs
3. Verify configuration (Step 7 above)

## Cost Management

### Free Tier
- $400 in credits
- Lasts several months with Basic cluster
- Perfect for learning and development

### Basic Cluster Costs (after free credits)
- ~$0.11/hour when running
- ~$80/month if running 24/7
- **TIP**: Delete cluster when not using it!

### How to Stop/Delete Cluster

**To save money when not using**:
1. Go to Confluent Cloud
2. Click on your cluster
3. Click **"Cluster settings"** (or gear icon)
4. Scroll down to **"Delete cluster"**
5. Confirm deletion
6. Your topics and data will be lost
7. Free to create a new cluster later!

**Note**: You cannot "pause" a cluster - only delete and recreate

## Security Best Practices

### ⚠️ NEVER Commit Credentials

The `.gitignore` file already includes:
```
application-confluent.properties
```

This prevents accidentally committing your credentials to Git.

### For Production

Use environment variables instead of hardcoded values:

```properties
# In application-confluent.properties
confluent.cloud.api.key=${CONFLUENT_API_KEY}
confluent.cloud.api.secret=${CONFLUENT_API_SECRET}
```

Then set environment variables:
```bash
export CONFLUENT_API_KEY=your-key
export CONFLUENT_API_SECRET=your-secret
java -jar target/carrace-1.0.0.jar --spring.profiles.active=confluent
```

## Next Steps

1. ✅ Set up Confluent Cloud
2. ✅ Run the application
3. Open the race viewer: http://localhost:8080/race-viewer.html
4. Create a race and watch it in real-time!
5. Check Kafka messages in Confluent Cloud console

## Resources

- Confluent Cloud Docs: https://docs.confluent.io/cloud/
- Confluent Cloud Console: https://confluent.cloud/
- Support: https://support.confluent.io/
- This app's README: `../README.md`

## Need Help?

1. Check logs for specific error messages
2. Verify all configuration values
3. Ensure cluster is running (green status)
4. Check Confluent Cloud status: https://status.confluent.cloud/
5. Review this guide from the beginning

Happy Racing! 🏁
