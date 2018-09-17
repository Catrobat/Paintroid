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

import sys
import re
import subprocess
import time

ANDROID_ADB_PORTS_RANGE_START = 5554
ANDROID_ADB_PORTS_RANGE_END = 5584

## return codes
ERROR_CODE_WAIT_NO_AVD_CREATED = 1
ERROR_CODE_WAIT_AVD_CREATED_BUT_NOT_RUNNING = 2
ERROR_CODE_WAIT_EMULATOR_RUNNING_UNKNOWN_SERIAL = 3
ERROR_CODE_WAIT_EMULATOR_RUNNING_STARTUP_TIMEOUT = 4

def get_open_ports_for_process(pid_to_check):
    open_ports = []

    if pid_to_check <= 0:
        return open_ports

    output = ""
    if sys.platform == "linux" or sys.platform == "darwin":
        header = True
        output = subprocess.run([ 'lsof', '-sTCP:LISTEN', '-i4', '-P', '-p', str(pid_to_check), '-a' ], stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding)
        for entry in output.splitlines():
            if header:
                header = False
                continue

            splitted = re.sub("\s+", " ", entry.strip()).split(' ')
            if len(splitted) >= 9:
                open_ports = open_ports + [ splitted[8].split(':')[1] ]

    elif sys.platform == "win32" or sys.platform == "cygwin":
        output = subprocess.run([ 'netstat', '-aon' ], stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding)
        for entry in output.splitlines():
            splitted = re.sub("\s+", " ", entry.strip()).split(' ')
            if len(splitted) == 5 and splitted[0] == 'TCP' and splitted[4] == str(pid_to_check) and not re.search('\[', splitted[1]):
                open_ports = open_ports + [ splitted[1].split(':')[1] ]

    return open_ports

def android_emulator_get_pid_from_avd_name(avd_name):
    if avd_name is None or avd_name == "":
        return ""

    emulator_pid = 0

    if sys.platform == "linux" or sys.platform == "darwin":
        output = subprocess.run([ 'pgrep', '-f', 'qemu.*-avd ' + avd_name + ''], stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding)
        try:
            emulator_pid = int(output)
        except:
            emulator_pid = 0
    elif sys.platform == "win32" or sys.platform == "cygwin":
        output = subprocess.run([ 'WMIC', 'path', 'win32_process', 'get', 'Caption,Processid,Commandline' ], stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding)
        for entry in output.splitlines():
            entry = entry.strip()
            if re.search('qemu.*-avd ' + avd_name, entry):
                entry = re.sub("^.* ", "", entry).strip()
                try:
                    emulator_pid = int(entry)
                except:
                    emulator_pid = 0

    return emulator_pid

def android_emulator_detect_used_adb_port_by_pid(pid_to_check):
    for pos_port in range(ANDROID_ADB_PORTS_RANGE_START, ANDROID_ADB_PORTS_RANGE_END, 2):
        pos_port2 = pos_port + 1

        ports_used_by_pid = get_open_ports_for_process(pid_to_check)
        if str(pos_port) in ports_used_by_pid and str(pos_port2) in ports_used_by_pid:
            return pos_port

    # not found
    return -1

def android_emulator_serial_via_port_from_used_avd_name_single_run(avd_name):
    if avd_name is None or avd_name == "":
        return ""

    emulator_pid = android_emulator_get_pid_from_avd_name(avd_name)
    if emulator_pid <= 0:
        return ""

    android_adb_port_even = android_emulator_detect_used_adb_port_by_pid(emulator_pid)

    if android_adb_port_even >= 0:
        return "emulator-" + str(android_adb_port_even)
    else:
        return ""


def android_emulator_serial_via_port_from_used_avd_name(avd_name):
    if avd_name is None or avd_name == "":
        return ""

    RETRIES = 10
    for i in range(1, RETRIES):
        emulator_serial = android_emulator_serial_via_port_from_used_avd_name_single_run(avd_name)
        if emulator_serial is not None and emulator_serial != "":
            return emulator_serial

        time.sleep(3)

    return ""
