#!/usr/bin/env bash

set -e

IMAGE_TAG="github.com/b-schipper/spotify-security"
VERSION="${1-latest}"

# script takes an optional tag argument, otherwise uses "latest"
docker build \
    -f spotify-security.Dockerfile \
    -t "$IMAGE_TAG:$VERSION" .

if [ -n "$IMAGE" ]; then
    docker tag "$IMAGE_TAG:$VERSION" "$IMAGE"
fi

echo "$IMAGE_TAG:$VERSION"
