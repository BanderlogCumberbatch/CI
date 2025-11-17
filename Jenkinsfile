pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git submodule update --init --recursive'  // если есть подмодули
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    // Останавливаем предыдущие контейнеры
                    sh 'docker-compose down || true'

                    // Запускаем тесты
                    sh '''
                        docker-compose up \
                            --build \
                            --abort-on-container-exit \
                            --exit-code-from test-runner \
                            test-runner
                    '''
                }
            }
            post {
                always {
                    // Сохраняем отчеты независимо от результата тестов
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true

                    // Публикуем HTML отчеты (если есть)
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site',
                        reportFiles: 'surefire-report.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
    }

    post {
        always {
            // Очистка
            sh 'docker-compose down -v || true'
            cleanWs()
        }
        success {
            emailext (
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Тесты прошли успешно. Ссылка на сборку: ${env.BUILD_URL}",
                to: "banderlog.cumberbatch@gmail.com"
            )
        }
        failure {
            emailext (
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Тесты упали. Ссылка на сборку: ${env.BUILD_URL}",
                to: "banderlog.cumberbatch@gmail.com"
            )
        }
    }
}