pipeline {
  agent {
    node {
      label 'maven'
    }
  }

    parameters {
        string(name:'TAG_NAME',defaultValue: '',description:'')
    }

    environment {
        DOCKER_CREDENTIAL_ID = 'dockerhub-id'
        GIT_CREDENTIALS_ID = 'gitee-id'
        KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
        REGISTRY = 'node1:9990'
        DOCKERHUB_NAMESPACE = 'docker'
        GITREPO_ACCOUNT = 'wolfcodeliug'
        GITREPO_EMAIL = 'liugang@wolfcode.cn'
        APP_NAME = 'devops-demo'
    }

    stages {
        stage ('checkout scm') {
            steps {
                checkout(scm)
            }
        }

        stage ('unit test') {
            steps {
                container ('maven') {
                    sh 'mvn clean  -gs `pwd`/configuration/settings.xml test'
                }
            }
        }

        stage ('build & push') {
            steps {
                container ('maven') {
                    sh 'mvn  -Dmaven.test.skip=true -gs `pwd`/configuration/settings.xml clean package'
                    sh 'docker build -f Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
                    withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                        sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                        sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER'
                    }
                }
            }
        }

        stage('push latest'){
           when{
             branch 'master'
           }
           steps{
                container ('maven') {
                  sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:latest '
                  sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:latest '
                }
           }
        }

        stage('deploy to dev') {
          when{
            branch 'master'
          }
          steps {
            input(id: 'deploy-to-dev', message: 'deploy to dev?')
            kubernetesDeploy(configs: 'deploy/dev/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }
        stage('push with tag'){
          when{
            expression{
              return params.TAG_NAME =~ /v.*/
            }
          }
          steps {
              container ('maven') {
                input(id: 'release-image-with-tag', message: 'release image with tag?')
                  sshagent(["${GIT_CREDENTIALS_ID}"]) {
                    sh 'git config --global user.email "$GITREPO_EMAIL" '
                    sh 'git config --global user.name "$GITREPO_ACCOUNT" '
                    sh 'git tag -a $TAG_NAME -m "$TAG_NAME" '
                    sh 'git push git@gitee.com:$GITREPO_ACCOUNT/$APP_NAME.git --tags'
                  }
                sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:$TAG_NAME '
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:$TAG_NAME '
              }
          }
        }
        stage('deploy to production') {
          when{
            expression{
              return params.TAG_NAME =~ /v.*/
            }
          }
          steps {
            input(id: 'deploy-to-production', message: 'deploy to production?')
            kubernetesDeploy(configs: 'deploy/prod/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }
    }
}
