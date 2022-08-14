pipeline {
    agent any
    stages {
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
            }
        }
        stage('Docker build') {
            steps {
                script {
                    app = docker.build("book-batch:$BUILD_NUMBER", "-t book-batch:latest", "--build-arg V_PROFILE=$ENV", ".")
                    app.tage()
                }
            }
        }
    }
}