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

import os
import sys
import shutil
import urllib.request
import time
import subprocess
from hashlib import sha256
from zipfile import ZipFile
from pathlib import Path

def remove_file_or_dir(fn):
    p = Path(fn)
    if p.is_dir():
        shutil.rmtree(fn)
    if p.is_file():
        p.unlink()

def download_file(url, dest):
    dwnldfile = urllib.request.urlopen(url)
    with open(dest,'wb') as output:
        output.write(dwnldfile.read())

def is_directory(fn):
    p = Path(fn)
    return p.is_dir()

def is_file(fn):
    p = Path(fn)
    return p.is_file()

def sha256sum(fn):
    f = open(fn, 'rb')
    return sha256(f.read()).hexdigest()

def unzip(zipfn, dest):
    with ZipFile(zipfn, 'r') as zf:
        for info in zf.infolist():
            zf.extract(info.filename, path=dest)
            out_path = os.path.join(dest, info.filename)
            perm = info.external_attr >> 16
            os.chmod(out_path, perm)

def find_file_in_subtree(root, fn, depth):
    found_file = ""
    for dirpath, dirname, filename in os.walk(root):
        # distance between the root and the current dir, + 1 for the file itself
        distance = len(Path(dirpath).parts) - len(Path(root).parts) + 1
        if fn in filename and distance == depth:
            found_file = os.path.join(dirpath, fn)
            break

    return found_file

def split_string_and_get_part(string, delimiter, index, default=""):
    part = default
    try:
        part = string.split(delimiter)[index]
    except:
        pass

    return part

def kill_process_by_pid_with_force_try(pid, wait_before_kill=0, time_to_force=10):
    wait_time = 0
    while True:
        if not is_process_running(pid):
            return

        if wait_time == wait_before_kill:
            kill_process_by_pid(pid)

        # send kill after 15 seconds, and exit
        if wait_time == time_to_force:
            kill_process_by_pid(pid, force=True)
            break

        time.sleep(1)

        wait_time = wait_time + 1

def is_process_running(pid):
    if os.name == "posix":
        try:
            os.kill(pid, 0)
        except OSError:
            return False
        else:
            return True
    elif os.name == "nt":
        return len(subprocess.run([ "tasklist", "/FI", "PID eq " + str(pid) ], stdout=subprocess.PIPE).stdout.decode(sys.stdout.encoding).strip().splitlines()) > 1
    else:
        raise Exception("Unsupported platform: " + os.name)

def kill_process_by_pid(pid, force=False):
    if not is_process_running(pid):
        return

    if os.name == "posix":
        if force:
            subprocess.run([ 'kill', '-9', str(pid) ])
        else:
            subprocess.run([ 'kill', str(pid) ])
    elif os.name == "nt":
        if force:
            subprocess.run([ 'Taskkill', '/PID', str(pid), '/F' ])
        else:
            subprocess.run([ 'Taskkill', '/PID', str(pid) ])
    else:
        raise Exception("Unsupported platform: " + os.name)
