# 该镜像需要依赖的基础镜像
FROM openjdk:17
# 创建一个目录来存放你的应用
WORKDIR /app
# 将当前目录下的jar包复制到docker容器的/目录下
COPY ./mall-tiny-1.0.0-SNAPSHOT.jar /app/mall-tiny-1.0.0-SNAPSHOT.jar
# 指定配置文件和日志文件的外部挂载路径
VOLUME ["/app/config", "/app/logs"]
# 声明服务运行在8080端口
EXPOSE 8081
# 指定docker容器启动时运行jar包
CMD ["java", "--add-opens java.base/java.lang=ALL-UNNAMED", "-jar", "mall-tiny-1.0.0-SNAPSHOT.jar", "--spring.config.location=file:/app/config/application-dev.yml"]
# 指定维护者的名字
MAINTAINER jakChou