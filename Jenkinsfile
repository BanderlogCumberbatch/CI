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

        stage('Verify Tools') {
            steps {
                bat 'docker --version'
                bat 'docker-compose --version'
            }
        }

        stage('Build and Test') {
            steps {
                script {

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
                    bat 'dir /s reports || echo "No reports directory"'
                    bat 'dir /s target\\surefire-reports 2>nul || echo "No surefire-reports directory"'
                }
            }
            post {
                always {
                    // Разные возможные пути к отчетам
                    junit testResults: '**/surefire-reports/*.xml', allowEmptyResults: true
                    junit testResults: '**/test-results/*.xml', allowEmptyResults: true
                    junit testResults: '**/reports/*.xml', allowEmptyResults: true

                    archiveArtifacts artifacts: '**/target/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: '**/reports/**/*', allowEmptyArchive: true

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
                bat 'docker-compose down --remove-orphans --volumes || echo "Cleanup completed"'
                bat 'docker rm -f api-test-runner selenoid || echo "Containers already removed"'

                // Собираем информацию о тестах для email
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
                                <p><em>Примечание: Тестовые отчеты могут быть недоступны</em></p>
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