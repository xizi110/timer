
1. 找不到 ’CL‘命令时
```shell
call "D:\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat"
```
2. `gradlew shadowJar`，打包包含依赖的可执行 jar

3. 打包跟踪jvm生成配置文件
   Graalvm通过静态分析提前编译来为Java应用程序构建高度优化的本机可执行文件，这就需要在编译时就知道所有的程序类型，而java中的反射、动态代理等功能，
在编译时不确定具体的类型，所以在使用GraalVm构建native image前需要通过配置列出反射可见的所有类型。而程序内置提供了agentlib
```shell
java -agentlib:native-image-agent=config-output-dir=.\peizhi\ -jar Timer-1.0.jar
```

4. jar -> exe，生成原生 exe
```shell
native-image -jar .\Timer-1.0.jar --no-fallback
```

5. 去除程序运行时的 cmd 窗口
```shell
.\editbin.exe /SUBSYSTEM:WINDOWS 'C:\Users\zhong\IdeaProjects\timer\build\libs\Timer-1.0.exe'
```
