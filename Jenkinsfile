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

def useDebugLabelParameter(defaultLabel){
    return env.DEBUG_LABEL?.trim() ? env.DEBUG_LABEL : defaultLabel
}

pipeline {
    parameters {
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        string name: 'BUILD_WITH_CATROID', defaultValue: 'no', description: 'When set to \'yes\' the the current Paintroid build will be build with the current develop Branch of Catroid'
    }

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
            args '--device /dev/kvm:/dev/kvm -m=6.5G'
            label useDebugLabelParameter('LimitedEmulator')
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
                environment name: 'BUILD_WITH_CATROID', value: 'yes'
            }
            steps {
                sh './gradlew publishToMavenLocal -Psnapshot'
                sh 'rm -rf Catroid; mkdir Catroid'
                dir('Catroid') {
                    git branch: 'develop', url: 'https://github.com/Catrobat/Catroid.git'
                    sh "./gradlew -PpaintroidLocal assembleCatroidDebug"
                    archiveArtifacts 'catroid/build/outputs/apk/catroid/debug/catroid-catroid-debug.apk'
                }
            }
        }

        stage('Static Analysis') {
            steps {
                sh './gradlew pmd checkstyle lint'
            }

            post {
                always {
                    recordIssues aggregatingResults: true, enabledForFailure: true, qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
                                 tools: [androidLintParser(pattern: "$reports/lint*.xml"),
                                         checkStyle(pattern: "$reports/checkstyle.xml"),
                                         pmdParser(pattern: "$reports/pmd.xml")]
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
