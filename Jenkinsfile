pipeline {
    agent any
    stages {
        stage('Setup parameters') {
            steps {
                script {
                    properties([
                        parameters([
                            choice(
                                choices: ['prod', 'dev', 'test-dev', 'test', 'local'],
                                name: 'ENV'
                            )
                        ])
                    ])
                }
            }
        }
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
            }
        }
        stage('Docker build') {
            steps {
                script {
                    app = docker.build("book-batch:$BUILD_NUMBER", "--build-arg V_PROFILE=$ENV")
                }
            }
        }
    }
}