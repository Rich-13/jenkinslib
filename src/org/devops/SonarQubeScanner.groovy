package org.SonarQubeScanner

class SonarQubeScanner {

    static void scan(def script, Map params) {
        script.pipeline {
            agent { kubernetes { inheritFrom 'kanikoamd' } }
            steps {
                script.unstash 'source-code'

                script.container('kanikoamd') {
                    script.withSonarQubeEnv('sonar') {
                        script.withCredentials([string(credentialsId: 'sonar', variable: 'SONAR_TOKEN')]) {
                            script.sh """
                            sonar-scanner \
                                -Dsonar.projectKey=${params['JOB_NAME']} \
                                -Dsonar.projectName='${params['IMAGE_NAMESPACE']}' \
                                -Dsonar.projectVersion=${params['VERSION_TAG']} \
                                -Dsonar.sources=. \
                                -Dsonar.exclusions='**/*_test.go,**/vendor/**' \
                                -Dsonar.language=go \
                                -Dsonar.host.url=http://${params['SONARQUBE_DOMAIN']} \
                                -Dsonar.login=${params['SONAR_TOKEN']} \
                                -Dsonar.projectBaseDir=${params['BUILD_DIRECTORY']}
                            """
                        }
                    }
                }
            }
        }
    }
}
