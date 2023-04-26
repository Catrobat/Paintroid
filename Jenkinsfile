#!groovy

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
    // See also JENKINS-212 on jira.catrob.at
    sh "./buildScripts/cover2cover.py '$jacocoXmlFile' '$coverageFile'"
}

def useDebugLabelParameter(defaultLabel) {
    return env.DEBUG_LABEL?.trim() ? env.DEBUG_LABEL : defaultLabel
}

pipeline {
    parameters {
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        booleanParam name: 'BUILD_WITH_CATROID', defaultValue: false, description: 'When checked then the current Paintroid build will be built with the current develop branch of Catroid'
        string name: 'CATROID_BRANCH', defaultValue: 'develop', description: 'The branch which to build catroid with, when BUILD_WITH_CATROID is checked.'
    }

    agent {
         docker {
            image 'catrobat/catrobat-paintroid:stable'
            args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
            label 'LimitedEmulator'
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
                    sh "rm -f catroid/src/main/libs/*.aar"
                    sh "mv -f ../colorpicker/build/outputs/aar/colorpicker-debug.aar catroid/src/main/libs/colorpicker-LOCAL.aar"
                    sh "mv -f ../Paintroid/build/outputs/aar/Paintroid-debug.aar catroid/src/main/libs/Paintroid-LOCAL.aar"
                }
                renameApks("${env.BRANCH_NAME}-${env.BUILD_NUMBER}")
                dir('Catroid') {
                    archiveArtifacts "catroid/src/main/libs/*.aar"
                    sh "./gradlew assembleCatroidDebug"
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
                        sh "echo no | avdmanager create avd --force --name android28 --package 'system-images;android-28;default;x86_64'"
                        sh "/home/user/android/sdk/emulator/emulator -no-window -no-boot-anim -noaudio -avd android28 > /dev/null 2>&1 &"
                        sh './gradlew -PenableCoverage -Pjenkins -Pemulator=android28 -Pci createDebugCoverageReport -i'
                    }
                    post {
                        always {
                            sh '/home/user/android/sdk/platform-tools/adb logcat -d > logcat.txt'
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
            step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])
        }
        changed {
            notifyChat()
        }
    }
}
