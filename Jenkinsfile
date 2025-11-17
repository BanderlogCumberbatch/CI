pipeline {
    agent {
        docker {
            image 'maven:3.8.5-openjdk-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp'
            reuseNode true
        }
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git submodule update --init --recursive'
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    // Остановка предыдущих контейнеров
                    sh 'docker-compose down || true'

                    // Запуск тестов
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
                    // Сохранение отчётов
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true

                    // Публикация HTML отчетов, если есть
                    publishHTML([
                        allowMissing: true,  // Разрешаем отсутствие отчетов
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
            emailext (
                subject: "Jenkins Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <html>
                        <head>
                            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                        </head>
                        <body>
                            <h2>Результаты тестирования</h2>
                            <p><strong>Сборка:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Статус:</strong> ${currentBuild.currentResult}</p>
                            <p><strong>Проект:</strong> ${env.JOB_NAME}</p>
                            <p>Подробности сборки: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        </body>
                    </html>
                """,
                mimeType: "text/html",
                to: "banderlog.cumberbatch@gmail.com"
            )
        }
    }
}