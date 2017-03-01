FROM 10.69.42.85:5000/mxsev/java8
MAINTAINER erdaoya

ADD @project.build.finalName@.jar @project.build.finalName@.jar
RUN sh -c 'touch /@project.build.finalName@.jar'

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=docker", "-jar", "/@project.build.finalName@.jar"]




