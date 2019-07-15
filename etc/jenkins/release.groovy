pipeline {

  agent any

  tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
  }

  environment {
  }
  stages {

    stage('Check Environment') {
      sh '''
        env | sort
        pwd
        ls -la
      '''
    }
    
  }

}
