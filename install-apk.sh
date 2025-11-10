#!/bin/bash
# Install Pre-Built APK Script
# Use this when you have a pre-built APK and just want to install it

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

PACKAGE_NAME="com.esttp.SharaSpot"
BUILD_DIR="app/build/outputs/apk"
VARIANT="${1:-debug}"  # Default to debug

echo -e "${BLUE}📦 SharaCharge APK Installer${NC}"
echo -e "${BLUE}=============================${NC}"
echo ""

# Check if device is connected
echo -e "${YELLOW}📱 Checking for connected devices...${NC}"
if ! adb devices | grep -q "device$"; then
    echo -e "${RED}❌ No devices found!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Device connected${NC}"
echo ""

# Find APK
echo -e "${YELLOW}🔍 Looking for ${VARIANT} APK...${NC}"
APK_PATH=$(find ${BUILD_DIR}/${VARIANT} -name "*.apk" -type f 2>/dev/null | head -1)

if [ -z "$APK_PATH" ]; then
    echo -e "${RED}❌ No APK found in ${BUILD_DIR}/${VARIANT}${NC}"
    echo ""
    echo "Available APKs:"
    find ${BUILD_DIR} -name "*.apk" -type f 2>/dev/null || echo "  None found"
    echo ""
    echo "Build an APK first:"
    echo "  ./gradlew assembleDebug"
    exit 1
fi

echo -e "${GREEN}✓ Found: ${APK_PATH}${NC}"
echo ""

# Check if app is already installed
if adb shell pm list packages | grep -q "${PACKAGE_NAME}"; then
    echo -e "${YELLOW}ℹ️  App is already installed, will reinstall${NC}"
    INSTALL_FLAGS="-r"  # Reinstall flag
else
    echo -e "${YELLOW}ℹ️  Fresh installation${NC}"
    INSTALL_FLAGS=""
fi
echo ""

# Install
echo -e "${YELLOW}📲 Installing APK...${NC}"
adb install ${INSTALL_FLAGS} "${APK_PATH}"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Installation successful!${NC}"
    echo ""

    # Ask if user wants to launch
    read -p "Launch the app now? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}🚀 Launching app...${NC}"
        adb shell am start -n ${PACKAGE_NAME}/.MainActivity
        echo -e "${GREEN}✓ App launched!${NC}"
    fi

    echo ""
    echo -e "${BLUE}📊 Useful commands:${NC}"
    echo "  • View logs:     adb logcat | grep ${PACKAGE_NAME}"
    echo "  • Stop app:      adb shell am force-stop ${PACKAGE_NAME}"
    echo "  • Uninstall:     adb uninstall ${PACKAGE_NAME}"
else
    echo -e "${RED}❌ Installation failed${NC}"
    exit 1
fi
