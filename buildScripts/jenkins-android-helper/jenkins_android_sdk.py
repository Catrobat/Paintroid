#!/usr/bin/env python3

# This file is part of Jenkins-Android-Emulator Helper.
#    Copyright (C) 2018  Michael Musenbrock
#
# Jenkins-Android-Helper is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Jenkins-Android-Helper is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Jenkins-Android-Helper.  If not, see <http://www.gnu.org/licenses/>.

## ANDROID_SDK_ROOT needs to be set to the Android SDK

import os
import sys
import tempfile
import subprocess
import re
import uuid
import time
import shutil
from collections import namedtuple

import jenkins_android_helper_commons
import ini_helper_functions
import android_emulator_helper_functions

ERROR_CODE_WAIT_NO_AVD_CREATED = 1
ERROR_CODE_WAIT_AVD_CREATED_BUT_NOT_RUNNING = 2
ERROR_CODE_WAIT_EMULATOR_RUNNING_UNKNOWN_SERIAL = 3
ERROR_CODE_WAIT_EMULATOR_RUNNING_STARTUP_TIMEOUT = 4

ERROR_CODE_SDK_TOOLS_LICENSE_DIR_DOES_NOT_EXIST_AND_CANT_CREATE = 5
ERROR_CODE_SDK_TOOLS_ARCHIVE_CHKSUM_MISMATCH = 6
ERROR_CODE_SDK_TOOLS_ARCHIVE_EXTRACT_ERROR = 7

AndroidSDKContent = namedtuple("AndroidSDKContent", "path, executable, winending")

