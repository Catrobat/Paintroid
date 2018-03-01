#!groovy

pipeline {
	agent {
		docker {
			image 'redeamer/jenkins-android-helper:latest'
			args "--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle/:/.gradle -v /var/local/container_shared/android-sdk:/usr/local/android-sdk -v /var/local/container_shared/android-home:/.android -v /var/local/container_shared/emulator_console_auth_token:/.emulator_console_auth_token -v /var/local/container_shared/analytics.settings:/analytics.settings -v /var/local/container_shared/analytics.settings:/analytics.settings"
		}
	}

	environment {
		ANDROID_SDK_ROOT = "/usr/local/android-sdk"
		ANDROID_SDK_HOME = "/"
		// This is important, as we want the keep our gradle cache, but we can't share it between containers
		// the cache could only be shared if the gradle instances could comunicate with each other
		// imho keeping the cache per executor will have the least space impact
		GRADLE_USER_HOME = "/.gradle/${env.EXECUTOR_NUMBER}"
		// Otherwise user.home returns ? for java applications
		JAVA_TOOL_OPTIONS = "-Duser.home=/tmp/"
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
		ANDROID_HOME = "/usr/local/android-sdk"
		ANDROID_SDK_LOCATION = "/usr/local/android-sdk"
		ANDROID_NDK = ""
	}

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
	}

	stages {
		stage('Setup Android SDK') {
			steps {
				// Install Android SDK
				lock("update-android-sdk-on-${env.NODE_NAME}") {
					sh "jenkins_android_sdk_installer -g '${WORKSPACE}/${env.GRADLE_PROJECT_MODULE_NAME}/build.gradle' -s '${ANDROID_EMULATOR_IMAGE}'"
				}
			}
		}

		stage('Static Analysis') {
			steps {
				sh "./gradlew pmd checkstyle lint"
			}

			post {
				always {
					pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
					checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
					androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
				}
			}
		}

		stage('Unit and Device tests') {
			steps {
				// create emulator
				sh "jenkins_android_emulator_helper -C -P 'hw.ramSize:800' -P 'vm.heapSize:128' -i '${ANDROID_EMULATOR_IMAGE}' -s xhdpi"
				// start emulator
				sh "jenkins_android_emulator_helper -S -r 768x1280 -l en_US -c '-gpu swiftshader_indirect -no-boot-anim -noaudio'"
				// wait for emulator startup
				sh "jenkins_android_emulator_helper -W"
				// Run Unit and device tests
				sh "jenkins_android_cmd_wrapper -I ./gradlew adbDisableAnimationsGlobally connectedDebugAndroidTest -Pjenkins"
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

		stage('Build Debug-APK') {
			steps {
				sh "./gradlew assembleDebug"
				stash name: "debug-apk", includes: "${env.APK_LOCATION_DEBUG}"
				archiveArtifacts "${env.APK_LOCATION_DEBUG}"
			}
		}

		stage('Upload to share') {
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
