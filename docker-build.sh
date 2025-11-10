#!/bin/bash
# SharaCharge Docker Build Helper Script
# This script helps build SharaCharge in Docker with optimal memory settings

set -e

echo "=================================="
echo "SharaCharge Docker Build Helper"
echo "=================================="
echo ""

# Default values
MEMORY_LIMIT="3g"
BUILD_COMMAND="./gradlew clean build --no-daemon --no-configuration-cache --stacktrace"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --memory)
      MEMORY_LIMIT="$2"
      shift 2
      ;;
    --build-only)
      BUILD_COMMAND="./gradlew build --no-daemon --no-configuration-cache --stacktrace"
      shift
      ;;
    --assemble)
      BUILD_COMMAND="./gradlew assembleDebug --no-daemon --no-configuration-cache --stacktrace"
      shift
      ;;
    --interactive)
      docker run --rm -it \
        --memory="$MEMORY_LIMIT" \
        --memory-swap="$MEMORY_LIMIT" \
        -v "$(pwd):/workspace" \
        sharacharge-build /bin/bash
      exit 0
      ;;
    --help)
      echo "Usage: $0 [OPTIONS]"
      echo ""
      echo "Options:"
      echo "  --memory LIMIT      Set memory limit (default: 3g)"
      echo "  --build-only        Skip clean, just build"
      echo "  --assemble          Build debug APK only"
      echo "  --interactive       Start interactive shell"
      echo "  --help              Show this help message"
      echo ""
      echo "Examples:"
      echo "  $0                          # Full clean build with 3GB RAM"
      echo "  $0 --memory 4g              # Full clean build with 4GB RAM"
      echo "  $0 --build-only             # Incremental build (faster)"
      echo "  $0 --assemble --memory 2g   # Quick debug build with 2GB RAM"
      echo "  $0 --interactive            # Open shell in container"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo "Use --help for usage information"
      exit 1
      ;;
  esac
done

echo "Building Docker image..."
docker build -t sharacharge-build .

echo ""
echo "Running build with memory limit: $MEMORY_LIMIT"
echo "Build command: $BUILD_COMMAND"
echo ""

docker run --rm \
  --memory="$MEMORY_LIMIT" \
  --memory-swap="$MEMORY_LIMIT" \
  -v "$(pwd):/workspace" \
  sharacharge-build \
  /bin/bash -c "$BUILD_COMMAND"

echo ""
echo "=================================="
echo "Build completed successfully!"
echo "=================================="
