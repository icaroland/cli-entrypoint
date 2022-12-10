FROM maven:3.8.6-amazoncorretto-11
COPY pom.xml .
RUN mvn package
ENV ICARO_HOME=/home/icaro
COPY ./src src
COPY ./test test
CMD mvn package