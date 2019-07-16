pipeline {

    agent any

    def GIT_CREDENTIALS_ID

    tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
    }

    environment {
        GIT_REPO='git@github.com:eclipse-ee4j/jaxb-api.git'
        SPEC_DIR="${WORKSPACE}/spec"
        API_DIR="${WORKSPACE}"
    }

    stages {

        stage('Init') {
            steps {
                script {
                    GIT_CREDENTIALS_ID="${GIT_CREDENTIALS_ID}"
                }
                git branch: BRANCH, credentialsId: GIT_CREDENTIALS_ID, url: GIT_REPO
                // GPG initialization
                withCredentials([file(credentialsId: GPG_CREDENTIALS_ID, variable: 'KEYRING')]) {
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
        
        stage('Release') {
            steps {
                configFileProvider([
                        configFile(
                            fileId: SETTINGS_XML_ID,
                            targetLocation: '/home/jenkins/.m2/settings.xml'
                        ), 
                        configFile(
                            fileId: SETTINGS_SEC_XML_ID, 
                            targetLocation: '/home/jenkins/.m2/'
                        )]) {
                    sshagent(['github-bot-ssh']) {
                        sh '''
                            etc/jenkins/release.sh "${SPEC_VERSION}" "${NEXT_SPEC_VERSION}" \
                                                   "${API_VERSION}" "${NEXT_API_VERSION}" \
                                                   "${DRY_RUN}" "${OVERWRITE}"
                        '''
                    }
                }
            }
        }
      
    }

}
