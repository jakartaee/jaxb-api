pipeline {

  agent any

  tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
  }

  environment {
    GIT_REPO='git@github.com:eclipse-ee4j/jaxb-api.git'
    GIT_CREDENTIALS_ID='github-bot-ssh'
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
    stage('Git Checkout') {
        steps {
            git branch: BRANCH, credentialsId: GIT_CREDENTIALS_ID, url: GIT_REPO
        }
    }
        
  }

}
