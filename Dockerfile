# SharaCharge Reproducible Build Environment
# This Dockerfile provides a fully configured environment for building the SharaCharge Android application
# with all version dependencies properly aligned.

FROM ubuntu:24.04

# Set environment variables
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV GRADLE_USER_HOME=/root/.gradle
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$JAVA_HOME/bin

# Install required packages
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install Android SDK Command Line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    cd ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip commandlinetools-linux-11076708_latest.zip && \
    mv cmdline-tools latest && \
    rm commandlinetools-linux-11076708_latest.zip

# Accept Android SDK licenses
RUN yes | sdkmanager --licenses

# Install Android SDK components matching our build configuration
# SDK Platform 36 (compileSdk)
# SDK Platform 35 (targetSdk)
# Build Tools
RUN sdkmanager \
    "platforms;android-36" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    "platform-tools" \
    "ndk;27.0.12077973"

# Set working directory
WORKDIR /workspace

# Copy project files
COPY . /workspace/

# Make gradlew executable
RUN chmod +x ./gradlew

# Pre-download Gradle wrapper and dependencies (optional, speeds up builds)
RUN ./gradlew --version || true

# Default command: build the project
CMD ["./gradlew", "clean", "build"]

# Usage:
# Build image: docker build -t sharacharge-build .
# Run build:   docker run --rm -v $(pwd):/workspace sharacharge-build
# Interactive: docker run --rm -it -v $(pwd):/workspace sharacharge-build /bin/bash
