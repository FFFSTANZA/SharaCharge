# SharaCharge Build Guide

## Quick Start

### Prerequisites
- **JDK 21** (required)
- **Android SDK** with:
  - Platform 36 (compileSdk)
  - Platform 35 (targetSdk)
  - Build Tools 35.0.0 or higher
- **Git** (for cloning and version control)

### Build Commands

```bash
# 1. Clean build
./gradlew clean build

# 2. Build debug APK
./gradlew assembleDebug

# 3. Build release APK
./gradlew assembleRelease

# 4. Run tests
./gradlew test

# 5. Run lint checks
./gradlew lint

# 6. Build benchmark APK
./gradlew assembleBenchmark
```

## Using Docker (Recommended for Reproducibility)

### One-time Setup
```bash
# Build the Docker image
docker build -t sharacharge-build .
```

### Build Commands
```bash
# Full build
docker run --rm -v $(pwd):/workspace sharacharge-build

# Debug APK only
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew assembleDebug

# Interactive shell
docker run --rm -it -v $(pwd):/workspace sharacharge-build /bin/bash
```

### Windows (PowerShell)
```powershell
# Build
docker run --rm -v ${PWD}:/workspace sharacharge-build

# Interactive
docker run --rm -it -v ${PWD}:/workspace sharacharge-build /bin/bash
```

## Version Information

| Component | Version |
|-----------|---------|
| Gradle | 8.11.1 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.1.0 |
| KSP | 2.1.0-1.0.29 |
| Compose BOM | 2024.12.01 |
| Room | 2.6.1 |
| JDK | 21 |
| compileSdk | 36 |
| targetSdk | 35 |
| minSdk | 24 |

For detailed version information and compatibility matrix, see [VERSION_ALIGNMENT.md](VERSION_ALIGNMENT.md)

## Build Variants

### Available Flavors
- **gms**: Google Mobile Services (includes Firebase, Google Maps)
- **foss**: Free and Open Source Software (FOSS) build without GMS

### Build Types
- **debug**: Debuggable, unminified, uses debug signing
- **release**: Minified, obfuscated, optimized for production
- **preRelease**: Debuggable but uses production configuration
- **benchmark**: Optimized for performance testing

### Build Variant Examples
```bash
# GMS Debug
./gradlew assembleGmsDebug

# FOSS Release
./gradlew assembleFossRelease

# GMS Pre-release
./gradlew assembleGmsPreRelease

# All variants
./gradlew assemble
```

## Project Structure

```
SharaCharge/
├── app/                    # Main application module
├── feature/               # Feature modules
│   ├── charging/
│   ├── main/
│   ├── payment/
│   ├── power-source/
│   ├── splash/
│   ├── user/
│   └── vehicles/
├── core/                  # Core modules
│   ├── analytics/
│   ├── data/
│   ├── database/
│   ├── domain/
│   ├── model/
│   └── network/
├── common/                # Common/shared modules
│   ├── lib/
│   ├── resources/
│   ├── testing/
│   └── ui/
├── build-logic/           # Build convention plugins
└── benchmark/             # Performance benchmarks
```

## Configuration Files

### Local Properties
Create `local.properties` in the project root:
```properties
# Android SDK location
sdk.dir=/path/to/Android/sdk

# Package name (default: com.SharaSpot)
PACKAGE_NAME=com.SharaSpot

# Optional: Debug signing configuration
DEBUG_STORE_FILE=path/to/debug.keystore
DEBUG_KEY_ALIAS=debug
DEBUG_KEY_PASSWORD=android
DEBUG_STORE_PASSWORD=android

# Optional: Release signing configuration
RELEASE_STORE_FILE=path/to/release.keystore
RELEASE_KEY_ALIAS=release
RELEASE_KEY_PASSWORD=your_password
RELEASE_STORE_PASSWORD=your_password
```

### Secrets Configuration
Create `secrets.default.properties` in the project root:
```properties
# Google Maps API Key (for GMS flavor)
MAPS_API_KEY=your_maps_api_key_here

# Other API keys and secrets
```

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Android Build

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build with Gradle
        run: ./gradlew build

      - name: Upload APKs
        uses: actions/upload-artifact@v4
        with:
          name: apks
          path: app/build/outputs/apk/**/*.apk
