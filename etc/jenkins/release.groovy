pipeline {

    agent any

    options (
        [
            [$class: 'GithubProjectProperty',
                displayName: '',
                projectUrlStr: 'https://github.com/eclipse-ee4j/jaxb-api/'
            ],
            [$class: 'RebuildSettings',
                autoRebuild: false,
                rebuildDisabled: false
            ], 
            parameters([
                    string(defaultValue: '', description: '''Specification version to release.
Default value is from POM snapshot.''', name: 'SPEC_VERSION', trim: true),
                    string(defaultValue: '', description: '''Next specification snapshot version to set (e.g. 1.2.4-SNAPSHOT).
Default value is from POM snapshot with last component incremented by 1.''', name: 'NEXT_SPEC_VERSION', trim: true),
                    string(defaultValue: '', description: '''API version to release.
Default value is from POM snapshot.''', name: 'API_VERSION', trim: true),
                    string(defaultValue: '', description: '''Next API snapshot version to set (e.g. 1.2.4-SNAPSHOT).
Default value is from POM snapshot with last component incremented by 1.''', name: 'NEXT_API_VERSION', trim: true),
                    string(defaultValue: 'release_job', description: '''Branch to release.
Default value is master.''', name: 'BRANCH', trim: true),
                    booleanParam(defaultValue: false, description: 'Do not publish artifacts to OSSRH and code changes to GitHub.', name: 'DRY_RUN'),
                    booleanParam(defaultValue: false, description: 'Allows to overwrite existing version in git and OSSRH staging repositories.', name: 'OVERWRITE')
                ]),
            buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '20'))
        ]
    )

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
                git branch: BRANCH, credentialsId: SSH_CREDENTIALS_ID, url: GIT_REPO
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
                    sshagent([SSH_CREDENTIALS_ID]) {
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
