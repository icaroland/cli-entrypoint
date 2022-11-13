FROM maven:3.8.6-amazoncorretto-11
ENV ICARO_HOME=$HOME/icaro
COPY . .
CMD mvn test