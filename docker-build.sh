mvn clean package
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')
docker build -t metamind-api .
docker tag $(docker images -q metamind-api) metatavu/metamind-api:$VERSION
docker push metatavu/metamind-api
