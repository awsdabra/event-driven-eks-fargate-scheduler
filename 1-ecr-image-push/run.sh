docker build -t scan-repo .
docker tag scan-repo:latest $1
docker push $1