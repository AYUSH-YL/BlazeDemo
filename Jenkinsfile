pipeline {
    agent any

    stages {
        stage('Docker Environment Verification') {
            steps {
                echo 'Verifying that Jenkins has access to the local Docker engine.'
                bat 'docker --version'
            }
        }

        stage('Docker Build Lifecycle') {
            steps {
                echo 'Compiling image artifact layer from project Dockerfile specifications.'
                bat 'docker build -t testng-framework .'
            }
        }

        stage('Docker Container Regression Run') {
            steps {
                echo 'Spanning up sandboxed container node to launch TestNG framework headless suite.'
                bat 'docker run --name blazedemo-run testng-framework'
            }
        }
        stage('Docker Container Regression Run') {
            steps {
                echo 'Clearing any stale containers from prior failed pipeline runs.'
                bat 'docker rm -f blazedemo-run || ver>nul'

                echo 'Spanning up sandboxed container node to launch TestNG framework headless suite.'
                bat 'docker run --name blazedemo-run testng-framework'
            }
        }
    }

    post {
        always {
            echo 'Extracting isolated test results and screenshots from inside the closed container instance.'
            bat 'docker cp blazedemo-run:/app/screenshots ./screenshots'
            bat 'docker cp blazedemo-run:/app/target/ExtentReport.html ./target/ExtentReport.html'
            bat 'docker cp blazedemo-run:/app/target/surefire-reports ./target/surefire-reports'
            
            echo 'Cleaning up execution workspace nodes.'
            bat 'docker rm blazedemo-run'
            
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'screenshots/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/ExtentReport.html', allowEmptyArchive: true
        }
        success {
            echo 'Success: Containerized pipeline execution passed completely!'
        }
        failure {
            echo 'Error: Regression failure detected during isolated execution.'
        }
    }
}