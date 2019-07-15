pipeline {

    agent any

    tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
    }

    environment {
        GIT_REPO='git@github.com:eclipse-ee4j/jaxb-api.git'
        GIT_CREDENTIALS_ID='github-bot-ssh'
        API_DIR="${WORKSPACE}"
        SPEC_DIR="${WORKSPACE}/spec"
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
        stage('Initialization') {
            steps {
                git branch: BRANCH, credentialsId: GIT_CREDENTIALS_ID, url: GIT_REPO
                // GPG initialization
                withCredentials([file(credentialsId: 'b538a336-774c-4ca6-88ed-46ab067cbe41', variable: 'KEYRING')]) {
                    sh '''
                        gpg --batch --import ${KEYRING}
                        for fpr in $(gpg --list-keys --with-colons  | awk -F: '/fpr:/ {print $10}' | sort -u);
                        do
                          echo -e "5\ny\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                        done

                    '''
                }    
                sh '''
                    git config --global user.email "jsonb-bot@eclipse.org"
                    git config --global user.name "Eclipse JSON-B Bot"
                '''
            }
        }
        
        stage('Release Version') {
            steps {
                configFileProvider([
                        configFile(
                            fileId: '99777a39-41e1-432a-acbc-9be9f32fbf0b',
                            targetLocation: '/home/jenkins/.m2/settings.xml'
                        ), 
                        configFile(
                            fileId: '33aba566-675c-4604-8e9a-369338d78c20', 
                            targetLocation: '/home/jenkins/.m2/'
                        )]) {
                    sh '''
                        pwd
                        ls -l etc
                        . etc/scripts/mvn.sh
                        read_version 'SPEC' "${SPEC_DIR}"
                        read_version 'API' "${API_DIR}/jaxb-api"
                    '''
                }
            }
        }
      
    }

}