class AndroidSDK:
    # SDK paths
    ## root SDK directory, avd home and workspace will be read from environment
    __sdk_directory = ""
    __avd_home_directory = ""
    __workspace_directory = ""

    ## all other are relative to the root and can be retrievied via __get_full_sdk_path
    ANDROID_SDK_TOOLS_DIR = "tools"
    ANDROID_NDK_DIR = "ndk-bundle"

    ANDROID_SDK_TOOLS_BIN_SDKMANAGER = AndroidSDKContent(path=os.path.join(ANDROID_SDK_TOOLS_DIR, "bin", "sdkmanager"), executable=True, winending=".bat")
    ANDROID_SDK_TOOLS_BIN_AVDMANAGER = AndroidSDKContent(path=os.path.join(ANDROID_SDK_TOOLS_DIR, "bin", "avdmanager"), executable=True, winending=".bat")
    ANDROID_SDK_TOOLS_BIN_EMULATOR = AndroidSDKContent(path=os.path.join("emulator", "emulator"), executable=True, winending=".exe")
    ANDROID_SDK_TOOLS_BIN_ADB = AndroidSDKContent(path=os.path.join("platform-tools", "adb"), executable=True, winending=".exe")

    ### Info: This package shall support the platforms: linux, windows, cygwin and mac, therefore
    ### a general check is done in the constructor, and all further system dependent calls rely
    ### on a support for those platforms and no further checks are done
    ### the variables PLATFORM_ID_... shall correspond to sys.platform

    PLATFORM_ID_LINUX = "linux"
    PLATFORM_ID_MAC = "darwin"
    PLATFORM_ID_WIN = "win32"
    PLATFORM_ID_CYGWIN = "cygwin"
    SUPPORTED_PLATFORMS = [ PLATFORM_ID_LINUX, PLATFORM_ID_MAC, PLATFORM_ID_WIN, PLATFORM_ID_CYGWIN ]

    # SDK URLs and version
    ANDROID_SDK_TOOLS_ARCHIVE = { PLATFORM_ID_LINUX: "sdk-tools-linux-4333796.zip", PLATFORM_ID_MAC: "sdk-tools-darwin-4333796.zip", PLATFORM_ID_WIN: "sdk-tools-windows-4333796.zip", PLATFORM_ID_CYGWIN: "sdk-tools-windows-4333796.zip" }
    ANDROID_SDK_TOOLS_ARCHIVE_SHA256_CHECKSUM = { PLATFORM_ID_LINUX: "92ffee5a1d98d856634e8b71132e8a95d96c83a63fde1099be3d86df3106def9", PLATFORM_ID_MAC: "ecb29358bc0f13d7c2fa0f9290135a5b608e38434aad9bf7067d0252c160853e", PLATFORM_ID_WIN: "7e81d69c303e47a4f0e748a6352d85cd0c8fd90a5a95ae4e076b5e5f960d3c7a", PLATFORM_ID_CYGWIN: "7e81d69c303e47a4f0e748a6352d85cd0c8fd90a5a95ae4e076b5e5f960d3c7a" }
    ANDROID_SDK_TOOLS_VERSION = "26.1.1"
    ANDROID_SDK_BASE_URL = "https://dl.google.com/android/repository"

    ANDROID_NDK_ARCHIVE = { PLATFORM_ID_LINUX: "android-ndk-r16b-linux-x86_64.zip", PLATFORM_ID_MAC: "android-ndk-r16b-darwin-x86_64.zip", PLATFORM_ID_WIN: "android-ndk-r16b-windows-x86_64.zip", PLATFORM_ID_CYGWIN: "android-ndk-r16b-windows-x86_64.zip" }
    ANDROID_NDK_ARCHIVE_SHA256_CHECKSUM = { PLATFORM_ID_LINUX: "bcdea4f5353773b2ffa85b5a9a2ae35544ce88ec5b507301d8cf6a76b765d901", PLATFORM_ID_MAC: "9654a692ed97713e35154bfcacb0028fdc368128d636326f9644ed83eec5d88b", PLATFORM_ID_WIN: "4c6b39939b29dfd05e27c97caf588f26b611f89fe95aad1c987278bd1267b562", PLATFORM_ID_CYGWIN: "4c6b39939b29dfd05e27c97caf588f26b611f89fe95aad1c987278bd1267b562" }

    ANDROID_SDK_BUILD_TOOLS_VERSION_DEFAULT = "27.0.1"
    ANDROID_SDK_PLATFORM_VERSION_DEFAULT = "27"

    ### module properties file
    ANDROID_SDK_SRC_PROPS_FILENAME = "source.properties"
    ANDROID_SDK_TOOLS_PROP_NAME_PKG_REV = "Pkg.Revision"
    ANDROID_SDK_TOOLS_PROP_NAME_PKG_PATH = "Pkg.Path"
    ANDROID_SDK_TOOLS_PROP_NAME_PKG_DESC = "Pkg.Desc"

    ### tools versions and properties contents
    ANDROID_SDK_TOOLS_PROP_VAL_PKG_REV = ANDROID_SDK_TOOLS_VERSION
    ANDROID_SDK_TOOLS_PROP_VAL_PKG_PATH = "tools"
    ANDROID_SDK_TOOLS_PROP_VAL_PKG_DESC = "Android SDK Tools"

    ### ndk versions and properties contents
    #### Currently catroid can only be build with Android NDK r16, manual install that version
    ANDROID_NDK_WORKAROUND_KEEP_R16 = False
    ANDROID_NDK_PROP_VAL_PKG_REV = "16.1.4479499"
    ANDROID_NDK_PROP_VAL_PKG_PATH = None
    ANDROID_NDK_PROP_VAL_PKG_DESC = "Android NDK"

    ## sdk licenses
    ANDROID_SDK_ROOT_LICENSE_DIR = "licenses"
    ANDROID_SDK_ROOT_LICENSE_STANDARD_FILE = os.path.join(ANDROID_SDK_ROOT_LICENSE_DIR, "android-sdk-license")
    ANDROID_SDK_ROOT_LICENSE_PREVIEW_FILE = os.path.join(ANDROID_SDK_ROOT_LICENSE_DIR, "android-sdk-preview-license")
    ANDROID_SDK_ROOT_LICENSE_STANDARD_HASH = "d56f5187479451eabf01fb78af6dfcb131a6481e"
    ANDROID_SDK_ROOT_LICENSE_PREVIEW_HASH = "84831b9409646a918e30573bab4c9c91346d8abd"

    ## sdk modules, platform-tools are always installed
    ANDROID_SDK_MODULE_PLATFORM_TOOLS = "platform-tools"
    ANDROID_SDK_MODULE_NDK = "ndk-bundle"
    ANDROID_SDK_MODULE_EMULATOR = "emulator"

    ANDROID_SDK_SYSTEM_IMAGE_IDENTIFIER = "system-images"


    __download_if_neccessary = False

    # Emulator functionality
    emulator_avd_name = ""

    ANDROID_EMULATOR_DEFAULT_SDCARD_SIZE = "200M"

    ANDROID_EMULATOR_SWITCH_NO_WINDOW = "-no-window"
    ANDROID_EMULATOR_SWITCH_WIPE_DATA = "-wipe-data"

    AVD_NAME_UNIQUE_STORE_FILENAME = "last_unique_avd_name.tmp"

    def __init__(self):
        self.__sdk_directory = os.getenv('ANDROID_SDK_ROOT', "")
        if self.__sdk_directory is None or self.__sdk_directory == "":
            raise Exception("Environment variable ANDROID_SDK_ROOT needs to be set")

        android_home = os.getenv('ANDROID_HOME', "")
        if android_home != "":
            print("INFO: Current ANDROID_HOME [{}] will be set to given ANDROID_SDK_ROOT[{}]!".format(android_home, self.__sdk_directory))
        os.environ['ANDROID_HOME'] = self.__sdk_directory

        self.__avd_home_directory = os.getenv('ANDROID_AVD_HOME', "")
        if self.__avd_home_directory is None or self.__avd_home_directory == "":
            raise Exception("Environment variable ANDROID_AVD_HOME needs to be set")

        self.__workspace_directory = os.getenv('WORKSPACE', "")
        if self.__workspace_directory is None or self.__workspace_directory == "":
            raise Exception("Environment variable WORKSPACE needs to be set")

        self.emulator_read_avd_name()

        if not sys.platform in self.SUPPORTED_PLATFORMS:
            raise Exception("Unsupported platform: " + sys.platform)

    def get_sdk_directory(self):
        return self.__sdk_directory

    def __is_tool_valid(self, tool):
        full_path = self.__get_full_sdk_path(tool)

        is_posix = (sys.platform == self.PLATFORM_ID_LINUX or sys.platform == self.PLATFORM_ID_MAC)

        if is_posix and tool.executable:
            return os.access(full_path, os.X_OK)
        else:
            return os.access(full_path, os.R_OK)


    def __get_full_sdk_path(self, tool):
        if isinstance(tool, str):
            return os.path.join(self.__sdk_directory, tool)

        full_path = os.path.join(self.__sdk_directory, tool.path)

        if (sys.platform == self.PLATFORM_ID_WIN or sys.platform == self.PLATFORM_ID_CYGWIN) and tool.winending != "":
            full_path = full_path + tool.winending

        return full_path

    def download_if_neccessary(self):
        self.__download_if_neccessary = True

    def is_module_installed(self, module_name, expected_revision, expected_pkg_path, expected_desc, verbose=False):
        module_source_props = os.path.join(self.__get_full_sdk_path(module_name), self.ANDROID_SDK_SRC_PROPS_FILENAME)

        if not jenkins_android_helper_commons.is_file(module_source_props):
            if verbose:
                print("[%s] is not readable" % (module_source_props))
            return False

        if expected_revision is not None and not ini_helper_functions.ini_file_helper_check_key_for_value(module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_REV, expected_revision):
            if verbose:
                print("[%s] Value for key [%s] does not match expected: [%s]" % (module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_REV, expected_revision))
            return False

        if expected_pkg_path is not None and not ini_helper_functions.ini_file_helper_check_key_for_value(module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_PATH, expected_pkg_path):
            if verbose:
                print("[%s] Value for key [%s] does not match expected: [%s]" % (module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_PATH, expected_pkg_path))
            return False

        if expected_desc is not None and not ini_helper_functions.ini_file_helper_check_key_for_value(module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_DESC, expected_desc):
            if verbose:
                print("[%s] Value for key [%s] does not match expected: [%s]" % (module_source_props, self.ANDROID_SDK_TOOLS_PROP_NAME_PKG_DESC, expected_desc))
            return False

        return True

    def are_sdk_tools_installed(self, verbose=False):
        if not os.path.isdir(self.__sdk_directory):
            if verbose:
                print("[%s] is not a directory" % self.__sdk_directory)
            return False

        # validate current tools
        if not self.__is_tool_valid(self.ANDROID_SDK_TOOLS_BIN_SDKMANAGER):
            if verbose:
                print("[%s] is not executable" % self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_SDKMANAGER))
            return False

        if not self.is_module_installed(self.ANDROID_SDK_TOOLS_DIR, self.ANDROID_SDK_TOOLS_PROP_VAL_PKG_REV, self.ANDROID_SDK_TOOLS_PROP_VAL_PKG_PATH, self.ANDROID_SDK_TOOLS_PROP_VAL_PKG_DESC, verbose=verbose):
            if verbose:
                print("SDK tools are not correclty (or in the correct version) installed")
            return False

        return True

    def validate_or_download_sdk_tools(self):
        if not self.are_sdk_tools_installed():
            self.download_and_install_sdk_tools()

        if not self.are_sdk_tools_installed(verbose=True):
            raise Exception("Newly setup SDK directory [%s] does not look like a valid installation!" % self.__sdk_directory)

    def download_and_install_package(self, archive_to_download, checksum_sha256, directory_inside_tools):
        if not os.path.isdir(self.__sdk_directory):
            try:
                os.makedirs(self.__sdk_directory, exist_ok=True)
            except:
                raise Exception("Directory [%s] was not existent and could not be created!!" % self.__sdk_directory)

        if not os.access(self.__sdk_directory, os.W_OK):
            raise Exception("Directory [%s] is not writable!!" % self.__sdk_directory)

        # remove dest dir, if already exists
        jenkins_android_helper_commons.remove_file_or_dir(self.__get_full_sdk_path(directory_inside_tools))

        with tempfile.TemporaryDirectory() as tmp_download_dir:
            dest_file_name = os.path.join(tmp_download_dir, archive_to_download)
            download_url = self.ANDROID_SDK_BASE_URL + "/" + archive_to_download

            jenkins_android_helper_commons.download_file(download_url, dest_file_name)

            # check archive
            computed_checksum = jenkins_android_helper_commons.sha256sum(dest_file_name)
            if computed_checksum != checksum_sha256:
                sys.exit(ERROR_CODE_SDK_TOOLS_ARCHIVE_CHKSUM_MISMATCH)

            try:
                jenkins_android_helper_commons.unzip(dest_file_name, self.get_sdk_directory())
            except ValueError:
                sys.exit(ERROR_CODE_SDK_TOOLS_ARCHIVE_EXTRACT_ERROR)

    def download_and_install_sdk_tools(self):
        self.download_and_install_package(self.ANDROID_SDK_TOOLS_ARCHIVE[sys.platform], self.ANDROID_SDK_TOOLS_ARCHIVE_SHA256_CHECKSUM[sys.platform], self.ANDROID_SDK_TOOLS_DIR)

    ### Workaround for removed archs in r17
    def download_and_install_ndk(self):
        self.download_and_install_package(self.ANDROID_NDK_ARCHIVE[sys.platform], self.ANDROID_NDK_ARCHIVE_SHA256_CHECKSUM[sys.platform], self.ANDROID_NDK_DIR)
        shutil.move(self.__get_full_sdk_path('android-ndk-r16b'), self.__get_full_sdk_path(self.ANDROID_NDK_DIR))

    def download_sdk_modules(self, build_tools_version="", platform_version="", ndk=False, system_image="", additional_modules=[]):
        sdkmanager_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_SDKMANAGER) ]

        sdkmanager_command = sdkmanager_command + [ self.ANDROID_SDK_MODULE_PLATFORM_TOOLS ]

        # install ndk if requested
        if ndk:
            if self.ANDROID_NDK_WORKAROUND_KEEP_R16:
                if not self.is_module_installed(self.ANDROID_NDK_DIR, self.ANDROID_NDK_PROP_VAL_PKG_REV, self.ANDROID_NDK_PROP_VAL_PKG_PATH, self.ANDROID_NDK_PROP_VAL_PKG_DESC, verbose=True):
                    print("Manually downloading NDK version %s, otherwise build failes due to missing MIPS tools" % (self.ANDROID_NDK_PROP_VAL_PKG_REV))
                    self.download_and_install_ndk()
            else:
                sdkmanager_command = sdkmanager_command + [ self.ANDROID_SDK_MODULE_NDK ]

        # always install the build tools, if the given version does look bogus, fallback to default
        build_tools_version_str = self.ANDROID_SDK_BUILD_TOOLS_VERSION_DEFAULT
        if build_tools_version is not None and build_tools_version != "":
            if re.match("^[0-9]+\.[0-9]+\.[0-9]+$", build_tools_version):
                build_tools_version_str = build_tools_version
            else:
                print("Given build-tools version [" + build_tools_version + "] does not look like a valid version number")
                print("Fallback to default version [" + build_tools_version_str + "]")
        sdkmanager_command = sdkmanager_command + [ "build-tools;" + build_tools_version_str ]

        # always install a platform, if the given version does look bogus, fallback to default
        platform_version_str = self.ANDROID_SDK_PLATFORM_VERSION_DEFAULT
        if platform_version is not None and platform_version != "":
            if re.match("^[0-9]+$", platform_version):
                platform_version_str = platform_version
            else:
                print("Given platform version [" + platform_version + "] does not look like a valid version number")
                print("Fallback to default version [" + platform_version_str + "]")
        sdkmanager_command = sdkmanager_command + [ "platforms;android-" + platform_version_str ]

        # System image in form of system-images;android-24;default;x86
        # if a system image is set, also install the emulator package
        if system_image is not None and system_image != "":
            system_image_type = jenkins_android_helper_commons.split_string_and_get_part(system_image, ";", 0)
            system_image_platform = jenkins_android_helper_commons.split_string_and_get_part(system_image, ";", 1)
            system_image_vendor = jenkins_android_helper_commons.split_string_and_get_part(system_image, ";", 2)

            if system_image_type == self.ANDROID_SDK_SYSTEM_IMAGE_IDENTIFIER:
                sdkmanager_command = sdkmanager_command + [ self.ANDROID_SDK_MODULE_EMULATOR ]
                sdkmanager_command = sdkmanager_command + [ system_image ]

                ## between api level 15 and 24 there is an explicit add-ons package for google apis listed
                try:
                    system_image_api_level = int(system_image_platform.split("-")[1])
                    if system_image_vendor == "google_apis" and system_image_api_level >= 15 and system_image_api_level <= 24:
                        sdkmanager_command = sdkmanager_command + [ "add-ons;addon-google_apis-google-" + str(system_image_api_level) ]
                except:
                    pass

        sdkmanager_command = sdkmanager_command + additional_modules

        ## remove empty entries
        sdkmanager_command = list(filter(None, sdkmanager_command))

        print('echo y | ' + ' '.join(sdkmanager_command))
        subprocess.run(sdkmanager_command, input=b"y\n", stdout=None, stderr=None)

    def create_avd(self, android_system_image, sdcard_size="default", additional_properties=[]):
        if android_system_image is None or android_system_image == "":
            raise ValueError("An android emulator image needs to be set!")

        self.generate_unique_avd_name()

        avdmanager_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_AVDMANAGER) ]

        avdmanager_command = avdmanager_command + [ "create", "avd", "-f" ]

        if sdcard_size is not None and sdcard_size != "":
            if sdcard_size == "default":
                avdmanager_command = avdmanager_command + [ "-c", self.ANDROID_EMULATOR_DEFAULT_SDCARD_SIZE ]
            else:
                avdmanager_command = avdmanager_command + [ "-c", sdcard_size ]

        avdmanager_command = avdmanager_command + [ "-n", self.emulator_avd_name, "-k", android_system_image ]

        ## remove empty entries
        avdmanager_command = list(filter(None, avdmanager_command))

        print('echo no | ' + ' '.join(avdmanager_command))
        subprocess.run(avdmanager_command, input=b"no\n", stdout=None, stderr=None).check_returncode()

        # write the additional properties to the avd config file
        avd_home_directory = os.path.join(self.__avd_home_directory, self.emulator_avd_name + ".avd")
        avd_config_file = os.path.join(avd_home_directory, "config.ini")

        for keyval in additional_properties:
            ini_helper_functions.ini_file_helper_add_or_update_key_value(avd_config_file, keyval)

        return 0

    def emulator_start(self, skin="", lang="", country="", show_window=False, keep_user_data=False, additional_cli_opts=[]):
        print("Start the emulator!")

        emulator_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_EMULATOR) ]

        emulator_command = emulator_command + [ "-avd", self.emulator_avd_name ]

        if skin is not None and skin != "":
            emulator_command = emulator_command + [ "-skin", skin ]

        if lang is not None and lang != "":
            emulator_command = emulator_command + [ "-prop", "persist.sys.language=" + lang ]

        if country is not None and country != "":
            emulator_command = emulator_command + [ "-prop", "persist.sys.country=" + country ]

        if not show_window:
            emulator_command = emulator_command + [ self.ANDROID_EMULATOR_SWITCH_NO_WINDOW ]

        if not keep_user_data:
            emulator_command = emulator_command + [ self.ANDROID_EMULATOR_SWITCH_WIPE_DATA ]

        emulator_command = emulator_command + additional_cli_opts

        ## remove empty entries
        emulator_command = list(filter(None, emulator_command))

        print(' '.join(emulator_command))
        proc = subprocess.Popen(emulator_command)

        ## check process after a few seconds
        time.sleep(5)
        rc = proc.poll()

        # still running?
        if rc is None:
            rc = 0

        return rc

    def emulator_wait_for_start(self):
        print("Waiting for the emulator!")

        if self.emulator_avd_name is None or self.emulator_avd_name == '':
            print("It seems that an AVD was never created! Nothing to wait for!")
            return ERROR_CODE_WAIT_NO_AVD_CREATED

        emulator_pid = android_emulator_helper_functions.android_emulator_get_pid_from_avd_name(self.emulator_avd_name)
        if emulator_pid <= 0:
            print("AVD with the name [" + self.emulator_avd_name + "] does not seem to run! Startup failure? Nothing to wait for!")
            return ERROR_CODE_WAIT_AVD_CREATED_BUT_NOT_RUNNING

        emulator_max_startup_time = 300
        emulator_startup_time = 0

        android_emulator_serial = android_emulator_helper_functions.android_emulator_serial_via_port_from_used_avd_name(self.emulator_avd_name)
        if android_emulator_serial is None or android_emulator_serial == '':
            print("Could not detect android_emulator_serial for emulator [PID: '" + str(emulator_pid) + "', AVD: '" + self.emulator_avd_name + "']! Can't properly wait!")
            return ERROR_CODE_WAIT_EMULATOR_RUNNING_UNKNOWN_SERIAL

        while True:
            emulator_wait_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_ADB), "-s", android_emulator_serial, "shell", "getprop", "init.svc.bootanim" ]

            bootanim_output = subprocess.run(emulator_wait_command, stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding).strip()
            if bootanim_output == "stopped":
                return 0

            time.sleep(5)

            if emulator_startup_time > emulator_max_startup_time:
                print("AVD with the name [" + self.emulator_avd_name + "] seems to run, but startup does not finish within " + emulator_max_startup_time + " seconds!")
                break

            time.sleep(1)
            emulator_startup_time = emulator_startup_time + 1

        return ERROR_CODE_WAIT_EMULATOR_RUNNING_STARTUP_TIMEOUT

    def emulator_disable_animations(self):
        print("Disable animations!")

        animations_to_disable = [ 'window_animation_scale', 'transition_animation_scale', 'animator_duration_scale' ]

        if self.emulator_avd_name is None or self.emulator_avd_name == '':
            print("It seems that an AVD was never created! Nothing to do here!")
            return 1

        emulator_pid = android_emulator_helper_functions.android_emulator_get_pid_from_avd_name(self.emulator_avd_name)
        if emulator_pid <= 0:
            print("AVD with the name [" + self.emulator_avd_name + "] does not seem to run. Nothing to do here!")
            return 1

        android_emulator_serial = android_emulator_helper_functions.android_emulator_serial_via_port_from_used_avd_name_single_run(self.emulator_avd_name)
        if android_emulator_serial is None or android_emulator_serial == '':
            print("Could not detect android_emulator_serial for emulator [PID: '" + str(emulator_pid) + "', AVD: '" + self.emulator_avd_name + "']")
            return 1

        # WORKAROUND: Settings provider needs sometimes more time to start, so even after waiting for emulator, the commands could fail
        time.sleep(5)

        rc = 0
        for animation_to_disable in animations_to_disable:
            disable_animation_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_ADB), '-s', android_emulator_serial, 'shell', 'settings', 'put', 'global', animation_to_disable, '0' ]
            rc_last = subprocess.run(disable_animation_command).returncode

            # save first error as rc
            if rc == 0 and rc_last != 0:
                rc = rc_last

        return rc

    def emulator_kill(self):
        print("Stop emulator!")

        if self.emulator_avd_name is None or self.emulator_avd_name == '':
            print("It seems that an AVD was never created! Nothing to do here!")
            return 0

        emulator_pid = android_emulator_helper_functions.android_emulator_get_pid_from_avd_name(self.emulator_avd_name)
        if emulator_pid <= 0:
            print("AVD with the name [" + self.emulator_avd_name + "] does not seem to run. Nothing to do here!")
            return 0

        android_emulator_serial = android_emulator_helper_functions.android_emulator_serial_via_port_from_used_avd_name_single_run(self.emulator_avd_name)
        if android_emulator_serial is None or android_emulator_serial == '':
            print("Could not detect android_emulator_serial for emulator [PID: '" + str(emulator_pid) + "', AVD: '" + self.emulator_avd_name + "']")
            print("  > skip sending 'emu kill' command and proceed with sending kill signals")
        else:
            emulator_kill_command = [ self.__get_full_sdk_path(self.ANDROID_SDK_TOOLS_BIN_ADB), '-s', android_emulator_serial, 'emu', 'kill' ]
            subprocess.run(emulator_kill_command)

        jenkins_android_helper_commons.kill_process_by_pid_with_force_try(emulator_pid, wait_before_kill=10, time_to_force=20)

        return 0

    def run_command_with_android_serial_set(self, command=[], cwd=None):
        android_emulator_serial = android_emulator_helper_functions.android_emulator_serial_via_port_from_used_avd_name(self.emulator_avd_name)
        return subprocess.run(command, cwd=cwd, env=dict(os.environ, ANDROID_SERIAL=android_emulator_serial)).returncode

    def write_license_files(self):
        license_dir = self.__get_full_sdk_path(self.ANDROID_SDK_ROOT_LICENSE_DIR)
        try:
            if not jenkins_android_helper_commons.is_directory(license_dir):
                os.mkdir(license_dir)
        except OSError:
            print("Directory [" + license_dir + "] was not existent and could not be created!!")
            sys.exit(ERROR_CODE_SDK_TOOLS_LICENSE_DIR_DOES_NOT_EXIST_AND_CANT_CREATE)

        with open(self.__get_full_sdk_path(self.ANDROID_SDK_ROOT_LICENSE_STANDARD_FILE), 'w') as licensefile:
            licensefile.write("\n")
            licensefile.write(self.ANDROID_SDK_ROOT_LICENSE_STANDARD_HASH)

        with open(self.__get_full_sdk_path(self.ANDROID_SDK_ROOT_LICENSE_PREVIEW_FILE), 'w') as licensefile:
            licensefile.write("\n")
            licensefile.write(self.ANDROID_SDK_ROOT_LICENSE_PREVIEW_HASH)

    def __get_unique_avd_file_name(self):
        return os.path.join(self.__workspace_directory, self.AVD_NAME_UNIQUE_STORE_FILENAME)

    ## this shall only be called on avd creation, all other calls will reference this name
    def generate_unique_avd_name(self):
        with open(self.__get_unique_avd_file_name(), 'w') as avdnamestore:
            print(uuid.uuid4().hex, file=avdnamestore)

        self.emulator_read_avd_name()

    def emulator_read_avd_name(self):
        try:
            with open(self.__get_unique_avd_file_name()) as f:
                self.emulator_avd_name = f.readline().strip()
        except:
            self.emulator_avd_name = ""

    def info(self):
        print("Current SDK directory: " + self.__sdk_directory)
