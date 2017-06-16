# Jenkins Pipeline Shared libraries

## Purpose

This repo stores shared libraries used in jenkins groovy pipelines.
The libraries are abstract code which is used numerous jobs.

For information on what jenkins pipeline shared libraries are go to:
[Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)

## Usage

To use the shared libraries just define the Git repo:
 * Add global library 'jenkins-pipeline-shared-lib' pointing to github in Jenkins settings (Manage Jenkins > Configure System > Global Pipeline Libraries)
 * Add @Library('jenkins-pipeline-shared-lib') _ into your pipeline definition (more details here)

Jenkinsfile example:

```
    @Library("jenkins-pipeline-shared-lib") _
    ForceStartedBuildsForTheSameJob {
    }
```

## Shared libraries description

### ForceStartedBuildsForTheSameJob

Check if there are any builds which are running for the job in which the code is inserted and aborts them.

### ForceStartedBuildsForTheSamePR

Check if there are any builds which are running for the same Pull Request in which the code is inserted and aborts them.

Note that in order to determine the PR number, the `ghprbPullId` variable is used which is defined after the git plugin has checkout the code.
In the test cases used for this proof of concept, the plugin was [GitHub pull request builder plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin)
