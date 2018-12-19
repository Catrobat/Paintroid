FROM openjdk:8-jdk

# Android Dependencies
# --------------------
# Adapt the paramters below to change the dependencies.
#

# Android SDK 26.1.1
ARG ANDROID_SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip"

# Android NDK r18b
ARG ANDROID_NDK_URL="https://dl.google.com/android/repository/android-ndk-r18b-linux-x86_64.zip"

ARG SDKMANAGER_PACKAGES="build-tools;27.0.3 platforms;android-26 cmake;3.6.4111459 system-images;android-24;default;x86_64 emulator platform-tools"


# Arguments, Environment
# ----------------------
#

# Default values for the arguments to be passed from the Jenkinsfile.
# Those contain the uid and gid of the Jenkins user, and are used to
# create this user inside of the container, needed for eg ssh-agent to work
ARG USER_ID=1000
ARG USER_HOME=/home/user
ARG GROUP_ID=1000
ARG KVM_GROUP_ID=999

# Environment variables that are needed by the build job.

ENV ANDROID_SDK_ROOT=$USER_HOME/android/sdk
ENV ANDROID_AVD_HOME=$USER_HOME/avds

# Deprecated: Still used by gradle, once gradle respects ANDROID_SDK_ROOT, this can be removed
ENV ANDROID_HOME=$ANDROID_SDK_ROOT

ARG _SDKMANAGER=$ANDROID_SDK_ROOT/tools/bin/sdkmanager

# System Packages
# ---------------
#

# curl/openssh-client: upload to web/share
RUN apt-get update && apt-get install -y --no-install-recommends \
        curl \
        openssh-client \
    && rm -rf /var/lib/apt/lists/*

# Install Fastlane
RUN apt-get update && apt-get install -y --no-install-recommends \
        rubygems \
        ruby-dev \
        g++ \
        make \
        less \
    && rm -rf /var/lib/apt/lists/* \
    && gem update --system \
    && gem install fastlane -NV \
    && apt-get purge -y --auto-remove \
        ruby-dev \
        g++ \
        make

# User Management
# ---------------
#

# add the 'Jenkins' user
# Add group of the user used by Jenkins during building.
RUN if [ ! $(getent group $GROUP_ID) ]; then groupadd -g $GROUP_ID user; fi

RUN groupadd -g $KVM_GROUP_ID kvm

# Add the user used by Jenkins during building.
RUN useradd -m -u $USER_ID -g $GROUP_ID -G $KVM_GROUP_ID -d $USER_HOME -s /usr/sbin/nologin user

# Run all other commands as user
USER user

RUN mkdir -p $ANDROID_AVD_HOME


# Android SDK
# ------------------
#
RUN curl $ANDROID_SDK_URL --output /tmp/android_sdk.zip \
    && mkdir -p $ANDROID_SDK_ROOT \
    && unzip -d $ANDROID_SDK_ROOT /tmp/android_sdk.zip \
    && rm /tmp/android_sdk.zip


# Android NDK
# ----------------
#
RUN if [ ! -z "$ANDROID_NDK_URL" ]; then \
        curl $ANDROID_NDK_URL --output /tmp/android_ndk.zip \
        && mkdir -p $ANDROID_SDK_ROOT /tmp/ndk \
        && unzip -d /tmp/ndk /tmp/android_ndk.zip \
        && mv /tmp/ndk/* $ANDROID_SDK_ROOT/ndk-bundle \
        && rm /tmp/android_ndk.zip; \
    fi


# Installing SDK Packages
# -----------------------
#
RUN if [ ! -z "$SDKMANAGER_PACKAGES" ]; then yes | $_SDKMANAGER $SDKMANAGER_PACKAGES; fi

# Performance related settings for the JVM
# - Respect the cgroup settings for memory.
#
# Note: Usage of _JAVA_OPTIONS is in general discouraged.
#       This is an internal flag that will be preferred over
#       JAVA_TOOL_OPTIONS and the command line parameters.
#       We still use it here to ensure that these settings
#       are respected, no matter what is configured elsewhere.
ENV _JAVA_OPTIONS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
