def IMAGE_NAME="study-helper-bot"
def CONTAINER_NAMES=["bot-container-1", "bot-container-2"]
def CONTAINER_ARGS_MAP=["bot-container-1":["1020755763:AAFB0c6uCLDBGcmIXf0ZZeLnqecG2SIjsZY", "StudyHelperBot"],
                        "bot-container-2":["1095393713:AAFplKsXVWmtJmxQxU14lubZvjNntw4jdbg", "StudyHelperBot2"]]

def IMAGE_TAG="latest"
def DOCKER_HUB_USER="yurets2000"

node {

    stage('Initialize'){
        def dockerHome = tool 'Jenkins-Docker'
        def mavenHome  = tool 'Jenkins-Maven'
        env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"
    }

    stage('Checkout') {
        checkout scm
    }
    /*Перебудувати проект та сформувати jar-файл*/
    stage('Build'){
        sh "mvn clean package"
    }
    /*Видалити зображення, якщо воно не використовується ні одним контейнером*/
    stage("Image Prune"){
        imagePrune(IMAGE_NAME)
    }
    /*Сформувати нове зображення*/
    stage('Image Build'){
        imageBuild(IMAGE_NAME, IMAGE_TAG)
    }
    /*Внести нове зображення до Docker Registry*/
    stage('Push to Docker Registry'){
        withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            pushToImage(IMAGE_NAME, IMAGE_TAG, USERNAME, PASSWORD)
        }
    }
    /*Отримати оновлене зображення*/
    stage('Pull updated Image'){
        imagePull(IMAGE_NAME, DOCKER_HUB_USER)
    }
    /*Зупинити та видалити контейнери, які використовували застаріле зображення, та запустити ботів на контейнерах з оновленим зображенням*/
    stage('Run App'){
        CONTAINER_NAMES.each{ item ->
            runApp(item, CONTAINER_ARGS_MAP.get(item).get(0), CONTAINER_ARGS_MAP.get(item).get(1), IMAGE_NAME, IMAGE_TAG, DOCKER_HUB_USER)
        }
    }

}

def imagePrune(imageName){
    try {
        sh "docker image prune -f"
    } catch(error){}
}

def imageBuild(imageName, tag){
    sh "docker build -t $imageName:$tag  -t $imageName --pull --no-cache ."
    echo "Image build complete"
}

def pushToImage(imageName, tag, dockerUser, dockerPassword){
    sh "docker login -u $dockerUser -p $dockerPassword"
    sh "docker tag $imageName:$tag $dockerUser/$imageName:$tag"
    sh "docker push $dockerUser/$imageName:$tag"
    echo "Image push complete"
}

def imagePull(imageName, dockerHubUser){
    sh "docker pull $dockerHubUser/$imageName"
    echo "Updated image pulled"
}

def runApp(containerName, arg0, arg1, imageName, tag, dockerHubUser){
    sh "docker stop $containerName || true && docker rm $containerName || true"
    sh "docker run -d --rm --name $containerName $dockerHubUser/$imageName:$tag $arg0 $arg1"
    echo "$containerName started"
}
