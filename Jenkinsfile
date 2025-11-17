pipeline {
    agent {
        label 'butler'
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git submodule update --init --recursive'
            }
        }

        stage('Build and Test') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: '7b7ebe1b-3300-4e48-92c2-a9de5a18c6ff',
                    usernameVariable: 'WORDPRESS_USERNAME',
                    passwordVariable: 'WORDPRESS_PASSWORD'
                )]) {
                    bat 'docker-compose down || echo "No containers to stop"'
                    bat 'docker-compose up --build --abort-on-container-exit --exit-code-from test-runner test-runner'
                }
            }
        }

        stage('Collect Reports') {
            steps {
                script {
                    // Проверка, существуют ли отчеты
                    bat 'dir /s target || echo "No target directory"'
                    bat 'dir /s target/surefire-reports || echo "No surefire-reports directory"'
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: '*.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
    }

    post {
        always {
            script {
                // Очистка
                bat 'docker-compose down --remove-orphans --volumes || echo "Cleanup completed"'
                bat 'docker rm -f api-test-runner selenoid || echo "Containers already removed"'
            }
        }
    }
}