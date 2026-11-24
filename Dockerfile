FROM eclipse-temurin:17-jdk
WORKDIR /app

ARG WAR_FILE=admin-backend.war
ARG USER_NAME=user
ARG USER_UID=1000
ARG USER_GID=1000

ENV WAR_FILE=admin-backend.war

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN groupadd -f -g ${USER_GID} ${USER_NAME} && \
    (useradd -u ${USER_UID} -g ${USER_GID} -m -s /bin/bash ${USER_NAME} || true)

RUN mkdir -p /data/framework /data/framework/uploads && \
    chown -R ${USER_UID}:${USER_GID} /data && \
    chmod -R 775 /data

RUN chown -R ${USER_UID}:${USER_GID} /app
RUN chmod -R 775 /app

COPY ${WAR_FILE} ${WAR_FILE}

USER ${USER_UID}:${USER_GID}

CMD java -Duser.timezone=Asia/Seoul \
         -jar ${WAR_FILE}
