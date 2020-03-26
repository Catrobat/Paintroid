#!groovy

class DockerParameters {
    def image = 'catrobat/catrobat-android'
    def imageLabel = 'stable'
    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
    def label = 'LimitedEmulator'
}

def d = new DockerParameters()

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

def useDockerLabelParameter(dockerImage, defaultLabel) {
    def label = env.DOCKER_LABEL?.trim() ? env.DOCKER_LABEL : defaultLabel
    return dockerImage + ':' + label
}

pipeline {
    parameters {
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        string name: 'DOCKER_LABEL', defaultValue: '', description: 'When entered will be used as label for docker catrobat/catroid-android image to build'
    }

    agent {
        docker {
            image useDockerLabelParameter(d.image, d.imageLabel)
            args d.args
            label useDebugLabelParameter(d.label)
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
        issueCommentTrigger('.*test this please.*')
    }

    stages {
        stage('Build Debug-APK') {
            steps {
                sh "./gradlew -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleDebug"
                renameApks("${env.BRANCH_NAME}-${env.BUILD_NUMBER}")
                archiveArtifacts 'app/build/outputs/apk/debug/app-debug*.apk'
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
