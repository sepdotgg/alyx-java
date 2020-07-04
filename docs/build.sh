#/usr/local/bin/bash -xe

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

docker build -f "${DIR}/Dockerfile" -t alyx-sphinx $DIR
rm -rf "${DIR}/_build"

# build the docs html
docker run -it -v $DIR/:/docs alyx-sphinx make html