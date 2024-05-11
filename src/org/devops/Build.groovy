package org.devops


//构建类型
def Build(BuildType,BuildShell){
    def BuildTools = ["mvn":"m2","ant":"ANT","gradle":"Gradle","npm":"NPN"]

    println("当前您选择的构建类型为：${BuildType}")
    BuildHome = tool BuildTools[BuildType]

    sh "${BuildHome}/bin/${BuildType}  ${BuildShell}" 
}
