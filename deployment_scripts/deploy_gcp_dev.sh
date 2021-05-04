export GIT_SHA=$(git rev-parse HEAD)
export REGISTRY_URL="gcr.io/mlops-engineering-lab"

# docker build -f ./kafka/Dockerfile -t ${REGISTRY_URL}/kafka:${GIT_SHA} ./kafka


docker build  -f ./kafka-connect/Dockerfile -t ${REGISTRY_URL}/kafka-connect:${GIT_SHA} ./kafka-connect
docker push ${REGISTRY_URL}/kafka-connect:${GIT_SHA}


mvn clean package -f ./queryconsumeroffsets/pom.xml
mvn clean package -f ./profilekafkastreams/pom.xml
mvn clean package -f ./metadata/pom.xml
mvn clean package -f ./kafkaadmin/pom.xml
cd jobserver
mvn clean package
cd ..
mvn clean package -f ./flowkafkastreams/pom.xml
mvn clean package -f ./eventview/pom.xml
cd consumeroffsetsparser
mvn clean package
cd ..



docker build  -f ./queryconsumeroffsets/Dockerfile.dev -t ${REGISTRY_URL}/queryconsumeroffsets:${GIT_SHA} ./queryconsumeroffsets
docker push ${REGISTRY_URL}/queryconsumeroffsets:${GIT_SHA}

docker build  -f ./profilekafkastreams/Dockerfile.dev -t ${REGISTRY_URL}/profilekafkastreams:${GIT_SHA} ./profilekafkastreams
docker push ${REGISTRY_URL}/profilekafkastreams:${GIT_SHA}

docker build  -f ./metadata/Dockerfile.dev -t ${REGISTRY_URL}/metadata:${GIT_SHA} ./metadata
docker push ${REGISTRY_URL}/metadata:${GIT_SHA}

docker build  -f ./kafkaadmin/Dockerfile.dev -t ${REGISTRY_URL}/kafkaadmin:${GIT_SHA} ./kafkaadmin
docker push ${REGISTRY_URL}/kafkaadmin:${GIT_SHA}

docker build  -f ./jobserver/Dockerfile.dev -t ${REGISTRY_URL}/jobserver:${GIT_SHA} ./jobserver
docker push ${REGISTRY_URL}/jobserver:${GIT_SHA}


docker build  -f ./flowkafkastreams/Dockerfile.dev -t ${REGISTRY_URL}/flowkafkastreams:${GIT_SHA} ./flowkafkastreams
docker push ${REGISTRY_URL}/flowkafkastreams:${GIT_SHA}

docker build  -f ./eventview/Dockerfile.dev -t ${REGISTRY_URL}/eventview:${GIT_SHA} ./eventview
docker push ${REGISTRY_URL}/eventview:${GIT_SHA}


docker build  -f ./consumeroffsetsparser/Dockerfile.dev -t ${REGISTRY_URL}/consumeroffsetsparser:${GIT_SHA} ./consumeroffsetsparser
docker push ${REGISTRY_URL}/consumeroffsetsparser:${GIT_SHA}


docker build -t ${REGISTRY_URL}/client:${GIT_SHA} ./izac
docker push ${REGISTRY_URL}/client:${GIT_SHA}

docker build -t ${REGISTRY_URL}/server:${GIT_SHA} ./izacserver
docker push ${REGISTRY_URL}/server:${GIT_SHA}

kubectl apply -f ./k8s/

kubectl set image deployments/server-deployment server=${REGISTRY_URL}/server:${GIT_SHA}
kubectl set image deployments/client-deployment client=${REGISTRY_URL}/client:${GIT_SHA}

curl -o /tmp/get_helm.sh -LO https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3

chmod 700 /tmp/get_helm.sh
/tmp/get_helm.sh --version v3.2.3

helm upgrade --install izac ./izac-helm-charts --set cp-kafka-connect.image=${REGISTRY_URL}/kafka-connect --set cp-kafka-connect.imageTag=${GIT_SHA} --set consumeroffsetsparser.image.repository=${REGISTRY_URL}/consumeroffsetsparser --set consumeroffsetsparser.image.tag=${GIT_SHA} --set eventview.image.repository=${REGISTRY_URL}/eventview --set eventview.image.tag=${GIT_SHA} --set kafkaadmin.image.repository=${REGISTRY_URL}/kafkaadmin --set kafkaadmin.image.tag=${GIT_SHA} --set metadata.image.repository=${REGISTRY_URL}/metadata --set metadata.image.tag=${GIT_SHA} --set jobserver.image.repository=${REGISTRY_URL}/jobserver --set jobserver.image.tag=${GIT_SHA} --set jobserver.images.flowimagewithtag=${REGISTRY_URL}/flowkafkastreams:${GIT_SHA} --set jobserver.images.profileimagewithtag=${REGISTRY_URL}/profilekafkastreams:${GIT_SHA} --set queryconsumeroffsets.image.repository=${REGISTRY_URL}/queryconsumeroffsets --set queryconsumeroffsets.image.tag=${GIT_SHA}


kubectl create clusterrolebinding jobserver-binding --clusterrole=admin --serviceaccount default:izac-jobserver  --dry-run -o yaml | kubectl apply -f -

# helm repo add stable https://kubernetes-charts.storage.googleapis.com
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo add stable https://charts.helm.sh/stable
helm repo update

helm upgrade  --install nginx-ingress stable/nginx-ingress
