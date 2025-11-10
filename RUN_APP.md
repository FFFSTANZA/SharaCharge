# Running SharaCharge Without Building

This guide shows you how to run the SharaCharge app with minimal or no building required.

## Option 1: Install Pre-Built APK (No Build Needed)

If you already have a built APK from a previous build:

### On Physical Device

```bash
# Find existing APKs
find app/build/outputs/apk -name "*.apk"

# Install debug APK
adb install app/build/outputs/apk/debug/SharaSpot-test-*.apk

# Install release APK
adb install app/build/outputs/apk/release/SharaSpot-prod-*.apk

# If app is already installed, use -r to reinstall
adb install -r app/build/outputs/apk/debug/SharaSpot-test-*.apk
```

### On Emulator

```bash
# List running emulators
adb devices

# Install to specific emulator
adb -s emulator-5554 install app/build/outputs/apk/debug/SharaSpot-test-*.apk

# Or drag and drop the APK file onto the emulator window
```

---

## Option 2: Incremental Build + Install (Fastest)

Instead of `clean build`, use Gradle tasks that only build what changed:

### Quick Install to Device

```bash
# Install debug build directly to connected device
./gradlew installDebug

# This does:
# 1. Incremental build (only rebuilds what changed)
# 2. Automatic installation to device/emulator
# 3. No need for manual adb commands
```

### Build + Install + Run

```bash
# Install and launch the app immediately
./gradlew installDebug && adb shell am start -n com.esttp.SharaSpot/.MainActivity

# Or create an alias for quick runs
alias run-app="./gradlew installDebug && adb shell am start -n com.esttp.SharaSpot/.MainActivity"
```

**Performance:** Incremental builds typically take 5-30 seconds vs 2-5 minutes for clean builds.

---

## Option 3: Android Studio (Recommended for Development)

Android Studio provides the fastest development workflow:

### Setup

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select a device/emulator from the dropdown
4. Click the "Run" ▶️ button (or press Shift+F10)

### Benefits

- **Instant Run / Apply Changes**: Apply code changes without reinstalling
- **Hot Reload**: UI changes apply instantly
- **Live Edit**: See changes as you type (for Compose)
- **Debugging**: Breakpoints, variable inspection, step-through
- **Logcat**: Real-time log viewing

### Quick Actions

| Action | Shortcut | What it does |
|--------|----------|--------------|
| Run | Shift+F10 | Build + Install + Launch |
| Apply Changes | Ctrl+F10 | Update code without reinstall |
| Apply Code Changes | Ctrl+Alt+F10 | Update only code changes |
| Debug | Shift+F9 | Run with debugger attached |

---

## Option 4: Use Docker with Pre-Built Image

If you've already built the Docker image, you can skip the rebuild:

```bash
# Extract APK from previously built image
docker create --name temp sharacharge-build
docker cp temp:/workspace/app/build/outputs/apk/ ./apk-output/
docker rm temp

# Install the APK
adb install apk-output/debug/*.apk
```

---

## Option 5: Download from CI/CD (If Available)

If your project has GitHub Actions or CI/CD:

1. Go to GitHub Actions tab
2. Find the latest successful workflow run
3. Download the APK artifact
4. Install using `adb install <apk-file>`

---

## Quick Reference: Gradle Install Tasks

| Task | Description | Use When |
|------|-------------|----------|
| `installDebug` | Install debug variant | Development/testing |
| `installRelease` | Install release variant | Pre-production testing |
| `installPreRelease` | Install preRelease variant | Production testing with debug |
| `installBenchmark` | Install benchmark variant | Performance testing |
| `uninstallDebug` | Remove debug variant | Clean slate needed |
| `uninstallAll` | Remove all variants | Reset completely |

---

## Fast Development Workflow

### Initial Setup (One-Time)

```bash
# Build debug APK once
./gradlew assembleDebug

# Install to device
./gradlew installDebug
```

### Daily Development

```bash
# Make code changes...

# Quick incremental build + install (5-30 seconds)
./gradlew installDebug

# Launch app
adb shell am start -n com.esttp.SharaSpot/.MainActivity

# Or combine both
./gradlew installDebug && adb shell am start -n com.esttp.SharaSpot/.MainActivity
```

### Create Convenience Script

Create `run.sh`:

```bash
#!/bin/bash
set -e

echo "📱 Installing SharaSpot..."
./gradlew installDebug

echo "🚀 Launching app..."
adb shell am start -n com.esttp.SharaSpot/.MainActivity

echo "✅ App is running!"
echo "📊 View logs: adb logcat | grep SharaSpot"
```

Make it executable:

```bash
chmod +x run.sh
./run.sh
```

---

## Troubleshooting

### "No devices found"

```bash
# Check connected devices
adb devices

# Start emulator
emulator -avd Pixel_4_API_30

# Or restart adb
adb kill-server
adb start-server
```

### "App not installed" Error

```bash
# Uninstall old version first
adb uninstall com.esttp.SharaSpot

# Then install new version
./gradlew installDebug
```

### "Installation failed: INSTALL_FAILED_UPDATE_INCOMPATIBLE"

The signing key changed. Uninstall first:

```bash
adb uninstall com.esttp.SharaSpot
./gradlew installDebug
```

### Check What's Installed

```bash
# List installed packages
adb shell pm list packages | grep sharaspot

# Get app info
adb shell dumpsys package com.esttp.SharaSpot
```

---

## Viewing Logs While Running

```bash
# View all logs
adb logcat

# Filter by app package
adb logcat | grep "com.esttp.SharaSpot"

# Filter by tag
adb logcat -s "SharaSpot"

# Clear old logs first
adb logcat -c && adb logcat
```

---

## Performance Comparison

| Method | Time | When to Use |
|--------|------|-------------|
| **Pre-built APK Install** | 5-10 sec | Testing existing build |
| **Incremental Build** | 5-30 sec | After code changes |
| **Android Studio Run** | 10-40 sec | Development with IDE |
| **Docker + Volume** | 30-60 sec | Incremental in Docker |
| **Clean Build** | 2-5 min | Major changes/conflicts |
| **Docker Clean Build** | 3-10 min | CI/CD or reproducible builds |

---

## Best Practices

1. **Never run `clean` unless necessary** - it forces a complete rebuild
2. **Use `installDebug` instead of `assembleDebug` + `adb install`** - it's faster
3. **Keep Android Studio open** - it maintains Gradle daemon and caches
4. **Use emulators with hardware acceleration** - much faster than without
5. **Enable Gradle daemon** - already configured in this project
6. **Use incremental builds** - default behavior when you don't clean

---

## Summary

**To run without building (fastest):**
```bash
adb install app/build/outputs/apk/debug/SharaSpot-test-*.apk
```

**To run with minimal build (recommended):**
```bash
./gradlew installDebug
```

**To run in Android Studio (best for development):**
- Click the Run ▶️ button

**First time? No existing APK?**
```bash
# Build once
./gradlew assembleDebug

# Then use installDebug for all future runs
./gradlew installDebug
```

You only need a full `clean build` when:
- Gradle cache is corrupted
- Major dependency changes
- Build issues that incremental builds don't fix
- CI/CD pipelines (for reproducibility)

For daily development, stick with `installDebug` for the fastest workflow! 🚀
