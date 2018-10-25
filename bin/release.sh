#!/usr/bin/env bash
TOPDIR=$(cd `dirname $0`/..;pwd)

if [ "$#" -ne 1 ]; then
    echo "Tag name is mandatory argument"
    exit
fi
TAG=$1

if git status | grep "nothing to commit, working tree clean"
then
  echo "Working tree clean, proceeding with the release!";
else
  echo "Some changes are not committed. Working tree has to be clean in order to do a release" && exit
fi

echo "Adding new tag to gradle properties file $TOPDIR/gradle.properties"
sed -i.bak "s/docker_tag=.*/docker_tag=$TAG/" $TOPDIR/gradle.properties && rm -rf $TOPDIR/gradle.properties.bak
echo "DONE"
echo

echo "Creating and pushing git tag $TAG..."
git tag $TAG
git add $TOPDIR/gradle.properties
git commit -m "Release $TAG"
git push
git push origin --tags
echo "DONE"
echo


echo "Building and pushing distrace/base docker image to docker hub..."
$TOPDIR/gradlew createDockerFile
docker login
docker build -t distrace/base:$TAG -t distrace/base:latest $TOPDIR/docker/distrace/base
#docker push distrace/base:$TAG
#docker push distrace/base:latest