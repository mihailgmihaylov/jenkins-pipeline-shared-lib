def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    try {
        stage ('Check for previously started builds for the same PR') {
            def jobname = env.JOB_NAME
            def buildnum = env.BUILD_NUMBER.toInteger()

            def job = Jenkins.instance.getItemByFullName(jobname)
            for (build in job.builds) {
                if (!build.isBuilding()) { continue; }
                if (buildnum == build.getNumber().toInteger()) { continue; println "equals" } 
                if (ghprbPullId == build.environment.get("ghprbPullId")) { build.doStop(); }
            }
        }
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}
