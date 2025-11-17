pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git submodule update --init --recursive'  // если есть подмодули
            }
        }

        stage('Build and Test') {
            triggers { pollSCM('* * * * *') }
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
                subject: "УСПЕХ: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <html>
                        <head>
                            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                        </head>
                        <body>
                            <h2>Результаты тестирования</h2>
                            <p><strong>Проект:</strong> ${PROJECT_NAME}</p>
                            <p><strong>Сборка:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Статус:</strong> ${currentBuild.currentResult}</p>
                            <p>Подробности сборки:</p>
                            <p>Кол-во тестов: ${TEST_COUNTS, var="TOTAL"}</p>
                            <p>Кол-во провалившихся тестов: ${TEST_COUNTS, var="FAIL"}</p>
                            <p>Кол-во пройденных тестов: ${TEST_COUNTS, var="PASS"}</p>
                        </body>
                    </html>
                """,
                mimeType: "text/html",
                to: "banderlog.cumberbatch@gmail.com"
            )
        }
        failure {
            emailext (
                subject: "ПРОВАЛ: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <html>
                        <head>
                            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                        </head>
                        <body>
                            <h2>Результаты тестирования</h2>
                            <p><strong>Проект:</strong> ${PROJECT_NAME}</p>
                            <p><strong>Сборка:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Статус:</strong> ${currentBuild.currentResult}</p>
                            <p>Подробности сборки:</p>
                            <p>Кол-во тестов: ${TEST_COUNTS,var="TOTAL"}</p>
                            <p>Кол-во провалившихся тестов: ${TEST_COUNTS,var="FAIL"}</p>
                            <p>Кол-во пройденных тестов: ${TEST_COUNTS,var="PASS"}</p>
                        </body>
                    </html>
                """,
                mimeType: "text/html",
                from: "banderlog.cumberbatch@gmail.com"
                to: "banderlog.cumberbatch@gmail.com"
            )
        }
    }
}