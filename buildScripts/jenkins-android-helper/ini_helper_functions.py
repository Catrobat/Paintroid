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

import re, os
from pathlib import Path

def ini_file_helper_check_key_for_value(ini_file_name, ini_key, ini_val_expect):

    if not Path(ini_file_name).is_file():
        return False

    if ini_key is None or ini_key == "":
        return False

    with open(ini_file_name, 'r') as ini_file:
        for ini_file_line in ini_file:
            try:
                ini_val = ini_file_line.split("=", maxsplit=1)[1].strip()
                if ini_val_expect == ini_val:
                    return True
            except:
                pass

    return False

def ini_file_helper_add_or_update_key_value(ini_file_name, ini_key_val_pair):

    if not Path(ini_file_name).is_file():
        return False

    if ini_key_val_pair is None or ini_key_val_pair == "":
        return False

    ini_key_val_pair_splitted = ini_key_val_pair.split(":", maxsplit=1)
    key_to_replace = ini_key_val_pair_splitted[0]
    val_to_replace = ini_key_val_pair_splitted[1]

    ## remove 'old' key
    ini_out_file_name = ini_file_name + ".tmpout"
    with open(ini_file_name, 'r') as ini_file:
        with open(ini_out_file_name, 'w') as out_file:
            for ini_file_line in ini_file:
                if not re.match("^" + key_to_replace + "=", ini_file_line):
                    out_file.write(ini_file_line)

    ## append to end
    with open(ini_out_file_name, 'a') as out_file:
        print(key_to_replace + "=" + val_to_replace, file=out_file)

    os.replace(ini_out_file_name, ini_file_name)

    return True
