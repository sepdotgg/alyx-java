# This Dockerfile is to build Docker image to build sphinx documents.
# Run `docker build -t alyx-sphinx .` to build docker image,
# and run `docker run -it -v $(pwd)/:/docs alyx-sphinx make html` to generate documents.

FROM ubuntu:xenial

# Necessary to build documents with Python3
# https://click.palletsprojects.com/en/7.x/python3/
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

RUN mkdir docs
RUN apt-get update && \
    apt-get install -y texlive texlive-latex-extra pandoc build-essential curl make python3 && \
    apt-get autoremove && \
    apt-get clean
RUN curl -kL https://bootstrap.pypa.io/get-pip.py | python3

RUN pip3 install sphinx sphinx_rtd_theme

WORKDIR /docs
VOLUME /docs

CMD ["/bin/bash"]
