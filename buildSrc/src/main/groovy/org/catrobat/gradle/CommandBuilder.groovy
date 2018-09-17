/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2018 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.gradle

import groovy.transform.TypeChecked
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.GradleScriptException

@TypeChecked
class CommandBuilder {
    File exe
    File workingDirectory
    Map<String, String> environment
    List arguments = []
    String input
    boolean verbose = false

    CommandBuilder(File exe) {
        this.exe = exe
    }

    CommandBuilder(File exe, String winEnding) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            this.exe = new File(exe.absolutePath + winEnding)
        } else {
            this.exe = exe
        }
    }

    CommandBuilder directory(File workingDirectory) {
        this.workingDirectory = workingDirectory
        this
    }

    CommandBuilder environment(Map<String, String> environment) {
        this.environment = environment
        this
    }

    CommandBuilder addArguments(List arguments ) {
        this.arguments += arguments
        this
    }

    CommandBuilder addOptionalArguments(def shouldAdd, List arguments) {
        if (shouldAdd) {
            this.arguments += arguments
        }
        this
    }

    CommandBuilder verbose() {
        verbose = true
        this
    }

    CommandBuilder input(String input) {
        this.input = input
        this
    }

    String execute(long timeoutSecs=30) {
        def proc = executeInternal()

        def stdout = verbose ? new PrintStreamAndStringBuilder(System.out) : new ByteArrayOutputStream()
        def stderr = verbose ? new PrintStreamAndStringBuilder(System.err) : new ByteArrayOutputStream()

        proc.consumeProcessOutput(stdout, stderr)
        proc.waitForOrKill(timeoutSecs * 1000)

        if (proc.exitValue()) {
            throw new GradleScriptException("Failed to execute ${commandLine().join(' ')} " +
                    "exit code ${proc.exitValue()} Err:\n$stdout\nText:\n$stderr", null)
        }

        stdout.toString()
    }

    /**
     * Starts a job in the background.
     *
     * When verbose is activated the process output will be forwarded to stdout/stderr.
     * @note You have to handle the process output yourself if you do not activate verbose.
     *       Thus you have to call consumeProcessOutput or waitForProcessOutput.
     *       Otherwise the internal buffers can be filled, which will lead to the process failing!
     */
    Process executeAsynchronously() {
        def proc = executeInternal()
        if (verbose) {
            proc.consumeProcessOutput(new PrintStream(System.out, true), new PrintStream(System.err, true))
        }

        proc
    }

    List commandLine() {
        [exe] + arguments
    }

    private Process executeInternal() {
        def cmd = commandLine()
        if (verbose) {
            println("Executing: ${cmd.join(' ')}")
        }

        def proc = cmd.execute(environment?.collect { k, v -> "$k=$v"}, workingDirectory)

        if (input) {
            proc << input
        }

        proc
    }
}
