# SharaCharge Build Optimization Guide

## Summary of Optimizations

This document outlines the optimizations made to fix Gradle build errors and improve Docker build performance with reduced memory usage.

## Problems Fixed

### 1. File Lock Issues
**Problem:** Could not delete build class files during compilation
**Solution:** 
- Clean build directories before building
- Disabled configuration cache which was causing file system issues
- Added `--no-daemon` flag for Docker builds

### 2. Configuration Cache Errors
**Problem:** Could not create configuration-cache-report directory
**Solution:**
- Disabled configuration cache (`org.gradle.configuration-cache=false`)
- Added `--no-configuration-cache` flag to Gradle commands

### 3. High Memory Usage
**Problem:** Build required 6GB RAM, causing crashes in resource-constrained environments
**Solution:**
- Reduced max heap from 6GB to 2GB
- Reduced metaspace from 1GB to 512MB
- Added memory optimization flags
- Reduced parallel workers from 4 to 2

## Memory Optimization Details

### gradle.properties Changes
```properties
# Before: -Xmx6g -XX:MaxMetaspaceSize=1g
# After:
org.gradle.jvmargs=-Xmx2g -Xms512m -XX:MaxMetaspaceSize=512m -XX:+UseStringDeduplication -XX:ReservedCodeCacheSize=256m

# Reduced workers
org.gradle.worker.max=2  # Was: 4

# Disabled configuration cache
org.gradle.configuration-cache=false  # Was: true
```

### Dockerfile Optimizations
- Aligned Docker memory settings with gradle.properties (2GB)
- Added `--no-daemon` and `--no-configuration-cache` flags
- Added memory optimization JVM flags

### build-logic/gradle.properties
- Added memory settings: `-Xmx1g -XX:MaxMetaspaceSize=512m`
- Disabled configuration cache
- Changed configureondemand to false for stability

## Memory Usage Breakdown

| Component | Memory Usage |
|-----------|--------------|
| Gradle JVM Heap | 2GB max |
| Metaspace | 512MB |
| Code Cache | 256MB |
| System Overhead | ~256MB |
| **Total** | **~3GB** |

## Running Builds

### Local Build (with Gradle wrapper)
```bash
# Clean build
./gradlew clean build --no-daemon --no-configuration-cache

# Incremental build (faster)
./gradlew build --no-daemon --no-configuration-cache

# Debug APK only
./gradlew assembleDebug --no-daemon --no-configuration-cache
```

### Docker Build

#### Using the helper script (recommended)
```bash
# Full clean build with 3GB RAM limit
./docker-build.sh

# With custom memory limit
./docker-build.sh --memory 4g

# Quick debug build with minimal memory
./docker-build.sh --assemble --memory 2g

# Incremental build (skip clean)
./docker-build.sh --build-only

# Interactive shell for debugging
./docker-build.sh --interactive
```

#### Manual Docker commands
```bash
# Build the Docker image
docker build -t sharacharge-build .

# Run with memory limit
docker run --rm \
  --memory=3g \
  --memory-swap=3g \
  sharacharge-build

# Interactive mode
docker run --rm -it \
  --memory=3g \
  --memory-swap=3g \
  -v $(pwd):/workspace \
  sharacharge-build /bin/bash
```

## Performance Tips

### 1. For Faster Builds
- Use `--build-only` (skip clean) when possible
- Use `assembleDebug` instead of full build for development
- Keep Docker containers running for incremental builds

### 2. For Lower Memory Usage
- Use minimum 2GB RAM allocation
- Close other applications during build
- Use `--assemble` for APK-only builds

### 3. For Docker Optimization
- Don't rebuild Docker image unless dependencies change
- Use volume mounts (`-v`) to preserve build cache
- Run builds without `clean` when possible

## Troubleshooting

### Out of Memory Errors
If you still get OOM errors:
1. Increase Docker memory limit: `./docker-build.sh --memory 4g`
2. Close other applications
3. Try building without parallel execution:
   ```bash
   ./gradlew build --no-daemon --no-configuration-cache --no-parallel
   ```

### Build Hangs or Freezes
1. Stop any running Gradle daemons: `./gradlew --stop`
2. Clean build directories: `rm -rf build */build .gradle`
3. Try with `--no-daemon` flag

### File Permission Errors in Docker
If you get permission errors:
```bash
# Fix permissions on mounted volume
docker run --rm -it -v $(pwd):/workspace sharacharge-build chown -R $(id -u):$(id -g) /workspace
```

## Continuous Integration

For CI/CD pipelines, use these settings:
```yaml
# Example GitHub Actions
- name: Build with Gradle
  run: ./gradlew build --no-daemon --no-configuration-cache --stacktrace
  env:
    GRADLE_OPTS: "-Xmx2g -XX:MaxMetaspaceSize=512m"
```

## Build Times

Expected build times (approximate):
- Full clean build: 5-10 minutes
- Incremental build: 2-5 minutes
- Debug APK only: 1-3 minutes

Times vary based on:
- Available CPU cores
- Available memory
- Disk I/O speed
- Network speed (for first-time dependency downloads)

## Summary

These optimizations reduce memory usage from 6GB to 2-3GB total while maintaining build stability and performance. The Docker build process is now more reliable and can run on machines with limited resources.
