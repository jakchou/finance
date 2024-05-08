#!/bin/sh
exec java $JAVA_OPTS -Dspring.profiles.active=$LOWCODE_SERVICE_ENV  -jar /app.jar