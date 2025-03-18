#!/bin/bash -e

./mvnw clean package spring-boot:build-image -Pnative

VERSION=$(./mvnw -q \
    -Dexec.executable=echo \
    -Dexec.args='${project.version}' \
    --non-recursive \
    exec:exec)

kind load docker-image k8stest-api:$VERSION
kind load docker-image k8stest-listener:$VERSION
kind load docker-image k8stest-service:$VERSION
