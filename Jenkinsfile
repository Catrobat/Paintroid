#!groovy

def reports = 'Paintroid/build/reports'

// place the cobertura xml relative to the source, so that the source can be found
def javaSrc = 'Paintroid/src/main/java'

def debugApk = 'app/build/outputs/apk/debug/app-debug.apk'

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

pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile.jenkins'
            // 'docker build' would normally copy the whole build-dir to the container, changing the
            // docker build directory avoids that overhead
            dir 'docker'
            // Pass the uid and the gid of the current user (jenkins-user) to the Dockerfile, so a
            // corresponding user can be added. This is needed to provide the jenkins user inside
            // the container for the ssh-agent to work.
            // Another way would be to simply map the passwd file, but would spoil additional information
            // Also hand in the group id of kvm to allow using /dev/kvm.
            additionalBuildArgs '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g) --build-arg KVM_GROUP_ID=$(getent group kvm | cut -d: -f3)'
            // Ensure that each executor has its own gradle cache to not affect other builds
            // that run concurrently.
            args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
            label 'LimitedEmulator'
        }
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
        issueCommentTrigger('.*test this please.*')
    }

    stages {
        stage('Build Debug-APK') {
            steps {
                sh './gradlew assembleDebug'
                archiveArtifacts debugApk
                plot csvFileName: 'dexcount.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'Paintroid/build/outputs/dexcount/*.csv', inclusionFlag: 'OFF', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'dexcount'
                plot csvFileName: 'apksize.csv', csvSeries: [[displayTableFlag: false, exclusionValues: 'kilobytes', file: 'Paintroid/build/outputs/apksize/*/*.csv', inclusionFlag: 'INCLUDE_BY_STRING', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'APK Size'
            }
        }

        stage('Static Analysis') {
            steps {
                sh './gradlew pmd checkstyle lint'
            }

            post {
                always {
                    pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
                    checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "$reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
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
                            junitAndCoverage "$reports/jacoco/jacocoTestDebugUnitTestReport/jacocoTestDebugUnitTestReport.xml", 'unit', javaSrc
                        }
                    }
                }

                stage('Device Tests') {
                    steps {
                        sh './gradlew -PenableCoverage -Pjenkins startEmulator adbDisableAnimationsGlobally createDebugCoverageReport'
                    }
                    post {
                        always {
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
