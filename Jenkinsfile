pipeline {
    agent any

    tools {
        maven 'Maven3' // Matches your VM global tool configuration name
    }

    stages {
        stage('Compile') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Test Execution') {
            steps {
                // Runs your testng.xml file via Maven test lifecycle
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            // Archives your TestNG report dashboards automatically in Jenkins
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: '**/test-output/**/*', allowEmptyArchive: true
        }
    }
}