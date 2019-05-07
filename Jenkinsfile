//"Jenkins Pipeline is a suite of plugins which supports implementing and integrating continuous delivery pipelines into Jenkins. Pipeline provides an extensible set of tools for modeling delivery pipelines "as code" via the Pipeline DSL."
//More information can be found on the Jenkins Documentation page https://jenkins.io/doc/
pipeline {
    agent { label 'linux-large' }
    options {
        buildDiscarder(logRotator(numToKeepStr:'25'))
        disableConcurrentBuilds()
        timestamps()
    }
    triggers {
        /*
          Restrict nightly builds to master branch, all others will be built on change only.
          Note: The BRANCH_NAME will only work with a multi-branch job using the github-branch-source
        */
        cron(BRANCH_NAME == "master" ? "H H(19-21) * * *" : "")
    }
    environment {
        ITESTS = 'tests/itests'
        LARGE_MVN_OPTS = '-Xmx8192M -Xss128M -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC '
        LINUX_MVN_RANDOM = '-Djava.security.egd=file:/dev/./urandom'
        DISABLE_DOWNLOAD_PROGRESS_OPTS = '-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn'
        COVERAGE_EXCLUSIONS = '**/test/**/*,**/itests/**/*,**/*Test*,**/sdk/**/*,**/*.js,**/node_modules/**/*,**/jaxb/**/*,**/wsdl/**/*,**/*.adoc,**/*.txt,**/*.xml'
    }
    stages {
        stage('Setup') {
            steps {
                slackSend color: 'good', message: "STARTED: ${JOB_NAME} ${BUILD_NUMBER} ${BUILD_URL}"
            }
        }
        stage('Build/Test') {
            parallel {
                stage('Linux Full Build') {
                    when { expression { env.CHANGE_ID == null } }
                    steps {
                        withMaven(maven: 'Maven 3.5.2', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS} ${LINUX_MVN_RANDOM}') {
                            // TODO: oconnormi - 01/15/2018 - Re-enable itests once stabilized/fixed
                            sh 'mvn install -B -nsu $DISABLE_DOWNLOAD_PROGRESS_OPTS -pl !$ITESTS'
                        }
                    }
                }
                stage('Windows Full Build') {
                    when { expression { env.CHANGE_ID == null } }
                    agent { label 'server-2016-large' }
                    steps {
                        withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS}') {
                            // TODO: oconnormi - 01/15/2018 - Re-enable itests once stabilized/fixed
                            bat 'mvn install -B -nsu %DISABLE_DOWNLOAD_PROGRESS_OPTS% -pl !%ITESTS%'
                        }
                    }
                }
                stage('Linux PR Build') {
                    when {
                        allOf {
                            expression { env.CHANGE_ID != null }
                            expression { env.CHANGE_TARGET != null }
                        }
                    }
                    steps {
                        withMaven(maven: 'Maven 3.5.2', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS} ${LINUX_MVN_RANDOM}', options: [artifactsPublisher(disabled: true), dependenciesFingerprintPublisher(disabled: true, includeScopeCompile: false, includeScopeProvided: false, includeScopeRuntime: false, includeSnapshotVersions: false)]) {
                            sh 'mvn install -B -Dfindbugs.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -DskipTests=true -nsu $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                            // TODO: oconnormi - 01/15/2018 - Re-enable itests once stabilized/fixed
                            sh 'mvn install -B -Dgib.enabled=true -Dgib.referenceBranch=/refs/remotes/origin/$CHANGE_TARGET -nsu $DISABLE_DOWNLOAD_PROGRESS_OPTS -pl !$ITESTS'
                        }
                    }
                }
                stage('Windows PR Build') {
                    when {
                        allOf {
                            expression { env.CHANGE_ID != null }
                            expression { env.CHANGE_TARGET != null }
                        }
                    }
                    agent { label 'server-2016-large' }
                    steps {
                        withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS}', options: [artifactsPublisher(disabled: true), dependenciesFingerprintPublisher(disabled: true, includeScopeCompile: false, includeScopeProvided: false, includeScopeRuntime: false, includeSnapshotVersions: false)]) {
                            bat 'mvn install -B -Dfindbugs.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -DskipTests=true -nsu %DISABLE_DOWNLOAD_PROGRESS_OPTS%'
                            // TODO: oconnormi - 01/15/2018 - Re-enable itests once stabilized/fixed
                            bat 'mvn install -B -Dgib.enabled=true -Dgib.referenceBranch=/refs/remotes/origin/%CHANGE_TARGET% -nsu %DISABLE_DOWNLOAD_PROGRESS_OPTS% -pl !%ITESTS%'
                        }
                    }
                }
            }
        }
        stage('Security Analysis') {
            // Add additional things like owasp later
            parallel {
                stage('OWASP') {
                    steps {
                       script {
                           withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS} ${LINUX_MVN_RANDOM}') {
                               // If this build is not a pull request, run full owasp scan. Otherwise run incremental scan
                               if (env.CHANGE_ID == null) {
                                   sh 'mvn install -q -B -Powasp -DskipTests=true -DskipStatic=true $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                               } else {
                                   sh 'mvn install -q -B -Powasp -DskipTests=true -DskipStatic=true -Dgib.enabled=true -Dgib.referenceBranch=/refs/remotes/origin/$CHANGE_TARGET $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                               }
                           }

                       }
                    }
                }
            }
        }
        /*
          Deploy stage will only be executed for deployable branches. These include master and any patch branch matching M.m.x format (i.e. 2.10.x, 2.9.x, etc...).
          It will also only deploy in the presence of an environment variable JENKINS_ENV = 'prod'. This can be passed in globally from the jenkins master node settings.
        */
        stage('Deploy') {
            when {
                allOf {
                    expression { env.CHANGE_ID == null }
                    expression { env.BRANCH_NAME ==~ /((?:\d*\.)?\d*\.x|master)/ }
                    environment name: 'JENKINS_ENV', value: 'prod'
                }
            }
            steps{
                withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LINUX_MVN_RANDOM}') {
                    sh 'mvn javadoc:aggregate -B -Dfindbugs.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -DskipTests=true -nsu $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                    sh 'mvn deploy -B -Dfindbugs.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -DskipTests=true -DretryFailedDeploymentCount=10 -nsu $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                }
            }
        }
        stage('Quality Analysis') {
            parallel {
                stage('SonarCloud') {
                    steps {
                        withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LARGE_MVN_OPTS} ${LINUX_MVN_RANDOM}') {
                            withCredentials([string(credentialsId: 'SonarQubeGithubToken', variable: 'SONARQUBE_GITHUB_TOKEN'), string(credentialsId: 'cxbot-sonarcloud', variable: 'SONAR_TOKEN')]) {
                                script {
                                    // If this build is not a pull request, run sonar scan. otherwise run incremental scan
                                    if (env.CHANGE_ID == null) {
                                        sh 'mvn -q -B -Dcheckstyle.skip=true org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN  -Dsonar.organization=cx -Dsonar.projectKey=ac -Dsonar.exclusions=${COVERAGE_EXCLUSIONS} -pl !$ITESTS $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                                    } else {
                                        sh 'mvn -q -B -Dcheckstyle.skip=true org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar -Dsonar.github.pullRequest=${CHANGE_ID} -Dsonar.github.oauth=${SONARQUBE_GITHUB_TOKEN} -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN -Dsonar.organization=cx -Dsonar.projectKey=ac -Dsonar.exclusions=${COVERAGE_EXCLUSIONS} -pl !$ITESTS -Dgib.enabled=true -Dgib.referenceBranch=/refs/remotes/origin/$CHANGE_TARGET $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                                    }
                                }
                            }
                        }
                    }
                }
                // Coverity will be skipped on all PR builds
                stage('Coverity') {
                    agent { label 'linux-medium' }
                    steps {
                        retry(3) {
                            checkout scm
                        }
                        script {
                            if (env.BRANCH_NAME != 'master') {
                                echo "Coverity is only run on master"
                            } else {
                                withMaven(maven: 'Maven 3.5.2', jdk: 'jdk8-latest', globalMavenSettingsConfig: 'default-global-settings', mavenSettingsConfig: 'codice-maven-settings', mavenOpts: '${LINUX_MVN_RANDOM}') {
                                    withCredentials([string(credentialsId: 'admin-console-coverity-token', variable: 'COVERITY_TOKEN')]) {
                                        withEnv(["PATH=${tool 'coverity-linux'}/bin:${env.PATH}"]) {
                                            configFileProvider([configFile(fileId: 'coverity-maven-settings', replaceTokens: true, variable: 'MAVEN_SETTINGS')]) {
                                                echo sh(returnStdout: true, script: 'env')
                                                sh 'cov-build --dir cov-int mvn -DskipTests=true -DskipStatic=true install -B --settings $MAVEN_SETTINGS $DISABLE_DOWNLOAD_PROGRESS_OPTS'
                                                sh 'tar czvf ac.tgz cov-int'
                                                sh 'curl --form token=$COVERITY_TOKEN --form email=cmp-security-team@connexta.com --form file=@ac.tgz --form version="master" --form description="Description: Admin Console CI Build" https://scan.coverity.com/builds?project=connexta%2Fadmin-console'
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            slackSend color: 'good', message: "SUCCESS: ${JOB_NAME} ${BUILD_NUMBER}"
        }
        failure {
            slackSend color: '#ea0017', message: "FAILURE: ${JOB_NAME} ${BUILD_NUMBER}. See the results here: ${BUILD_URL}"
        }
        unstable {
            slackSend color: '#ffb600', message: "UNSTABLE: ${JOB_NAME} ${BUILD_NUMBER}. See the results here: ${BUILD_URL}"
        }
    }
}
