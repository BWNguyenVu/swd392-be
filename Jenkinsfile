pipeline {
    agent any
    tools {
        maven 'my-maven'
    }

    stages {
        stage('Setup Environment Variables') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'aws-access-key', variable: 'AWS_ACCESS_KEY'),
                        string(credentialsId: 'AWS_PRIVATE_KEY', variable: 'AWS_PRIVATE_KEY'),
                        string(credentialsId: 'AWS_REGION', variable: 'AWS_REGION'),
                        string(credentialsId: 'AWS_BUCKET_NAME', variable: 'AWS_BUCKET_NAME'),
                        string(credentialsId: 'CONNECTION_STRING_POSTGRES_PROD', variable: 'CONNECTION_STRING_POSTGRES_PROD'),
                        string(credentialsId: 'CONNECTION_STRING_POSTGRES_DEV', variable: 'CONNECTION_STRING_POSTGRES_DEV'),
                        string(credentialsId: 'POSTGRES_USERNAME', variable: 'POSTGRES_USERNAME'),
                        string(credentialsId: 'POSTGRES_PASSWORD', variable: 'POSTGRES_PASSWORD'),
                        string(credentialsId: 'PAYOS_CLIENT_ID', variable: 'PAYOS_CLIENT_ID'),
                        string(credentialsId: 'PAYOS_API_KEY', variable: 'PAYOS_API_KEY'),
                        string(credentialsId: 'PAYOS_CHECKSUM_KEY', variable: 'PAYOS_CHECKSUM_KEY'),
                        string(credentialsId: 'EMAIL_USERNAME', variable: 'EMAIL_USERNAME'),
                        string(credentialsId: 'EMAIL_PASSWORD', variable: 'EMAIL_PASSWORD'),
                        string(credentialsId: 'KAFKA_BOOTSTRAP_SERVER', variable: 'KAFKA_BOOTSTRAP_SERVER'),
                        string(credentialsId: 'REDIS_HOST', variable: 'REDIS_HOST'),
                        string(credentialsId: 'REDIS_PORT', variable: 'REDIS_PORT'),
                        string(credentialsId: 'REDIS_PASSWORD', variable: 'REDIS_PASSWORD'),
                        string(credentialsId: 'REDIS_DATABASE', variable: 'REDIS_DATABASE'),
                        string(credentialsId: 'GOOGLE_CLIENT_ID', variable: 'GOOGLE_CLIENT_ID'),
                        string(credentialsId: 'GOOGLE_CLIENT_SECRET', variable: 'GOOGLE_CLIENT_SECRET'),
                        string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'),
                        string(credentialsId: 'PUBLIC_API', variable: 'PUBLIC_API'),
                        string(credentialsId: 'CONNECTION_STRING_NOTIFICATION_DEV', variable: 'CONNECTION_STRING_NOTIFICATION_DEV'),
                    ]) {
                        // This block sets up the environment variables
                        env.AWS_ACCESS_KEY = AWS_ACCESS_KEY
                        env.AWS_PRIVATE_KEY = AWS_PRIVATE_KEY
                        env.AWS_REGION = AWS_REGION
                        env.AWS_BUCKET_NAME = AWS_BUCKET_NAME
                        env.CONNECTION_STRING_POSTGRES_PROD = CONNECTION_STRING_POSTGRES_PROD
                        env.CONNECTION_STRING_POSTGRES_DEV = CONNECTION_STRING_POSTGRES_DEV
                        env.POSTGRES_USERNAME = POSTGRES_USERNAME
                        env.POSTGRES_PASSWORD = POSTGRES_PASSWORD
                        env.PAYOS_CLIENT_ID = PAYOS_CLIENT_ID
                        env.PAYOS_API_KEY = PAYOS_API_KEY
                        env.PAYOS_CHECKSUM_KEY = PAYOS_CHECKSUM_KEY
                        env.EMAIL_USERNAME = EMAIL_USERNAME
                        env.EMAIL_PASSWORD = EMAIL_PASSWORD
                        env.KAFKA_BOOTSTRAP_SERVER = KAFKA_BOOTSTRAP_SERVER
                        env.REDIS_HOST = REDIS_HOST
                        env.REDIS_PORT = REDIS_PORT
                        env.REDIS_PASSWORD = REDIS_PASSWORD
                        env.REDIS_DATABASE = REDIS_DATABASE
                        env.GOOGLE_CLIENT_ID = GOOGLE_CLIENT_ID
                        env.GOOGLE_CLIENT_SECRET = GOOGLE_CLIENT_SECRET
                        env.JWT_SECRET = JWT_SECRET
                        env.PUBLIC_API = PUBLIC_API
                        env.CONNECTION_STRING_NOTIFICATION_DEV = CONNECTION_STRING_NOTIFICATION_DEV
                    }
                }
            }
        }

        stage('Start Redis and Postgres Containers') {
            steps {
                script {
                    // Start Redis if not running
                    sh '''
                    if [ $(docker inspect -f '{{.State.Running}}' 03157b32e3f3) = "false" ]; then
                      docker start 03157b32e3f3;
                    fi
                    '''

                    // Start Postgres if not running
                    sh '''
                    if [ $(docker inspect -f '{{.State.Running}}' e311c112c83b) = "false" ]; then
                      docker start e311c112c83b;
                    fi
                    '''
                }
            }
        }

        stage('Start Zookeeper') {
            steps {
                script {
                    sh '''
                    if [ $(docker inspect -f '{{.State.Running}}' 4a53c8e9239e) = "false" ]; then
                      docker start 4a53c8e9239e;
                    fi
                    '''
                }
            }
        }

        stage('Build Maven for Main Service') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/BWNguyenVu/swd392-be.git']])

                dir('./main-service') {
                    sh '''
                        mvn clean install -X \
                        -Daws.accessKey=${AWS_ACCESS_KEY} \
                        -Daws.privateKey=${AWS_PRIVATE_KEY} \
                        -Daws.region=${AWS_REGION} \
                        -Daws.bucketName=${AWS_BUCKET_NAME} \
                        -Dspring.datasource.url=${CONNECTION_STRING_POSTGRES_PROD} \
                        -Dspring.datasource.username=${POSTGRES_USERNAME} \
                        -Dspring.datasource.password=${POSTGRES_PASSWORD} \
                        -Dspring.mail.username=${EMAIL_USERNAME} \
                        -Dspring.mail.password="${EMAIL_PASSWORD}" \
                        -Dspring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVER} \
                        -Dspring.data.redis.host=${REDIS_HOST} \
                        -Dspring.data.redis.password="${REDIS_PASSWORD}" \
                        -Dspring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID} \
                        -Dspring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET} \
                        -Djwt.secret=${JWT_SECRET} \
                        -Dpublic.api.url=${PUBLIC_API}
                    '''
                }
            }
        }

        stage('Build Maven for Notification Service') {
            steps {
                dir('./notification-service') {
                    sh '''
                        mvn clean install -X \
                        -Dspring.mail.username=${EMAIL_USERNAME} \
                        -Dspring.mail.password="${EMAIL_PASSWORD}" \
                        -Dspring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVER} \
                        -Dpublic.api.url=${PUBLIC_API} \
                        -Dspring.datasource.url=${CONNECTION_STRING_NOTIFICATION_DEV} \
                        -Dspring.datasource.username=${POSTGRES_USERNAME} \
                        -Dspring.datasource.password=${POSTGRES_PASSWORD} \
                    '''
                }
            }
        }

        stage('Build Docker Image for Main Service') {
            steps {
                script {
                    sh 'sudo docker build -t bwnguyenvu/swd392-be ./main-service'
                }
            }
        }

        stage('Build Docker Image for Notification Service') {
            steps {
                script {
                    sh 'sudo docker build -t bwnguyenvu/swd392-notification ./notification-service'
                }
            }
        }

        stage('Push Image to Docker Hub for Main Service') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
                        sh 'docker login -u bwnguyenvu -p ${dockerhubpwd}'
                    }
                    sh 'docker push bwnguyenvu/swd392-be'
                }
            }
        }

        stage('Push Image to Docker Hub for Notification Service') {
            steps {
                script {
                    sh 'docker push bwnguyenvu/swd392-notification'
                }
            }
        }

        stage('Pull Docker Image for Main Service') {
            steps {
                script {
                    sh 'sudo docker pull bwnguyenvu/swd392-be:latest'
                }
            }
        }

        stage('Pull Docker Image for Notification Service') {
            steps {
                script {
                    sh 'sudo docker pull bwnguyenvu/swd392-notification:latest'
                }
            }
        }

        stage('Run Docker Container for Main Service') {
            steps {
                script {
                    sh 'sudo docker stop swd392-container || true'
                    sh 'sudo docker rm swd392-container || true'

                    sh '''
                        sudo docker run -d -p 6868:6868 \
                        --name swd392-container \
                        -e AWS_ACCESS_KEY=${AWS_ACCESS_KEY} \
                        -e AWS_PRIVATE_KEY=${AWS_PRIVATE_KEY} \
                        -e AWS_REGION=${AWS_REGION} \
                        -e AWS_BUCKET_NAME=${AWS_BUCKET_NAME} \
                        -e CONNECTION_STRING_POSTGRES_PROD=${CONNECTION_STRING_POSTGRES_PROD} \
                        -e CONNECTION_STRING_POSTGRES_DEV=${CONNECTION_STRING_POSTGRES_DEV} \
                        -e POSTGRES_USERNAME=${POSTGRES_USERNAME} \
                        -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                        -e PAYOS_CLIENT_ID=${PAYOS_CLIENT_ID} \
                        -e PAYOS_API_KEY=${PAYOS_API_KEY} \
                        -e PAYOS_CHECKSUM_KEY=${PAYOS_CHECKSUM_KEY} \
                        -e EMAIL_USERNAME=${EMAIL_USERNAME} \
                        -e EMAIL_PASSWORD="${EMAIL_PASSWORD}" \
                        -e KAFKA_BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVER} \
                        -e REDIS_HOST=${REDIS_HOST} \
                        -e REDIS_PORT=${REDIS_PORT} \
                        -e REDIS_PASSWORD="${REDIS_PASSWORD}" \
                        -e REDIS_DATABASE=${REDIS_DATABASE} \
                        -e GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID} \
                        -e GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET} \
                        -e JWT_SECRET=${JWT_SECRET} \
                        -e PUBLIC_API=${PUBLIC_API} \
                        bwnguyenvu/swd392-be:latest
                    '''
                }
            }
        }

        stage('Run Docker Container for Notification Service') {
            steps {
                script {
                    sh 'sudo docker stop swd392-notification-container || true'
                    sh 'sudo docker rm swd392-notification-container || true'

                    sh '''
                        sudo docker run -d -p 8082:8082 \
                        --name swd392-notification-container \
                        -e EMAIL_USERNAME=${EMAIL_USERNAME} \
                        -e EMAIL_PASSWORD="${EMAIL_PASSWORD}" \
                        -e KAFKA_BOOTSTRAP_SERVER=${KAFKA_BOOTSTRAP_SERVER} \
                        -e PUBLIC_API=${PUBLIC_API} \
                        -e CONNECTION_STRING_NOTIFICATION_DEV=${CONNECTION_STRING_NOTIFICATION_DEV} \
                        -e POSTGRES_USERNAME=${POSTGRES_USERNAME} \
                        -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
                        bwnguyenvu/swd392-notification:latest
                    '''
                }
            }
        }
    }
}
