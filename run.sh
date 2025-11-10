#!/bin/bash
# Quick Run Script for SharaCharge
# This script does an incremental build and installs/launches the app

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
PACKAGE_NAME="com.esttp.SharaSpot"
BUILD_VARIANT="${1:-Debug}"  # Default to Debug, can pass Release, PreRelease, Benchmark

echo -e "${BLUE}🔧 SharaCharge Quick Run Script${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# Check if device is connected
echo -e "${YELLOW}📱 Checking for connected devices...${NC}"
if ! adb devices | grep -q "device$"; then
    echo -e "${RED}❌ No devices found!${NC}"
    echo ""
    echo "Please connect a device or start an emulator:"
    echo "  • Physical device: Enable USB debugging and connect"
    echo "  • Emulator: emulator -avd <avd-name>"
    echo ""
    exit 1
fi

DEVICE_COUNT=$(adb devices | grep "device$" | wc -l)
echo -e "${GREEN}✓ Found ${DEVICE_COUNT} device(s)${NC}"
echo ""

# Build and install
echo -e "${YELLOW}🔨 Building and installing ${BUILD_VARIANT} variant...${NC}"
./gradlew install${BUILD_VARIANT}

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build/Install failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Installation successful${NC}"
echo ""

# Launch the app
echo -e "${YELLOW}🚀 Launching app...${NC}"
adb shell am start -n ${PACKAGE_NAME}/.MainActivity

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ App launched successfully!${NC}"
    echo ""
    echo -e "${BLUE}📊 Useful commands:${NC}"
    echo "  • View logs:     adb logcat | grep ${PACKAGE_NAME}"
    echo "  • Clear logs:    adb logcat -c"
    echo "  • Stop app:      adb shell am force-stop ${PACKAGE_NAME}"
    echo "  • Uninstall:     adb uninstall ${PACKAGE_NAME}"
    echo ""
    echo -e "${GREEN}✨ Ready to go!${NC}"
else
    echo -e "${RED}❌ Failed to launch app${NC}"
    echo "Try launching manually from your device"
    exit 1
fi
