pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'dockerhub'
        DOCKER_IMAGE_MAIN = 'bwnguyenvu/swd392-be'
        DOCKER_IMAGE_NOTIFICATION = 'bwnguyenvu/notification-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/BWNguyenVu/swd392-be.git'
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    // Build Docker images
                    sh 'docker build -t $DOCKER_IMAGE_MAIN ./main-service'
                    sh 'docker build -t $DOCKER_IMAGE_NOTIFICATION ./notification-service'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Log in to Docker Hub
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        // Push images to Docker Hub
                        sh 'docker push $DOCKER_IMAGE_MAIN'
                        sh 'docker push $DOCKER_IMAGE_NOTIFICATION'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying the application'
                    sh "docker pull $DOCKER_IMAGE_MAIN"
                    sh "docker pull $DOCKER_IMAGE_NOTIFICATION"
                    sh 'docker run -d -p 6868:6868 --name swd392-container --env-file ./env.list $DOCKER_IMAGE_MAIN'
                    sh 'docker run -d -p 8082:8082 --name notification-service-container --env-file ./env.list $DOCKER_IMAGE_NOTIFICATION
'
                }
            }
        }
    }

    post {
        always {
            sh 'docker rmi $DOCKER_IMAGE_MAIN || true'
            sh 'docker rmi $DOCKER_IMAGE_NOTIFICATION || true'
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
