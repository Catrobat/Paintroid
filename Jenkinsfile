#!groovy

pipeline {
	agent none

	environment {
		ANDROID_SDK_ROOT = "/home/catroid/android-sdk-tools"
		ANDROID_SDK_HOME = "/home/catroid"
		ANDROID_EMULATOR_IMAGE = "system-images;android-24;default;x86_64"
		// Needed for compatibiliby to current Jenkins-wide Envs
		// Can be removed, once all builds are migrated to Pipeline
		ANDROID_HOME = ""
		ANDROID_SDK_LOCATION = ""
		ANDROID_NDK = ""
	}

	options {
		timeout(time: 1, unit: 'HOURS')
		timestamps()
	}

	stages {
		stage('Setup Android SDK') {
			steps {
				// Install Android SDK on all possible Android SDK slaves
				lock('update-android-sdk') {
					script {
						onAllAndroidSdkSlaves {
							checkout scm
							sh "jenkins_android_sdk_installer -g ${WORKSPACE}/Paintroid/build.gradle -s '${ANDROID_EMULATOR_IMAGE}'"
						}
					}
				}
			}
		}

		stage('Run Tests') {
			parallel {
				stage('Static Analysis') {
					agent {
						label 'NoDevice'
					}
		
					steps {
						checkout scm

						sh "./gradlew pmd checkstyle lint"
						sh "./gradlew assembleDebug"

						pmd         canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'Paintroid/build/reports/pmd.xml',        unHealthy: ''
						checkstyle  canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'Paintroid/build/reports/checkstyle.xml', unHealthy: ''
						androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'Paintroid/build/reports/lint*.xml',      unHealthy: ''

						archiveArtifacts artifacts: 'Paintroid/build/outputs/apk/*.apk'
					}
				}

				stage('Run Device Tests') {
					agent {
						label 'Emulator'
					}
		
					steps {
						checkout scm
						// create emulator
						sh """
							jenkins_android_emulator_helper -C \
								-P 'hw.ramSize:800' -P 'vm.heapSize:128' \
								-i '${ANDROID_EMULATOR_IMAGE}' \
								-s xhdpi
						"""
						// start emulator
						sh "jenkins_android_emulator_helper -S -r 768x1280 -l en_US -c '-no-boot-anim -noaudio -qemu -m 800 -enable-kvm'"
						// wait for emulator startup
						sh "jenkins_android_emulator_helper -W"
						// Build and Test"
						sh "jenkins_android_cmd_wrapper ./gradlew adbDisableAnimationsGlobally connectedDebugAndroidTest -Pjenkins"
						// stop emulator
						sh "jenkins_android_emulator_helper -K"
					}
		
					post {
						always {
							junit '**/*TEST*.xml'
		
							// kill emulator
							sh "jenkins_android_emulator_helper -K"
						}
					}
				}
			}
		}
	}

	post {
		always {
			// Send notifications with standalone=false
			script {
				sendNotifications false
			}
		}
	}
}
