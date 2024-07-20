# 使用 OpenJDK 17-alpine 作为基础镜像
FROM openjdk:17-alpine

# 设置环境变量
ENV MAVEN_VERSION=3.6.3
ENV MAVEN_HOME=/usr/local/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# 安装依赖工具和 Maven
RUN apk add --no-cache wget tar bash && \
    wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/local && \
    mv /usr/local/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME} && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# 验证安装
RUN java -version && mvn -version

# 复制本地代码到容器中
WORKDIR /app
COPY pom.xml .
COPY src ./src

# 构建项目
RUN mvn package -DskipTests

# 启动服务
CMD ["java", "-jar", "/app/target/user_center_fdt-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
