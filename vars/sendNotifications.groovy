#!/usr/bin/env groovy

/**
 * Send notifications based on build status string
 * This is a copy of the official example:
 * https://jenkins.io/blog/2017/02/15/declarative-notifications/
 * The syntax is switched to scripted.
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    try {

	// build status of null means successful
	buildStatus = config.buildstatus ?: 'SUCCESS'

	// Default values
	def colorName = 'RED'
	def colorCode = '#FF0000'
	def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
	def summary = "${subject} (${env.BUILD_URL})"
	def details = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
	  <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

	// Override default values based on build status
	if (buildStatus == 'STARTED') {
	  color = 'YELLOW'
	  colorCode = '#FFFF00'
	} else if (buildStatus == 'SUCCESS') {
	  color = 'GREEN'
	  colorCode = '#00FF00'
	} else {
	  color = 'RED'
	  colorCode = '#FF0000'
	}

	// Send notifications
	slackSend (channel: '#testing', color: colorCode, message: summary)
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}
