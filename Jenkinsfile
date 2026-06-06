pipeline {
    agent any

    tools {
        maven 'Maven3' 
    }

    stages {
        stage('Compile') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Test Execution') {
            steps {
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: '**/test-output/**/*', allowEmptyArchive: true
        }
    }
}