# Messaging SDK - Build Instructions

## 📦 Building the Library from Command Line

### **Build everything (includes tests):**
```bash
./gradlew :messaging-sdk:build
```

### **Build without tests (faster):**
```bash
./gradlew :messaging-sdk:assemble
```

### **Build specific variants:**
```bash
# Debug variant
./gradlew :messaging-sdk:assembleDebug

# Release variant
./gradlew :messaging-sdk:assembleRelease
```

### **Clean and rebuild:**
```bash
./gradlew :messaging-sdk:clean :messaging-sdk:build
```

## 📂 Output Location

After building, the library AAR files will be in:
```
messaging-sdk/build/outputs/aar/
├── messaging-sdk-debug.aar
└── messaging-sdk-release.aar
```

## 🔍 Other Useful Commands

**List all available tasks for the library:**
```bash
./gradlew :messaging-sdk:tasks
```

**Run tests only:**
```bash
./gradlew :messaging-sdk:test
```

**Generate documentation:**
```bash
./gradlew :messaging-sdk:dokkaHtml  # (requires Dokka plugin)
```

**Check dependencies:**
```bash
./gradlew :messaging-sdk:dependencies
```

**Publish to local Maven repository:**
```bash
./gradlew :messaging-sdk:publishToMavenLocal
```

## 📝 Quick Start

```bash
# Navigate to project root
cd /Users/uncbrooke00/workspace/chat_demo

# Build the library
./gradlew :messaging-sdk:assembleRelease

# Check the output
ls -lh messaging-sdk/build/outputs/aar/
```

## 💡 Tips

- The `:messaging-sdk` prefix tells Gradle to run the task only for that specific module
- Use `assembleRelease` for production builds (optimized, no debug info)
- Use `assembleDebug` for development builds (includes debug symbols)
- Add `-x test` to skip tests: `./gradlew :messaging-sdk:build -x test`
- Add `--info` for verbose output: `./gradlew :messaging-sdk:build --info`
- Add `--stacktrace` for debugging build errors

## 🚀 Publishing

To use this library in other projects, you can:

1. **Copy the AAR file** to another project's `libs/` folder
2. **Publish to local Maven** and reference it
3. **Publish to a remote repository** (Maven Central, JitPack, etc.)

### Local Maven Example:
```bash
# Publish to local Maven (~/.m2/repository/)
./gradlew :messaging-sdk:publishToMavenLocal

# Then in another project's build.gradle.kts:
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.example:messaging-sdk:1.0.0")
}
```
