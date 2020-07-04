#/usr/local/bin/bash -xe

docker build -t alyx-sphinx .
rm -rf _build

# build the docs html
docker run -it -v $(pwd)/:/documents alyx-sphinx make html