FROM gomerepo.io:5000/oversea/java8
MAINTAINER zhaozhou
ADD @project.build.finalName@.jar @project.artifactId@.jar
RUN sh -c 'touch /@project.artifactId@.jar'
ENV JAVA_OPTS=""
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /@project.artifactId@.jar