# Chat Demo - Android Chat Application

A simple Android chat application built with Kotlin and the Tencent IM SDK, featuring one-on-one messaging.

## Features

- User authentication with Tencent IM SDK
- One-on-one text messaging
- Real-time message delivery
- Conversation list with message previews
- Message history loading
- Clean Material Design UI

## Prerequisites

Before running this application, you need:

1. **Tencent Cloud Account**: Sign up at [Tencent Cloud](https://cloud.tencent.com/)
2. **IM SDK App**: Create an IM application in the Tencent Cloud Console
3. **SDK App ID**: Obtain your SDKAppID from the console
4. **UserSig**: Generate UserSig for test users (can be generated in the console for testing)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/chatdemo/
│   │   ├── MainActivity.kt           # Entry point
│   │   ├── LoginActivity.kt          # User login
│   │   ├── ConversationListActivity.kt  # Conversation list
│   │   ├── ChatActivity.kt           # Chat interface
│   │   └── adapters/
│   │       ├── ConversationAdapter.kt
│   │       └── MessageAdapter.kt
│   ├── res/
│   │   ├── layout/                   # XML layouts
│   │   ├── values/                   # Strings, colors
│   │   └── menu/                     # Menu resources
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Setup Instructions

### 1. Add Launcher Icons

The project references launcher icons that need to be added. You can:
- Use Android Studio's Image Asset tool to generate icons
- Or manually add `ic_launcher.png` and `ic_launcher_round.png` to the mipmap folders

### 2. Get Tencent IM Credentials

1. Go to [Tencent Cloud IM Console](https://console.cloud.tencent.com/im)
2. Create a new IM application or use an existing one
3. Note your **SDKAppID**
4. Generate **UserSig** for test users using the console's development tools

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Connect an Android device or start an emulator
4. Run the app

### 4. Login

1. Tap "Get Started" on the main screen
2. Enter your **SDK App ID**
3. Enter a **User ID** (any string, e.g., "user1")
4. Enter the **UserSig** generated for that user
5. Tap "Login"

### 5. Start Chatting

1. After login, you'll see the conversation list
2. Enter a target User ID (must be a valid user that exists)
3. Tap "Start Chat"
4. Send and receive messages in real-time

## Important Notes

### UserSig Generation

- **For Testing**: UserSig can be generated in the Tencent Cloud Console under "Development Tools"
- **For Production**: UserSig MUST be generated on your server for security. Never expose your secret key in client apps.

### Testing with Multiple Users

To test the chat functionality:
1. Generate UserSig for two different users (e.g., "user1" and "user2")
2. Login with "user1" on one device/emulator
3. Login with "user2" on another device/emulator
4. Start a conversation from one user to the other
5. Send messages and see them appear in real-time

## Dependencies

- **Tencent IM SDK**: 7.6.5022
- **AndroidX Core**: 1.12.0
- **Material Design**: 1.11.0
- **RecyclerView**: 1.3.2
- **Kotlin Coroutines**: 1.7.3

## Minimum Requirements

- **Android SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34
- **Kotlin**: 1.9.0

## Troubleshooting

### Login Failed
- Verify your SDKAppID is correct
- Ensure UserSig is generated correctly and not expired
- Check your internet connection

### Messages Not Sending
- Verify both users are logged in
- Check that the target User ID exists in your IM app
- Ensure you have proper permissions in AndroidManifest.xml

### Build Errors
- Sync Gradle files
- Clean and rebuild the project
- Check that all dependencies are downloaded

## License

This is a demo application for educational purposes.

## Resources

- [Tencent Cloud IM Documentation](https://www.tencentcloud.com/document/product/1047)
- [Tencent IM SDK GitHub](https://github.com/tencentyun/TIMSDK)
