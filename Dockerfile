FROM maven:3.8.6-amazoncorretto-11
ENV ICARO_HOME="/home/dilbert/icaro"
COPY . .
CMD mvn test