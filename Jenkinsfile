pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building application'
                // Example:
                // sh 'mvn clean package'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application on local machine'
            }
        }
    }
}
