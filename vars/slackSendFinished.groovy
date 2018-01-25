#!/usr/bin/env groovy

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    try {
        if (currentBuild.result == 'FAILURE') {
            color = 'danger'
            status = 'Build Failed: '
        } else {
            status = 'Build Succeeded: '
            color = 'good'
        }
        switch (config.jobtype) {
            case 'GHPRBCAUSE':
                message = "${env.JOB_NAME}, PR: \"${env.ghprbPullTitle}\", Build Number: #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
                break;
            case 'TIMERTRIGGER':
                message = "${env.JOB_NAME} - #${env.BUILD_NUMBER} Started by timer (<${env.BUILD_URL}|Open>)"
                break;
            case 'MANUALTRIGGER':
                // Note that in order to show the build user first you should install:
                // https://wiki.jenkins.io/display/JENKINS/Build+User+Vars+Plugin
                wrap([$class: 'BuildUser']) {
                    message = "${env.JOB_NAME} - #${env.BUILD_NUMBER} Started by user ${env.BUILD_USER} (<${env.BUILD_URL}|Open>)"
                }
                break;
            default:
                message = "${env.JOB_NAME}, Build Number: #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
        }
	def previousResult = currentBuild.previousBuild?.result
        if (previousResult && previousResult != currentBuild.result) {
            slackSend channel: config.channel, color: 'good', message: status + message + " Back to normal!"
        } else {
            slackSend channel: config.channel, color: color, message: status + message
        }
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}
