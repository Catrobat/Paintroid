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
			additionalBuildArgs '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g)'
			args "--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle/:/.gradle -v /var/local/container_shared/android-sdk-paintroid:/usr/local/android-sdk -v /var/local/container_shared/android-home:/.android -v /var/local/container_shared/emulator_console_auth_token:/.emulator_console_auth_token -v /var/local/container_shared/analytics.settings:/analytics.settings -v /var/local/container_shared/analytics.settings:/analytics.settings"
		}
	}

	environment {
		ANDROID_SDK_ROOT = "/usr/local/android-sdk"
		// Deprecated: Still used by the used gradle version, once gradle respects ANDROID_SDK_ROOT, this can be removed
		ANDROID_HOME = "/usr/local/android-sdk"
		ANDROID_SDK_HOME = "/"
		// This is important, as we want the keep our gradle cache, but we can't share it between containers
		// the cache could only be shared if the gradle instances could comunicate with each other
		// imho keeping the cache per executor will have the least space impact
		GRADLE_USER_HOME = "/.gradle/${env.EXECUTOR_NUMBER}"
		// Otherwise user.home returns ? for java applications
		JAVA_TOOL_OPTIONS = "-Duser.home=/tmp/"

		// modulename
		GRADLE_PROJECT_MODULE_NAME = "Paintroid"

		// APK build output locations
		APK_LOCATION_DEBUG = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/debug/Paintroid-debug.apk"

		// share.catrob.at
		CATROBAT_SHARE_UPLOAD_BRANCH = "develop"
		CATROBAT_SHARE_APK_NAME = "org.catrobat.paintroid_debug_${env.CATROBAT_SHARE_UPLOAD_BRANCH}_latest.apk"

		// set to any value to debug jenkins_android* scripts
		ANDROID_EMULATOR_HELPER_DEBUG = ""
		// Needed for compatibiliby to current Jenkins-wide Envs
		// Can be removed, once all builds are migrated to Pipeline
		ANDROID_SDK_LOCATION = "/usr/local/android-sdk"
		ANDROID_NDK = ""

		PYTHONUNBUFFERED = "true"
		JACOCO_XML = "Paintroid/build/reports/coverage/debug/report.xml"

		// place the cobertura xml relative to the source, so that the source can be found
		COBERTURA_XML = "Paintroid/src/main/java/coverage.xml"
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
					sh "./buildScripts/build_step_install_android_sdk"
				}
			}
		}

		stage('Static Analysis') {
			steps {
				sh "./buildScripts/build_step_run_static_analysis"
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
				// Run Unit and device tests
				sh "./buildScripts/build_step_run_tests_on_emulator__all_tests"

				// Convert the JaCoCo coverate to the Cobertura XML file format.
				// This is done since the Jenkins JaCoCo plugin does not work well.
				// See also JENKINS-212 on jira.catrob.at
				sh "if [ -e '$JACOCO_XML' ]; then ./buildScripts/cover2cover.py $JACOCO_XML > $COBERTURA_XML; fi"
			}

			post {
				always {
					junit '**/*TEST*.xml'
					step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: COBERTURA_XML, failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false, failNoReports: false])

					// stop/kill emulator
					sh "./buildScripts/build_helper_stop_emulator"
				}
			}
		}

		stage('Build Debug-APK') {
			steps {
				sh "./buildScripts/build_step_create_debug_apk"
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
			step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])

			// Send notifications with standalone=false
			script {
				sendNotifications false
			}
		}
	}
}
