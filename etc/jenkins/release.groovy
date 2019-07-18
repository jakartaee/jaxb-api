// Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Distribution License v. 1.0, which is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
// SPDX-License-Identifier: BSD-3-Clause

// Job input parameters:
//   SPEC_VERSION      - Specification version to release
//   NEXT_SPEC_VERSION - Next specification snapshot version to set (e.g. 1.2.4-SNAPSHOT)
//   API_VERSION       - API version to release
//   NEXT_API_VERSION  - Next API snapshot version to set (e.g. 1.2.4-SNAPSHOT)
//   BRANCH            - Branch to release
//   DRY_RUN           - Do not publish artifacts to OSSRH and code changes to GitHub
//   OVERWRITE         - Allows to overwrite existing version in git and OSSRH staging repositories

// Job internal argumets:
//   GIT_USER_NAME       - Git user name (for commits)
//   GIT_USER_EMAIL      - Git user e-mail (for commits)
//   SSH_CREDENTIALS_ID  - Jenkins ID of SSH credentials
//   GPG_CREDENTIALS_ID  - Jenkins ID of GPG credentials (stored as KEYRING variable)
//   SETTINGS_XML_ID     - Jenkins ID of settings.xml file
//   SETTINGS_SEC_XML_ID - Jenkins ID of settings-security.xml file

pipeline {
    
    agent any

    tools {
        jdk 'openjdk-jdk11-latest'
        maven 'apache-maven-latest'
    }

    environment {
        SPEC_DIR="${WORKSPACE}/spec"
        API_DIR="${WORKSPACE}"
    }

    stages {
        // Initialize build environment
        stage('Init') {
            steps {
                git branch: BRANCH, credentialsId: SSH_CREDENTIALS_ID, url: GIT_URL
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
                // Git configuration
                sh '''
                    git config --global user.name "${GIT_USER_NAME}"
                    git config --global user.email "${GIT_USER_EMAIL}"
                '''
            }
        }
        // Perform release
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
