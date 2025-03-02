FROM ubuntu:latest
LABEL authors="nimda"

ENTRYPOINT ["top", "-b"]