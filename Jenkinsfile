pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'dockerhub'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/BWNguyenVu/swd392-be.git'
            }
        }

        stage('Build with Maven') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Images') {
            when {
                branch 'main'
            }
            steps {
                script {
                    // Build Docker images
                    sh 'docker build -t bwnguyenvu/swd392-be ./main-service'
                    sh 'docker build -t bwnguyenvu/notification-service ./notification-service'
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                branch 'main'
            }
            steps {
                script {
                    docker.withRegistry(credentialsId: 'dockerhub', url: 'https://index.docker.io/v1/') {
                        sh 'docker push bwnguyenvu/swd392-be'
                        sh 'docker push bwnguyenvu/notification-service'
                    }
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo 'Deploying the application'
                    sh 'docker pull bwnguyenvu/swd392-be'
                    sh 'docker pull bwnguyenvu/notification-service'
                    sh 'docker run -d -p 6868:6868 --name swd392-container --env-file ./env.list bwnguyenvu/swd392-be'
                    sh 'docker run -d -p 8082:8082 --name notification-service-container --env-file ./env.list bwnguyenvu/notification-service'
                }
            }
        }
    }

    post {
        always {
            sh 'docker rmi bwnguyenvu/swd392-be || true'
            sh 'docker rmi bwnguyenvu/notification-service || true'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
