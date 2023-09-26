# ---- Base JDK 17 ----
FROM eclipse-temurin:17-jdk AS base
ENV MAVEN_VERSION="3.9.4"
ENV M2_HOME /opt/maven
ENV PATH=$M2_HOME/bin/:$PATH
RUN cd /opt \
    && wget https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -zxvf apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && mv apache-maven-${MAVEN_VERSION} maven
ENV APP_PATH app/spotify-security
ENV APP_JAR target/*.jar


# ---- Dependencies ----
FROM base AS dependencies
# copy Maven dependencies & sources
WORKDIR /spotify-clone/${APP_PATH}/src/main
COPY ./src/main/* ./
WORKDIR /spotify-clone/${APP_PATH}
COPY ./pom.xml ./
RUN mvn clean package

#COPY ./target/*.jar spotify-security.jar


# ---- Release ----
FROM base AS release
# copy target .jar file
WORKDIR /spotify-clone/${APP_PATH}
COPY --from=dependencies /spotify-clone/${APP_PATH}/${APP_JAR} ./

EXPOSE 8090


# start the application
ENTRYPOINT ["java", "-jar", "/spotify-security.jar"]
#CMD ["java", "-jar", "/spotify-security.jar"]