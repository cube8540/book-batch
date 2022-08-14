pipeline {
    agent any
    stages {
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
            }
            script {
                version = '`gradle printVersion`'
            }
        }
        stage('Docker build') {
            steps {
                script {
                    app = docker.build("book-batch:$version", "-t book-batch:latest --build-arg V_PROFILE=$ACTIVE_PROFILE .")
                }
            }
        }
    }
}