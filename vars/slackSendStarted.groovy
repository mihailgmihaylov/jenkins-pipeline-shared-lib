def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    try {
        switch (config.jobtype) {
            case 'GHPRBCAUSE':
                message = "${env.JOB_NAME} - #${env.BUILD_NUMBER} \"${env.ghprbPullDescription}\", PR: \"${env.ghprbPullTitle}\" (<${env.BUILD_URL}|Open>)"
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
                message = "Started: ${env.JOB_NAME}, Build Number: #${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
        }
	if (config.buildstatus == 'FAILURE') {
            slackSend channel: config.channel, color: 'danger', message: message
	} else {
            slackSend channel: config.channel, color: 'good', message: message
	}
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}
