#!groovy

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
            args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle'
        }
    }

    environment {
        //////// Build specific variables ////////
        //////////// May be edited by the developer on changing the build steps
        // modulename
        GRADLE_PROJECT_MODULE_NAME = "Paintroid"
        GRADLE_APP_MODULE_NAME = "app"

        // APK build output locations
        APK_LOCATION_DEBUG = "${env.GRADLE_APP_MODULE_NAME}/build/outputs/apk/debug/app-debug.apk"

        // Code coverage
        JACOCO_XML = "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/coverage/debug/report.xml"
        JACOCO_UNIT_XML = "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/jacoco/jacocoTestDebugUnitTestReport/jacocoTestDebugUnitTestReport.xml"

        // place the cobertura xml relative to the source, so that the source can be found
        JAVA_SRC = "${env.GRADLE_PROJECT_MODULE_NAME}/src/main/java"
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    stages {
        stage('Prepare build') {
            steps {
                script {
                    currentBuild.displayName = "#${env.BUILD_NUMBER} | ${env.gitBranch}"
                }
            }
        }

        stage('Build signed APK') {
            steps {
                // Build, zipalign and sign releasable APK
                withCredentials([file(credentialsId: 'a925b6e8-b3c6-407e-8cad-65886e330037', variable: 'SIGNING_KEYSTORE')]) {
                    script {
                        sh '''
                            set +x
                            ./gradlew assembleSignedRelease \
                            -PsigningKeystore=${SIGNING_KEYSTORE} \
                            -PsigningKeystorePassword=$signingKeystorePassword \
                            -PsigningKeyAlias=$signingKeyAlias \
                            -PsigningKeyPassword=$signingKeyPassword
                        '''
                    }
                }
                archiveArtifacts artifacts: 'app/build/outputs/apk/signedRelease/app-signedRelease.apk', fingerprint: true
            }
        }

        stage('Approve upload') {
            options {
                timeout(time: 60, unit: 'MINUTES')
            }
            steps {
                script {
                    env.APPROVE_UPLOAD_APK = input message: 'User input required',
                            parameters: [choice(name: 'Upload', choices: 'no\nyes',
                                    description: 'Do you want to upload this APK to Alpha Channel on Google Play?')]
                }
            }
        }

        stage('Upload AKP to Alpha') {
            when {
                environment name: 'APPROVE_UPLOAD_APK', value: 'yes'
            }
            steps {
                script {
                    sh 'fastlane android upload_APK_Paintroid'
                }
            }
        }

        stage('Bintray upload') {
            when {
                environment name: 'APPROVE_UPLOAD_APK', value: 'yes'
            }
            steps {
                script {
                    sh '''
                            set +x
                            ./gradlew bintrayUpload \
                            -PbintrayUser=$bintrayUser \
                            -PbintrayKey=$bintrayKey \
                            -PdryRun=false
                    '''
                }
            }
        }
    }

    post {
        always {
            // clean workspace
            deleteDir()
        }
    }
}
