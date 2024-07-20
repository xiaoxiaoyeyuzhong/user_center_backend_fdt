# 使用 OpenJDK 17-slim 作为基础镜像,包含apt-get
FROM openjdk:17-slim

# 设置环境变量
ENV MAVEN_VERSION=3.6.3
ENV MAVEN_HOME=/usr/local/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# 安装依赖工具
RUN apt-get update && \
    apt-get install -y wget tar && \
    rm -rf /var/lib/apt/lists/*

# 下载并安装 Maven
RUN wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/local && \
    mv /usr/local/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# 验证安装
RUN java -version && mvn -version

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests


# Run the web service on container startup.
CMD ["java","-jar","/app/target/user_center_fdt-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]