```

### GitLab CI Example
```yaml
image: openjdk:21-jdk

variables:
  ANDROID_SDK_ROOT: "${CI_PROJECT_DIR}/.android-sdk"

before_script:
  - chmod +x ./gradlew

build:
  stage: build
  script:
    - ./gradlew clean build
  artifacts:
    paths:
      - app/build/outputs/apk/
    expire_in: 1 week
```

### Using Docker in CI/CD
```yaml
# GitHub Actions with Docker
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Build in Docker
        run: |
          docker build -t sharacharge-build .
          docker run --rm -v $(pwd):/workspace sharacharge-build
```

## Troubleshooting

### Build Fails with "SDK not found"
**Solution:**
1. Install Android SDK via Android Studio, OR
2. Use the Docker build (SDK included in image)

### Build Fails with "JDK version mismatch"
**Solution:**
```bash
# Check Java version
java -version  # Must be 21

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/jdk-21
```

### Build Fails with "Could not resolve dependencies"
**Solution:**
```bash
# Clear Gradle cache and rebuild
./gradlew clean --no-daemon
rm -rf ~/.gradle/caches
./gradlew build
```

### "Gradle version X.X.X is required"
**Solution:** Always use the wrapper
```bash
# ✓ Correct
./gradlew build

# ✗ Incorrect
gradle build
```

### "Configuration cache problems found"
**Solution:**
```bash
# Disable configuration cache temporarily
./gradlew build --no-configuration-cache
```

## Performance Optimization

### Gradle Performance
The project is configured with optimal Gradle settings:
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx6g
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.worker.max=4
```

### Build Speed Tips
1. **Use the Gradle daemon** (enabled by default)
2. **Avoid clean builds** unless necessary
3. **Use build caching** (already enabled)
4. **Build specific variants** instead of all variants
5. **Use parallel builds** (already enabled)

### Faster Builds
```bash
# Build only what you need
./gradlew assembleDebug           # Instead of 'assemble'
./gradlew assembleGmsDebug       # Specific variant

# Skip tests during development
./gradlew assembleDebug -x test

# Incremental build
./gradlew assembleDebug           # No 'clean'
```

## Build Output Locations

### APKs
```
app/build/outputs/apk/
├── gms/
│   ├── debug/
│   │   └── app-gms-debug.apk
│   └── release/
│       └── app-gms-release.apk
└── foss/
    ├── debug/
    │   └── app-foss-debug.apk
    └── release/
        └── app-foss-release.apk
```

### Test Results
```
app/build/reports/tests/
└── testDebugUnitTest/
    └── index.html
```

### Lint Reports
```
app/build/reports/lint-results.html
```

## Environment Setup Script

### Linux/macOS
```bash
#!/bin/bash
# setup-build-env.sh

# Install JDK 21 (Ubuntu/Debian)
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk

# Or on macOS with Homebrew
# brew install openjdk@21

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version
./gradlew --version

# Build
./gradlew clean build
```

### Windows (PowerShell)
```powershell
# setup-build-env.ps1

# Download and install JDK 21 from:
# https://adoptium.net/temurin/releases/?version=21

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version
.\gradlew --version

# Build
.\gradlew clean build
```

## Additional Resources

- **Version Alignment Details**: [VERSION_ALIGNMENT.md](VERSION_ALIGNMENT.md)
- **Android Documentation**: https://developer.android.com/
- **Kotlin Documentation**: https://kotlinlang.org/docs/
- **Gradle Documentation**: https://docs.gradle.org/

## Support

For build issues:
1. Check [VERSION_ALIGNMENT.md](VERSION_ALIGNMENT.md) for version compatibility
2. Review this build guide's troubleshooting section
3. Use the Docker build for a guaranteed working environment
4. Clear Gradle caches and retry

---

**Note:** All builds should succeed with these aligned versions. If you encounter issues, ensure you're using JDK 21 and the Gradle wrapper (`./gradlew`).
