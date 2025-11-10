# Docker Build Guide for SharaCharge

This guide explains how to build the SharaCharge Android application using Docker for a consistent, reproducible build environment.

## Prerequisites

- Docker installed on your system
- At least 8GB of available RAM for the build process
- Approximately 5GB of disk space for the Docker image

## Quick Start

### 1. Build the Docker Image

```bash
docker build -t sharacharge-build .
```

This command:
- Downloads Ubuntu 24.04 base image
- Installs OpenJDK 21
- Downloads and configures Android SDK
- Installs required SDK platforms (35, 36) and build tools (34, 35)
- Sets up the build environment

**Note:** The initial build may take 10-15 minutes as it downloads all dependencies.

### 2. Run the Build

#### Option A: Build Without Volume Mount (Recommended for CI/CD)

```bash
docker run --rm sharacharge-build
```

This builds the APK inside the container. To extract the built APK:

```bash
# Create a container
docker create --name temp-build sharacharge-build

# Copy the APK out
docker cp temp-build:/workspace/app/build/outputs/apk/ ./outputs/

# Remove the temporary container
docker rm temp-build
```

#### Option B: Build With Volume Mount (For Local Development)

```bash
docker run --rm -v $(pwd):/workspace sharacharge-build
```

This mounts your local directory into the container, so build outputs appear in your local `app/build/` directory.

### 3. Interactive Shell (For Debugging)

```bash
docker run --rm -it -v $(pwd):/workspace sharacharge-build /bin/bash
```

Once inside the container, you can run Gradle commands manually:

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew test
```

## Build Variants

The project supports multiple build variants:

- **debug**: Debuggable, unminified test version
- **release**: Production version (minified, not debuggable)
- **preRelease**: Debuggable, unminified production version
- **benchmark**: Performance testing variant

Build specific variants:

```bash
# Build debug APK
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew assembleDebug

# Build release APK
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew assembleRelease

# Build all variants
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew assemble
```

## Configuration

### Package Name

The default package name is `com.esttp.SharaSpot`. To use a different package name:

1. Create or edit `local.properties` in your project root:
```properties
PACKAGE_NAME=com.your.package
```

2. Run the build with volume mount:
```bash
docker run --rm -v $(pwd):/workspace sharacharge-build
```

### Signing Configuration

For signed builds, add signing configuration to `local.properties`:

```properties
# Debug signing
DEBUG_STORE_FILE=path/to/debug.keystore
DEBUG_KEY_ALIAS=debug
DEBUG_KEY_PASSWORD=your_password
DEBUG_STORE_PASSWORD=your_password

# Release signing
RELEASE_STORE_FILE=path/to/release.keystore
RELEASE_KEY_ALIAS=release
RELEASE_KEY_PASSWORD=your_password
RELEASE_STORE_PASSWORD=your_password
```

## What the Dockerfile Does

### Environment Setup
- **Android SDK Root**: `/opt/android-sdk`
- **Java Home**: OpenJDK 21 at `/usr/lib/jvm/java-21-openjdk-amd64`
- **Gradle User Home**: `/root/.gradle`

### Installed SDK Components
- Android SDK Command Line Tools (version 11076708)
- Platform SDK 36 (compileSdk)
- Platform SDK 35 (targetSdk)
- Build Tools 35.0.0 and 34.0.0
- Platform Tools
- NDK 27.0.12077973

### Gradle Configuration
The Dockerfile configures Gradle with optimal settings:
- **Max Heap**: 4GB (`-Xmx4g`)
- **MetaSpace**: 1GB
- **Garbage Collector**: Parallel GC
- **Features**: Daemon, parallel execution, caching enabled

### Automatic local.properties Creation

The Docker entrypoint automatically creates `local.properties` if it doesn't exist with:
```properties
sdk.dir=/opt/android-sdk
PACKAGE_NAME=com.esttp.SharaSpot
```

This ensures builds work even when using volume mounts.

## Troubleshooting

### Build Fails with "Gradle daemon disappeared"

This usually indicates insufficient memory. Solutions:

1. **Increase Docker Memory**:
   - Docker Desktop: Settings → Resources → Memory (set to at least 8GB)
   - Linux: No limit by default, check system memory

2. **Reduce Gradle Memory**:
   Edit `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx2g
   ```

### "Directory does not exist" Error

This error occurs if `local.properties` has an invalid `sdk.dir`. Solutions:

1. Delete local `local.properties` before building
2. Ensure `.dockerignore` is present (prevents copying local file)
3. Let the entrypoint script create it automatically

### SDK License Issues

If you see license acceptance errors:

```bash
# Run interactively
docker run --rm -it sharacharge-build /bin/bash

# Inside container
sdkmanager --licenses
```

### Slow Builds

First build is slow due to dependency downloads. Subsequent builds are faster because:
- Gradle caching is enabled
- Dependencies are cached in the image
- Parallel execution is enabled

To speed up incremental builds, use volume mount to preserve Gradle cache:

```bash
docker run --rm \
  -v $(pwd):/workspace \
  -v gradle-cache:/root/.gradle \
  sharacharge-build
```

## Advanced Usage

### Custom Gradle Commands

```bash
# Run tests
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew test

# Generate coverage report
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew jacocoTestReport

# Dependency updates
docker run --rm -v $(pwd):/workspace sharacharge-build ./gradlew dependencyUpdates
```

### Multi-Stage Builds

For smaller images in production, consider using multi-stage builds:

```dockerfile
# Builder stage
FROM sharacharge-build as builder
RUN ./gradlew assembleRelease

# Runtime stage
FROM alpine:latest
COPY --from=builder /workspace/app/build/outputs/apk/release/*.apk /apk/
```

### CI/CD Integration

#### GitHub Actions Example

```yaml
- name: Build APK with Docker
  run: |
    docker build -t sharacharge-build .
    docker run --rm sharacharge-build
    docker cp $(docker create sharacharge-build):/workspace/app/build/outputs/apk/ ./
```

#### GitLab CI Example

```yaml
build:
  image: docker:latest
  services:
    - docker:dind
  script:
    - docker build -t sharacharge-build .
    - docker run --rm sharacharge-build
  artifacts:
    paths:
      - app/build/outputs/apk/
```

## Files Modified for Docker Support

- **Dockerfile**: Complete build environment definition
- **.dockerignore**: Prevents copying unnecessary files to Docker
- **DOCKER_BUILD.md**: This documentation

## Support

For issues specific to Docker builds:
1. Check Docker logs: `docker logs <container-id>`
2. Run interactively to debug: `docker run --rm -it sharacharge-build /bin/bash`
3. Verify Docker has sufficient resources allocated

For application build issues, refer to the main `BUILD.md` documentation.
