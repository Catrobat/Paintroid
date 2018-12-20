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
			args '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=7G --cpus=3.5'
			label 'LimitedEmulator'
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

	triggers {
		cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
		issueCommentTrigger('.*test this please.*')
	}

	stages {
		stage('Static Analysis') {
			steps {
				sh './gradlew pmd checkstyle lint'
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
				// Run local unit tests
				sh './gradlew -PenableCoverage -Pjenkins jacocoTestDebugUnitTestReport'
				// Convert the JaCoCo coverate to the Cobertura XML file format.
				// This is done since the Jenkins JaCoCo plugin does not work well.
				// See also JENKINS-212 on jira.catrob.at
				sh "./buildScripts/cover2cover.py '$JACOCO_UNIT_XML' '$JAVA_SRC/coverage1.xml'"

				// Run device tests
				sh './gradlew -PenableCoverage -Pjenkins startEmulator adbDisableAnimationsGlobally createDebugCoverageReport'
				// Convert the JaCoCo coverate to the Cobertura XML file format.
				// This is done since the Jenkins JaCoCo plugin does not work well.
				// See also JENKINS-212 on jira.catrob.at
				sh "./buildScripts/cover2cover.py '$JACOCO_XML' '$JAVA_SRC/coverage2.xml'"
			}

			post {
				always {
					junit '**/*TEST*.xml'
					step([$class: 'CoberturaPublisher', autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: "$JAVA_SRC/coverage*.xml", failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false, failNoReports: false])

					sh './gradlew stopEmulator'
					archiveArtifacts 'logcat.txt'

					plot csvFileName: 'dexcount.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'Paintroid/build/outputs/dexcount/*.csv', inclusionFlag: 'OFF', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'dexcount'
					plot csvFileName: 'apksize.csv', csvSeries: [[displayTableFlag: false, exclusionValues: 'kilobytes', file: 'Paintroid/build/outputs/apksize/*/*.csv', inclusionFlag: 'INCLUDE_BY_STRING', url: '']], group: 'APK Stats', numBuilds: '180', style: 'line', title: 'APK Size'
				}
			}
		}

		stage('Build Debug-APK') {
			steps {
				sh './gradlew assembleDebug'
				archiveArtifacts "${env.APK_LOCATION_DEBUG}"
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
