FROM maven:3.9.9-amazoncorretto-23 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package

FROM openjdk:23-slim-bullseye

WORKDIR /

RUN apt-get update \
    && apt-get install -y automake ca-certificates g++ git libtool libleptonica-dev make pkg-config libpango1.0-dev libicu-dev libcairo2-dev \
    && git clone --depth 1 --branch 5.5.0 https://github.com/tesseract-ocr/tesseract.git \
    && cd tesseract \
    && ./autogen.sh \
    && ./configure \
    && make \
    && make install \
    && ldconfig \
    && make training \
    && make training-install

WORKDIR /app

RUN git clone https://github.com/tesseract-ocr/tessdata.git

COPY --from=build /app/target/resume-service*.jar ./app.jar

CMD ["java", "-jar", "app.jar"]