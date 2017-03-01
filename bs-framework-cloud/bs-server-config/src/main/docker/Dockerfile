FROM 10.69.42.85:5000/mxsev/java8
MAINTAINER zhaozhou

ADD @project.build.finalName@.jar cloud-app.jar
RUN sh -c 'touch /cloud-app.jar'

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=docker", "-jar", "/cloud-app.jar"]




