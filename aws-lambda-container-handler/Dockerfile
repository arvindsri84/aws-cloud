FROM debian:12

# installation copied from https://github.com/corretto/corretto-docker/blob/df7c548293c02db289dfe7de38c90e92a43e5fcf/11/jdk/debian/Dockerfile
ARG version=11.0.20.8-1

RUN set -eux \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates gnupg software-properties-common fontconfig java-common \
    && curl -fL https://apt.corretto.aws/corretto.key | apt-key add - \
    && add-apt-repository 'deb https://apt.corretto.aws stable main' \
    && mkdir -p /usr/share/man/man1 || true \
    && apt-get update \
    && apt-get install -y java-11-amazon-corretto-jdk=1:$version \
    && apt-get purge -y --auto-remove -o APT::AutoRemove::RecommendsImportant=false curl gnupg software-properties-common

ENV LANG C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-11-amazon-corretto