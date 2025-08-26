#!groovy

class DockerParameters {

    // 'docker build' would normally copy the whole build-dir to the container, changing the
    // docker build directory avoids that overhead
    def dir = 'docker'
    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
    def label = 'LimitedEmulator'
    def image = 'floriankanduth/devops-846:bullseye'

}

def dockerParameters = new DockerParameters()

def startEmulator(String android_version, String stageName) {
    sh "whoami"
    sh 'adb start-server'
    // creates a new avd, and if it already exists it does nothing.
    sh "echo no | avdmanager create avd --force --name android${android_version}" + " --package 'system-images;android-${android_version};default;x86_64'"
    sh "/home/user/android/sdk/emulator/emulator -no-window -no-boot-anim -noaudio -avd android${android_version} > ${stageName}_emulator.log 2>&1 &"
}

def waitForEmulatorAndPressWakeUpKey() {
    sh 'adb devices'
    // sh 'timeout 5m adb wait-for-device'
    sh '''#!/bin/bash
adb devices
timeout 5m adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1;
done'
echo "Emulator started"
'''
    sh '''
        adb shell settings put global window_animation_scale 0 &
        adb shell settings put global transition_animation_scale 0 &
        adb shell settings put global animator_duration_scale 0 &
    '''

    // In case the device went to sleep
    sh 'adb shell input keyevent KEYCODE_WAKEUP'
}

def reports = 'Paintroid/build/reports'

// place the cobertura xml relative to the source, so that the source can be found
def javaSrc = 'Paintroid/src/main/java'

def junitAndCoverage(String jacocoXmlFile, String coverageName, String javaSrcLocation) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    String coverageFile = "$javaSrcLocation/coverage_${coverageName}.xml"
    // Convert the JaCoCo coverate to the Cobertura XML file format.
    // This is done since the Jenkins JaCoCo plugin does not work well.
    sh "./buildScripts/cover2cover.py '$jacocoXmlFile' '$coverageFile'"
}

def useDebugLabelParameter(defaultLabel) {
    return env.DEBUG_LABEL?.trim() ? env.DEBUG_LABEL : defaultLabel
}

pipeline {
    environment {
        ANDROID_VERSION = 28
        ADB_INSTALL_TIMEOUT = 60
    }

    parameters {
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        booleanParam name: 'BUILD_WITH_CATROID', defaultValue: false, description: 'When checked then the current Paintroid build will be built with the current develop branch of Catroid'
        string name: 'CATROID_BRANCH', defaultValue: 'develop', description: 'The branch which to build catroid with, when BUILD_WITH_CATROID is checked.'
    }

    agent {
        docker {
            image dockerParameters.image
            args dockerParameters.args
            label dockerParameters.label
            alwaysPull true
        }
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
        issueCommentTrigger('.*(test this please|please test this).*')
    }

    stages {
        stage('Build Debug-APK') {
            steps {
                sh "java --version"
                sh "whoami"
                sh "./gradlew -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleDebug"
                archiveArtifacts 'app/build/outputs/apk/debug/paintroid-debug*.apk'
                plot csvFileName: 'dexcount.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'Paintroid/build/outputs/dexcount/*.csv', inclusionFlag: 'OFF', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'dexcount'
            }
        }

        stage('Build with Catroid') {
            when {
                expression {
                    params.BUILD_WITH_CATROID
                }
            }

            steps {
                sh './gradlew publishToMavenLocal -Psnapshot'
                sh 'rm -rf Catroid; mkdir Catroid'
                dir('Catroid') {
                    git branch: params.CATROID_BRANCH, url: 'https://github.com/Catrobat/Catroid.git'
                    sh 'rm -f catroid/src/main/libs/*.aar'
                    sh 'mv -f ../colorpicker/build/outputs/aar/colorpicker-debug.aar catroid/src/main/libs/colorpicker-LOCAL.aar'
                    sh 'mv -f ../Paintroid/build/outputs/aar/Paintroid-debug.aar catroid/src/main/libs/Paintroid-LOCAL.aar'
                }
                renameApks("${env.BRANCH_NAME}-${env.BUILD_NUMBER}")
                dir('Catroid') {
                    archiveArtifacts 'catroid/src/main/libs/*.aar'
                    sh './gradlew assembleCatroidDebug'
                    archiveArtifacts 'catroid/build/outputs/apk/catroid/debug/catroid-catroid-debug.apk'
                }
            }
        }

        stage('Static Analysis') {
            steps {
                sh './gradlew pmd checkstyle lint detekt'
            }

            post {
                always {
                    recordIssues aggregatingResults: true, enabledForFailure: true, qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
                            tools: [androidLintParser(pattern: "$reports/lint*.xml"),
                                    checkStyle(pattern: "$reports/checkstyle.xml"),
                                    pmdParser(pattern: "$reports/pmd.xml"),
                                    detekt(pattern: "$reports/detekt/detekt.xml")]
                }
            }
        }

        stage('Tests') {
            stages {
                stage('Unit Tests') {
                    steps {
                        sh './gradlew -PenableCoverage -Pjenkins jacocoTestDebugUnitTestReport'
                    }
                    post {
                        always {
                            junitAndCoverage "$reports/jacoco/jacocoTestDebugUnitTestReport/jacoco.xml", 'unit', javaSrc
                        }
                    }
                }

                stage('Device Tests') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            startEmulator(ANDROID_VERSION, 'device_tests')
                            waitForEmulatorAndPressWakeUpKey()
                            sh "./gradlew disableAnimations -PenableCoverage -Pjenkins -Pemulator=android${android_version} -Pci createDebugCoverageReport -i"
                        }
                    }
                    post {
                        always {
                            archiveArtifacts "device_tests_emulator.log"
                            // sh '/home/user/android/sdk/platform-tools/adb logcat -d > logcat.txt'
                            sh './gradlew stopEmulator'
                            junitAndCoverage "$reports/coverage/debug/report.xml", 'device', javaSrc
                            archiveArtifacts 'logcat.txt'
                        }
                    }
                }
            }

            post {
                always {
                    step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: "$javaSrc/coverage*.xml", failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false, failNoReports: false])
                }
            }
        }
    }

    post {
        always {
            steps {
                step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])                
            }
        }
        changed {
            notifyChat()
        }
    }
}
