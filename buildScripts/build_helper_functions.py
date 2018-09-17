#!/usr/bin/env python3

import os
import sys
import re
import subprocess
import datetime

running_on_jenkins = True

CUSTOM_PROPERTIES_FILE = 'emulator_config.ini'
CUSTOM_PROPERTIES_IMAGE_KEY = 'system_image'
CUSTOM_PROPERTIES_AVD_CONFIG_PREFIX = 'prop.'
CUSTOM_PROPERTIES_SCREEN_DENSITY = 'screen.density'
CUSTOM_PROPERTIES_SCREEN_RESOLUTION = 'screen.resolution'
CUSTOM_PROPERTIES_SDCARD_SIZE = 'sdcard.size'
CUSTOM_PROPERTIES_LANGUAGE = 'device.language'

def check_environment():
	## Check for the existance of the android helper
    if not 'SCRIPT_DIR' in os.environ:
        print("Environment-variable SCRIPT_DIR needs to be set to the directory of this script! Abort!")
        sys.exit(2)

    if not 'REPO_DIR' in os.environ:
        print("Environment-variable REPO_DIR needs to be set to the repository root! Abort!")
        sys.exit(2)

def set_jenkins_variables_if_running_locally():
    global running_on_jenkins

    ## if no WORKSPACE is set, set to repository root directory
    if not 'WORKSPACE' in os.environ:
        os.environ['WORKSPACE'] = os.environ["REPO_DIR"]

        print("ENV: Setting unspecified WORKSPACE to repository root [%s]!" % os.environ['WORKSPACE'])
        running_on_jenkins = False

    ## define a build number
    if not 'BUILD_NUMBER' in os.environ:
        os.environ['BUILD_NUMBER'] = datetime.date.today().strftime("%Y%m%d")

        print("ENV: Setting unspecified BUILD_NUMBER to current date [%s]" % os.environ['BUILD_NUMBER'])
        running_on_jenkins = False

    ## do some more checks
    if not 'JENKINS_URL' in os.environ:
        running_on_jenkins = False

    if not running_on_jenkins:
        print("INFO: It seems that the script runs outside Jenkins!")


def setup_android_sdk():
    sdk_installer_cmd = get_jenkins_android_helper_executable('jenkins_android_sdk_installer') + [ '-d' ]

    system_image = get_emulator_image_from_properties()
    if system_image is not None and system_image != "":
        sdk_installer_cmd = sdk_installer_cmd + [ '-s', system_image ]

    print("Installing the SDK: " + " ".join(sdk_installer_cmd))
    subprocess.run( sdk_installer_cmd, check=True )

def check_number_of_parameters(valid_param_count=-1, valid_param_count_min=-1, valid_param_count_max=-1, usage_func=None):
    number_of_parameters = len(sys.argv) - 1

    if valid_param_count >= 0:
        valid_param_count_min = valid_param_count
        valid_param_count_max = valid_param_count

    if (valid_param_count_min >= 0 and number_of_parameters < valid_param_count_min) or (valid_param_count_max >= 0 and number_of_parameters > valid_param_count_max):
        if usage_func is None:
            print("Invalid number of parameters: {}, excpected MIN: {}, MAX: {}".format(number_of_parameters, valid_param_count_min, valid_param_count_max))
        else:
            usage_func()
        sys.exit(1)

def get_relative_gradle_name():
    if sys.platform == "linux" or sys.platform == "darwin":
        return "./gradlew"
    elif sys.platform == "win32" or sys.platform == "cygwin":
        return "gradlew.bat"
    else:
        raise Exception("Unsupported platform: " + sys.platform)

def __get_full_script_path_executable(name, subdir=None):
    path = os.environ['SCRIPT_DIR']
    if subdir is not None and subdir != "":
        path = os.path.join(path, subdir)
    path = os.path.join(path, name)

    ## on windows, check if it has a python shebang and call with python interpreter
    cmd = [ path ]
    if sys.platform == "win32" or sys.platform == "cygwin":
        try:
            with open(path) as f:
                firstline = f.readline()
                if firstline.startswith("#!") and "python" in firstline:
                    cmd = [ 'python' ] + cmd
        except:
            pass

    return cmd

def get_build_scripts_executable(name):
    return __get_full_script_path_executable(name)

def get_jenkins_android_helper_executable(name):
    return __get_full_script_path_executable(name, subdir="jenkins-android-helper")

### Properties handling
def read_custom_emulator_properties():
    global CUSTOM_PROPERTIES_FILE
    global CUSTOM_PROPERTIES_IMAGE_KEY

    properties_dict = {}

    props_filename = os.path.join(os.path.dirname(os.path.realpath(__file__)), CUSTOM_PROPERTIES_FILE)
    with open(props_filename) as properties:
        for prop in properties:
            if prop.strip().startswith('#') or "=" not in prop:
                continue

            prop_split = prop.split("=", maxsplit=1)
            properties_dict[prop_split[0].strip()] = prop_split[1].strip()

    return properties_dict

