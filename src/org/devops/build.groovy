package org.devops


// 使用函数简化构建步骤
def buildAmd64() {
    echo 'Building for amd64'
    buildImage('amd64')
}

def buildArm64() {
    echo 'Building for arm64'
    buildImage('arm64')
}

// 使用函数执行实际的镜像构建
def buildImage(String arch) {
    // 确保定义了 kubernetes agent 模板
    def nodeLabel = (arch == 'amd64') ? 'kanikoamd' : 'kanikoarm'
    agent { kubernetes { inheritFrom nodeLabel } }
    
    // ...这里放置实际的构建命令
    unstash 'source-code' // 恢复之前存储的代码
    container(nodeLabel) {
        sh """
        /kaniko/executor \
              --context ${env.WORKSPACE}/${params.BUILD_DIRECTORY} \
              --dockerfile ${params.BUILD_DIRECTORY}/Dockerfile \
              --destination ${env.IMAGE_REGISTRY}/${env.IMAGE_NAMESPACE}/${env.JOB_NAME}:${env.VERSION_TAG}-${arch} \
              --cache=true \
              --cache-repo=${env.IMAGE_REGISTRY}/${env.IMAGE_NAMESPACE}/cache \
              --skip-tls-verify \
              --skip-unused-stages=true \
              --custom-platform=linux/${arch} \
              --build-arg BUILDKIT_INLINE_CACHE=1 \
              --snapshotMode=redo \
              --log-format=text \
              --verbosity=info
        """
    }
}
