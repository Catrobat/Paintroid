#!groovy

pipeline {
	agent none

	environment {
		ANDROID_SDK_ROOT = "/home/catroid/android-sdk-tools"
		ANDROID_SDK_HOME = "/home/catroid"
		ANDROID_EMULATOR_IMAGE = "system-images;android-24;default;x86_64"

		// modulename
		GRADLE_PROJECT_MODULE_NAME = "Paintroid"

		// APK build output locations
		APK_LOCATION_DEBUG = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/Paintroid-debug.apk"

		// share.catrob.at
		CATROBAT_SHARE_UPLOAD_BRANCH = "develop"
		CATROBAT_SHARE_APK_NAME = "org.catrobat.paintroid_debug_${env.CATROBAT_SHARE_UPLOAD_BRANCH}_latest.apk"

		// set to any value to debug jenkins_android* scripts
		ANDROID_EMULATOR_HELPER_DEBUG = ""
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
							sh "jenkins_android_sdk_installer -g ${WORKSPACE}/${env.GRADLE_PROJECT_MODULE_NAME}/build.gradle -s '${ANDROID_EMULATOR_IMAGE}'"
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

						pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
						checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
						androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
					}
				}

				stage('Unit and Device tests') {
					agent {
						label 'Emulator'
					}

					steps {
						checkout scm

						wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
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
							// Run Unit and device tests
							sh "jenkins_android_cmd_wrapper -I ./gradlew adbDisableAnimationsGlobally connectedDebugAndroidTest -Pjenkins"
							// stop emulator
							sh "jenkins_android_emulator_helper -K"
						}
					}

					post {
						always {
							junit '**/*TEST*.xml'

							// kill emulator
							sh "jenkins_android_emulator_helper -K"
						}
					}
				}

				stage('Build APK') {
					agent {
						label 'NoDevice'
					}

					steps {
						checkout scm

						sh "./gradlew assembleDebug"
						stash name: "debug-apk", includes: "${env.APK_LOCATION_DEBUG}"
						archiveArtifacts "${env.APK_LOCATION_DEBUG}"
					}
				}
			}
		}

		stage('Upload to share') {
			agent {
				label 'NoDevice'
			}

			when {
				branch "${env.CATROBAT_SHARE_UPLOAD_BRANCH}"
			}

			steps {
				unstash "debug-apk"
				script {
					uploadFileToShare "${env.APK_LOCATION_DEBUG}", "${env.CATROBAT_SHARE_APK_NAME}"
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
