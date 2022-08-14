pipeline {
    agent any
    stages {
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
                sh 'buildVersion=$(gradle -q printVersion)'
            }
        }
        stage('Docker build') {
            steps {
                script {
                    app = docker.build("book-batch:$buildVersion", "-t book-batch:latest --build-arg V_PROFILE=$ACTIVE_PROFILE .")
                }
            }
        }
    }
}