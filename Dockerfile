FROM harbor.codewave-dev.163yun.com/library/openjdk:8-jre-multi
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LANGUAGE=C.UTF-8 LC_ALL=C.UTF-8
ADD ./exec.sh /exec.sh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && chmod +x /exec.sh
ADD target/*.jar /app.jar
ENTRYPOINT ["./exec.sh"]
