pipeline {
    agent any

    tools {
        // Must match the exact name given to your Maven installation in Jenkins Global Tool Configuration
        maven 'Maven3' 
    }

    environment {
        // Ensures clean command execution flags across operating systems
        MAVEN_OPTS = '-Dfile.encoding=UTF-8'
    }

    stages {
        stage('Environment Verification') {
            steps {
                echo 'Checking software environment baselines...'
                bat 'mvn --version'
                bat 'java -version'
            }
        }

        stage('Compile Code') {
            steps {
                echo 'Compiling project target definitions...'
                bat 'mvn clean compile'
            }
        }

        stage('Regression Suite Execution') {
            steps {
                echo 'Launching Data-Driven Selenium Tests via testng.xml...'
                // runs target test phase and continues if assertion mismatches surface
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            echo 'Processing test artifacts and compiling metrics...'
            
            // 1. Parse JUnit/TestNG target XML results into the native Jenkins UI dashboard trends
            junit '**/target/surefire-reports/*.xml'
            
            // 2. Archive your manual checkpoint images and ExtentReport dashboard files for easy download
            archiveArtifacts artifacts: 'screenshots/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/ExtentReport.html', allowEmptyArchive: true
        }
        success {
            echo '✨ All test steps passed successfully! Booking channel cycle verified.'
        }
        failure {
            echo '❌ Regression anomalies detected. Review archived reports and screenshots.'
        }
    }
}