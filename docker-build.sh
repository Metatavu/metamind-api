 mvn clean package
docker build -t metamind-api .
docker tag $(docker images -q metamind-api) metatavu/metamind-api:develop
docker push metatavu/metamind-api
