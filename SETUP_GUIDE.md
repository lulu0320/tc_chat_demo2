# Quick Setup Guide

## Step 1: Open in Android Studio

1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to this directory and select it
4. Wait for Gradle sync to complete

## Step 2: Add Launcher Icons

The app requires launcher icons. You have two options:

### Option A: Use Android Studio (Recommended)
1. In Android Studio, right-click on `app/src/main/res`
2. Select **New → Image Asset**
3. Choose "Launcher Icons (Adaptive and Legacy)"
4. Select your icon image or use the default Android icon
5. Click "Next" then "Finish"

### Option B: Use Default Android Icons
Alternatively, modify the AndroidManifest.xml to use default system icons temporarily:
```xml
android:icon="@android:drawable/ic_dialog_email"
android:roundIcon="@android:drawable/ic_dialog_email"
```

## Step 3: Get Tencent IM Credentials

### Create a Tencent Cloud Account
1. Go to https://cloud.tencent.com/
2. Sign up for a free account
3. Navigate to the IM (Instant Messaging) service

### Get Your SDK App ID
1. In the Tencent Cloud Console, go to **IM Console**
2. Create a new application or select an existing one
3. Copy your **SDKAppID** (it's a number like 1400000000)

### Generate UserSig for Testing
1. In the IM Console, go to your application
2. Find **Development Tools** or **Auxiliary Tools** section
3. Look for "UserSig Generator"
4. Enter a UserID (e.g., "user1")
5. Click generate and copy the UserSig
6. Repeat for a second user (e.g., "user2") to test chat between two users

**Important**: UserSig expires after a certain time. For production, generate UserSig on your server.

## Step 4: Run the App

### On Emulator
1. In Android Studio, click the AVD Manager icon
2. Create or start an Android Virtual Device
3. Click the "Run" button (green play icon)

### On Physical Device
1. Enable Developer Options on your Android phone
2. Enable USB Debugging
3. Connect your phone via USB
4. Click the "Run" button in Android Studio

## Step 5: Login and Test

1. Launch the app
2. Tap "Get Started"
3. Enter your credentials:
   - **SDK App ID**: Your SDKAppID from step 3
   - **User ID**: e.g., "user1"
   - **User Signature**: The UserSig you generated
4. Tap "Login"

## Step 6: Test Chatting

### Single Device Testing
1. After logging in, enter another User ID in the conversation list
2. Tap "Start Chat"
3. Type and send messages
4. Messages will be stored on the server

### Two Device Testing (Recommended)
1. Login with "user1" on Device 1
2. Login with "user2" on Device 2
3. On Device 1, start a chat with "user2"
4. Send a message from Device 1
5. You should see it appear on Device 2 in real-time

## Common Issues

### Gradle Sync Failed
- Make sure you have a stable internet connection
- Try File → Invalidate Caches / Restart
- Check that you're using a compatible JDK version

### Login Failed
- Verify your SDKAppID is correct (no spaces)
- Ensure UserSig is not expired
- Check that your device has internet connection
- Look at the Logcat in Android Studio for detailed error messages

### App Won't Build
- Make sure all dependencies are downloaded
- Try Build → Clean Project, then Build → Rebuild Project
- Check the mipmap folders have icons or use default system icons

## Next Steps

Once you have the app running:
- Explore the code to understand how the Tencent IM SDK works
- Modify the UI to match your design requirements
- Add more features like group chat, file sharing, etc.
- Implement server-side UserSig generation for production

## Need Help?

- Check the README.md for detailed documentation
- Visit Tencent Cloud IM documentation: https://www.tencentcloud.com/document/product/1047
- Review the code comments for implementation details
