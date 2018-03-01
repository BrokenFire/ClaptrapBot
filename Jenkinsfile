node {
    def app
   stage('Clone') { // for display purposes
      	// Get some code from a GitHub repository
      	echo env.BRANCH_NAME
		script {
	        if (env.BRANCH_NAME == 'master') {
	                git url: 'https://github.com/BrokenFire/BrokenDiscordBot.git', branch: 'master' 
	        } else {
	               git url: 'https://github.com/BrokenFire/BrokenDiscordBot.git', branch: 'devel' 
	        }
	    }

   }
   stage('Gradle Build'){
        script {
            if (env.BRANCH_NAME == 'master') {
                    build job: 'Bot Discord Gradle', wait: true
            } else {
                   build job: 'Bot Discord Gradle devel', wait: true
            }
        }
   }
   stage('Build image') {
        /* This builds the actual image; synonymous to
         * docker build on the command line */

        app = docker.build("brokenfire/brokendiscordbot","--rm=true . ", "--build-arg BUILD_NBR=${BUILD_NUMBER}", "--build-arg BRANCH_NAME=${BRANCH_NAME}")
    }
   stage('Push image') {
        /* Finally, we'll push the image with two tags:
         * First, the incremental build number from Jenkins
         * Second, the 'latest' tag.
         * Pushing multiple tags is cheap, as all the layers are reused. */
         def docker_tag
         script {
            if (env.BRANCH_NAME == 'master') {
                    docker_tag = "latest"
            } else {
                    docler_tag = "devel"
            }
         }
         echo docker_tag
         app.push(docker_tag)
        
        
    }
    stage('Cleaning'){
        sh "docker image prune -f"
    }
} 
