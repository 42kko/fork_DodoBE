FROM gradle:8.5.0-jdk21

# 작업 디렉토리 설정
WORKDIR /home/gradle/project

# Spring 소스 코드를 이미지에 복사
COPY . .

# gradle 빌드 시 proxy 설정을 gradle.properties에 추가
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

# gradlew를 이용한 프로젝트 필드
RUN ./gradlew clean build

# 빌드 결과 jar 파일을 실행
#CMD ["java", "-jar", "/home/gradle/project/build/libs/dododocs-0.0.1-SNAPSHOT.jar"]
CMD ["sh", "-c", "env && exec java -jar -Dspring.profiles.active=dev /home/gradle/project/build/libs/dododocs-0.0.1-SNAPSHOT.jar"]
