pipeline {
    agent any
    stages {
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
                script {
                    buildVersion = sh(script: 'gradle -q printVersion', returnStdout: true)
                }
            }
        }
        stage('Docker build') {
            steps {
                script {
                    echo "buildVersion=${buildVersion}"
                    app = docker.build("book-batch:${buildVersion}", "-t book-batch:latest --build-arg V_VERSION=${buildVersion} --build-arg V_PROFILE=$ACTIVE_PROFILE .")
                }
            }
        }
    }
}