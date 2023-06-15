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
        booleanParam name: 'DEVICE_TESTING', defaultValue: true, description: 'When selected UI-testing runs locally'
        string name: 'CATROID_BRANCH', defaultValue: 'develop', description: 'The branch which to build catroid with, when BUILD_WITH_CATROID is checked.'
        separator(name: "BROWSERSTACK", sectionHeader: "BrowserStack configuration",
                separatorStyle: "border-width: 0",
                sectionHeaderStyle: """
                background-color: #ffff00;
                text-align: center;
                padding: 4px;
                color: #000000;
                font-size: 20px;
                font-weight: normal;
                font-family: 'Orienta', sans-serif;
                letter-spacing: 1px;
                font-style: italic;
                """)
        choice choices: ['AndroidDevices', 'Samsung Galaxy S23-13.0', 'Samsung Galaxy S23 Ultra-13.0', 'Samsung Galaxy S22 Ultra-12.0', 'Samsung Galaxy S22 Plus-12.0', 'Samsung Galaxy S22-12.0', 'Samsung Galaxy S21-12.0', 'Samsung Galaxy S21 Ultra-11.0', 'Samsung Galaxy S21-11.0', 'Samsung Galaxy S21 Plus-11.0', 'Samsung Galaxy S20-10.0', 'Samsung Galaxy S20 Plus-10.0', 'Samsung Galaxy S20 Ultra-10.0', 'Samsung Galaxy M52-11.0', 'Samsung Galaxy M32-11.0', 'Samsung Galaxy A52-11.0', 'Samsung Galaxy Note 20 Ultra-10.0', 'Samsung Galaxy Note 20-10.0', 'Samsung Galaxy A51-10.0', 'Samsung Galaxy A11-10.0', 'Samsung Galaxy S10e-9.0', 'Samsung Galaxy S10 Plus-9.0', 'Samsung Galaxy S10-9.0', 'Samsung Galaxy Note 10 Plus-9.0', 'Samsung Galaxy Note 10-9.0', 'Samsung Galaxy A10-9.0', 'Samsung Galaxy Note 9-8.1', 'Samsung Galaxy J7 Prime-8.1', 'Samsung Galaxy S9 Plus-9.0', 'Samsung Galaxy S9 Plus-8.0', 'Samsung Galaxy S9-8.0', 'Samsung Galaxy Note 8-7.1', 'Samsung Galaxy A8-7.1', 'Samsung Galaxy S8 Plus-7.0', 'Samsung Galaxy S8-7.0', 'Samsung Galaxy S7-6.0', 'Samsung Galaxy S6-5.0', 'Samsung Galaxy Tab S8-12.0', 'Samsung Galaxy Tab S7-11.0', 'Samsung Galaxy Tab S7-10.0', 'Samsung Galaxy Tab S6-9.0', 'Samsung Galaxy Tab S5e-9.0', 'Samsung Galaxy Tab S4-8.1', 'Google Pixel 7 Pro-13.0', 'Google Pixel 7-13.0', 'Google Pixel 6 Pro-13.0', 'Google Pixel 6 Pro-12.0', 'Google Pixel 6-12.0', 'Google Pixel 5-12.0', 'Google Pixel 5-11.0', 'Google Pixel 4-11.0', 'Google Pixel 4 XL-10.0', 'Google Pixel 4-10.0', 'Google Pixel 3-10.0', 'Google Pixel 3a XL-9.0', 'Google Pixel 3a-9.0', 'Google Pixel 3 XL-9.0', 'Google Pixel 3-9.0', 'Google Pixel 2-9.0', 'Google Pixel 2-8.0', 'Google Pixel-7.1', 'Google Nexus 5-4.4', 'OnePlus 9-11.0', 'OnePlus 8-10.0', 'OnePlus 7T-10.0', 'OnePlus 7-9.0', 'OnePlus 6T-9.0', 'Xiaomi Redmi Note 11-11.0', 'Xiaomi Redmi Note 9-10.0', 'Xiaomi Redmi Note 8-9.0', 'Xiaomi Redmi Note 7-9.0', 'Motorola Moto G71 5G-11.0', 'Motorola Moto G9 Play-10.0', 'Motorola Moto G7 Play-9.0', 'Vivo Y21-11.0', 'Vivo Y50-10.0', 'Oppo Reno 6-11.0', 'Oppo A96-11.0', 'Oppo Reno 3 Pro-10.0', 'Huawei P30-9.0'], description: 'Available Android Devices on BrowserStack', name: 'BROWSERSTACK_ANDROID_DEVICES'
        booleanParam name: 'BROWSERSTACK_TESTING', defaultValue: false, description: 'When selected testing runs over Browserstack'
        choice choices: ['1', '2', '3', '4',' 5'], description: 'Number of Shards for running tests on BrowserStack. <a href="https://app-automate.browserstack.com/dashboard/v2/builds/">BrowserStack Dashboard</a>', name: 'BROWSERSTACK_SHARDS'

    }

    agent {
         docker {
            image 'catrobat/catrobat-paintroid:stable'
            args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
            label 'LimitedEmulator'
            alwaysPull true
        }
    }

    tools {nodejs "NodeJS"}

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

        stage('Build Test-APK') {
            steps {
                sh "./gradlew -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleAndroidTest"
                archiveArtifacts 'Paintroid/build/outputs/apk/androidTest/debug/*.apk'
                renameApks("${env.BRANCH_NAME}-${env.BUILD_NUMBER}")
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

        stage('Browserstack testing') {
            when {
                expression { params.BROWSERSTACK_TESTING == true }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'browserstack', passwordVariable: 'BROWSERSTACK_ACCESS_KEY', usernameVariable: 'BROWSERSTACK_USERNAME')]) {
                    script {
                         browserStack('app/build/outputs/apk/debug/', 'Paintroid/build/outputs/apk/androidTest/debug/', 'Paintroid')
                    }
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
                    when {
                        expression { params.DEVICE_TESTING == true }
                    }
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