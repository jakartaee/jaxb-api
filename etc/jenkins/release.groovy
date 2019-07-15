pipeline {

  agent any

  tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
  }

  environment {
    REPO='git@github.com:eclipse-ee4j/jaxb-api.git'
    CREDENTIALS_ID='jaxb-bot'
  }

  stages {

    stage('Check Environment') {
      steps {
        sh '''
          env | sort
          pwd
          ls -la
        '''
      }
    }
    
  }

}
