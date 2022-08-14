FROM ubuntu:18.04

RUN apt-get update && apt-get install -y unzip wget vim

RUN wget https://download.java.net/java/GA/jdk16.0.1/7147401fd7354114ac51ef3e1328291f/9/GPL/openjdk-16.0.1_linux-x64_bin.tar.gz
RUN wget https://services.gradle.org/distributions/gradle-7.1-bin.zip

RUN tar -xvzf openjdk-16.0.1_linux-x64_bin.tar.gz -C /lib
RUN unzip gradle-7.1-bin.zip -d /lib

ENV JAVA_HOME /lib/jdk-16.0.1
ENV GRADLE_HOME /lib/gradle-7.1
ENV PATH $JAVA_HOME/bin:$PATH
ENV PATH $GRADLE_HOME/bin:$PATH

RUN mkdir /lib/book-batch
RUN mkdir /var/log/batch
RUN mkdir /var/log/batch/book
RUN mkdir /var/log/batch/book/root
RUN mkdir /var/log/batch/book/error

ARG V_VERSION
ARG V_PROFILE

ARG V_BATCH_LOG_VOLUME=/var/log/batch/book

ENV BATCH_VERSION=${V_VERSION}
ENV BATCH_PROFILE=${V_PROFILE}

ADD ./build/libs/book-batch-$BATCH_VERSION.jar /lib/book-batch

VOLUME ["$V_BATCH_LOG_VOLUME"]
ENTRYPOINT java -jar -Dspring.profiles.active=$BATCH_PROFILE /lib/book-batch/book-batch-$BATCH_VERSION.jar