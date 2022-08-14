pipeline {
    agent any
    stages {
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
                script {
                    env.BOOK_BATCH_VERSION = 'gradle -q printVersion'
                }
            }
        }
        stage('Docker build') {
            steps {
                script {
                    app = docker.build("book-batch:$BOOK_BATCH_VERSION", "-t book-batch:latest --build-arg V_PROFILE=$ACTIVE_PROFILE .")
                }
            }
        }
    }
}