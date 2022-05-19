pipeline {
    agent any
    options { retry(3) }
    options { timeout(time: 30, unit: 'MINUTES') }
    options { buildDiscarder(logRotator(numToKeepStr: '1')) }
    stages {
        stage('checkout') {
            steps {
                checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'GITHUB', url: 'https://github.com/venkatn087/spring3-mvc-maven-xml-hello-world.git']]]
            }
            
        }
        stage('build') {
            steps {
                bat 'mvn clean package'
            }
        } 
        stage('code coverage') {
            steps {
                jacoco buildOverBuild: true, deltaBranchCoverage: '70', deltaClassCoverage: '70', deltaComplexityCoverage: '70', deltaInstructionCoverage: '70', deltaLineCoverage: '70', deltaMethodCoverage: '70', maximumBranchCoverage: '80', maximumClassCoverage: '80', maximumComplexityCoverage: '80', maximumInstructionCoverage: '80', maximumLineCoverage: '80', maximumMethodCoverage: '80', runAlways: true
            }
        }
        stage('upload the artifact') {
            steps {
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'release', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\pipeline_6\\target\\spring3-mvc-maven-xml-hello-world-1.2.war']], mavenCoordinate: [artifactId: 'example', groupId: 'test.com.one.two', packaging: 'war', version: '4.6']]]
            }
        }
        stage('deploy') {
            steps {
                deploy adapters: [tomcat9(credentialsId: 'tomcatcrede', path: '', url: 'http://localhost:8282/')], contextPath: 'testfive', onFailure: false, war: '**/target/*.war'
            }
        }
    }
    post {
        always {
            mail bcc: '', body: '''HI 
                Build got failed 
               thanks
               devops team''', cc: '', from: '', replyTo: '', subject: 'Hi', to: 'venkatn087@gmail.com'
        }
    }
    
}
