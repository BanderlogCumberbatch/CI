pipeline {
    agent {
        label 'butler' // Явно указываем метку агента
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Cleanup') {
            steps {
                script {
                    bat '''
                        docker-compose down --rmi all --volumes --remove-orphans || echo "Cleanup completed"

                        # Дополнительная очистка на случай конфликтов
                        docker rm -f selenoid || echo "Selenoid container not found"
                        docker rm -f test-runner || echo "Test-runner container not found"

                        # Очистка сетей
                        docker network prune -f || echo "Network prune completed"
                    '''
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
                bat 'git submodule update --init --recursive'
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'docker --version'
                bat 'docker-compose --version'
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    bat '''
                        docker-compose up --build --force-recreate --abort-on-container-exit --exit-code-from test-runner test-runner
                    '''
                }
            }
        }

        stage('Collect Reports') {
            steps {
                script {
                    // Копируем отчеты из контейнеров если нужно
                    bat '''
                        docker cp test-runner:/app/target/ ./target-from-container/ 2>nul || echo "No reports in test-runner"
                        docker cp test-runner:/app/reports/ ./reports-from-container/ 2>nul || echo "No reports in test-runner"

                        # Проверяем что есть в рабочей директории
                        dir /s *report* || echo "No report files found"
                        dir /s target || echo "No target directory"
                    '''
                }
            }
            post {
                always {
                    // Ищем отчеты в разных возможных местах
                    junit testResults: '**/surefire-reports/*.xml', allowEmptyResults: true
                    junit testResults: '**/test-results/*.xml', allowEmptyResults: true
                    junit testResults: '**/reports/*.xml', allowEmptyResults: true
                    junit testResults: '**/target/*-reports/*.xml', allowEmptyResults: true
                    junit testResults: '**/target-from-container/**/*.xml', allowEmptyResults: true

                    archiveArtifacts artifacts: '**/target/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/target-from-container/**/*', allowEmptyArchive: true

                    publishHTML([
                        allowMissing: true,
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
            script {
                bat '''
                    docker-compose down --rmi local --remove-orphans || echo "Final cleanup"
                    docker rm -f selenoid test-runner 2>nul || echo "Containers already removed"
                '''

                def testResult = currentBuild.currentResult
                def buildUrl = env.BUILD_URL
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER

                emailext (
                    subject: "Jenkins Job '${jobName} [${buildNumber}]' - ${testResult}",
                    body: """
                        <html>
                            <head>
                                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                            </head>
                            <body>
                                <h2>Результаты тестирования</h2>
                                <p><strong>Сборка:</strong> #${buildNumber}</p>
                                <p><strong>Статус:</strong> ${testResult}</p>
                                <p><strong>Проект:</strong> ${jobName}</p>
                                <p>Подробности сборки: <a href="${buildUrl}">${buildUrl}</a></p>
                                <p><em>Тестовые отчеты могут быть недоступны из-за конфликта контейнеров</em></p>
                            </body>
                        </html>
                    """,
                    mimeType: "text/html",
                    to: "banderlog.cumberbatch@gmail.com"
                )
            }
        }
    }
}