def get_value_for_properties(key, default=None):
    global CUSTOM_PROPERTIES_FILE
    properties = read_custom_emulator_properties()

    if key in properties:
        return properties[key]
    elif default is not None:
        return default
    else:
        raise Exception('{} needs to be defined in {}'.format(key, CUSTOM_PROPERTIES_FILE))

def get_emulator_image_from_properties():
    global CUSTOM_PROPERTIES_IMAGE_KEY
    return get_value_for_properties(CUSTOM_PROPERTIES_IMAGE_KEY)

def get_screen_density_from_properties():
    global CUSTOM_PROPERTIES_SCREEN_DENSITY
    return get_value_for_properties(CUSTOM_PROPERTIES_SCREEN_DENSITY, '')

def get_screen_resolution_from_properties():
    global CUSTOM_PROPERTIES_SCREEN_RESOLUTION
    return get_value_for_properties(CUSTOM_PROPERTIES_SCREEN_RESOLUTION, '')

def get_sdcard_size_from_properties():
    global CUSTOM_PROPERTIES_SDCARD_SIZE
    return get_value_for_properties(CUSTOM_PROPERTIES_SDCARD_SIZE, '')

def get_device_language_from_properties():
    global CUSTOM_PROPERTIES_LANGUAGE
    return get_value_for_properties(CUSTOM_PROPERTIES_LANGUAGE, '')

# return list with entries in form <key:value>
def get_avd_config_values_from_properties():
    global CUSTOM_PROPERTIES_FILE
    global CUSTOM_PROPERTIES_AVD_CONFIG_PREFIX

    config_properties = []
    properties = read_custom_emulator_properties()
    for prop_key, prop_value in properties.items():
        if prop_key.startswith(CUSTOM_PROPERTIES_AVD_CONFIG_PREFIX):
            config_properties = config_properties + [ re.sub('^' + CUSTOM_PROPERTIES_AVD_CONFIG_PREFIX, '', prop_key) + ':' + prop_value ]

    return config_properties

### Emulator handling
def create_emulator():
    create_emulator_cmd = get_jenkins_android_helper_executable('jenkins_android_emulator_helper') + [ '-C' ]
    avd_properties = get_avd_config_values_from_properties()
    if len(avd_properties) > 0:
        create_emulator_cmd = create_emulator_cmd + [ '-p' ] + avd_properties

    create_emulator_cmd = create_emulator_cmd + [ '-i', get_emulator_image_from_properties() ]

    density = get_screen_density_from_properties()
    if density is not None and density != '':
        create_emulator_cmd = create_emulator_cmd + [ '-s', density ]
    else:
        print('No screen density given!')

    sdcard_size = get_sdcard_size_from_properties()
    if sdcard_size is not None and sdcard_size != '':
        create_emulator_cmd = create_emulator_cmd + [ '-z', sdcard_size ]

    print("Create AVD: " + " ".join(create_emulator_cmd))
    subprocess.run( create_emulator_cmd, check=True )

def start_emulator():
    global running_on_jenkins

    start_emulator_cmd = get_jenkins_android_helper_executable('jenkins_android_emulator_helper') + [ '-S' ]

    resolution = get_screen_resolution_from_properties()
    if resolution is not None and resolution != '':
        start_emulator_cmd = start_emulator_cmd + [ '-r', resolution ]
    else:
        print('No screen resolution given!')

    language = get_device_language_from_properties()
    if language is not None and language != '':
        start_emulator_cmd = start_emulator_cmd + [ '-l', language ]
    else:
        print('No device language given!')

    start_emulator_cmd = start_emulator_cmd + [ '-c', '-gpu swiftshader_indirect -no-boot-anim -noaudio' ]

    if not running_on_jenkins:
        start_emulator_cmd = start_emulator_cmd + [ '-w' ]

    print("Starting the emulator: " + " ".join(start_emulator_cmd))
    subprocess.run( start_emulator_cmd, check=True )

def kill_emulator():
    kill_emulator_cmd = get_jenkins_android_helper_executable('jenkins_android_emulator_helper') + ['-K' ]
    print("Stopping the emulator: " + " ".join(kill_emulator_cmd))
    subprocess.run( kill_emulator_cmd, check=True )

def bring_emulator_in_running_state():
    emulator_state = subprocess.run(get_jenkins_android_helper_executable('jenkins_android_emulator_helper') + [ '-W' ]).returncode

    if emulator_state == 0:
        print("Emulator already running, nothing to do")
        return
    elif emulator_state == 1:
        print("Emulator is not created")
        create_emulator()
        start_emulator()
    elif emulator_state == 2:
        print("Emulator is created, but not started")
        start_emulator()
    elif emulator_state > 2:
        print("Emulator partly running, kill and restart")
        kill_emulator()
        start_emulator()
    else:
        print("Unknown state %d" % emulator_state)

    return subprocess.run(get_jenkins_android_helper_executable('jenkins_android_emulator_helper') + [ '-W' ]).returncode

### Default calls
check_environment()
set_jenkins_variables_if_running_locally()
