export GIT_SHA=$(git rev-parse HEAD)

# docker build -f ./kafka/Dockerfile -t whiteklay/kafka:${GIT_SHA} ./kafka

docker build  -f ../components/kafka-connect/Dockerfile -t prohankumar/kafka-connect:6.1.0 ../components/kafka-connect

docker push prohankumar/kafka-connect:6.1.0


helm upgrade --install izac ../helm-charts --set cp-kafka-connect.image=prohankumar/kafka-connect
