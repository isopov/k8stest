Create local k8s cluster with kind
```
kind create cluster
```

Install metrics-server to it (without it HPA will not work)
```
helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
helm repo update
helm upgrade --install --set args={--kubelet-insecure-tls} metrics-server metrics-server/metrics-server --namespace kube-system
```

Install postgres
```
helm install --namespace postgresql --create-namespace postgresql bitnami/postgresql

export POSTGRES_PASSWORD=$(kubectl get secret --namespace postgresql postgresql -o jsonpath="{.data.postgres-password}" | base64 -d)
kubectl run postgresql-client --rm --tty -i --restart='Never' --namespace postgresql \
        --image docker.io/bitnami/postgresql:17.4.0-debian-12-r2 --env="PGPASSWORD=$POSTGRES_PASSWORD" \
        --command -- psql --host postgresql -U postgres -d postgres -p 5432

create database k8stest;
create user k8stest with password 'k8stest';
alter database k8stest owner TO k8stest;
```
Check created db and user
```
kubectl run postgresql-client --rm --tty -i --restart='Never' --namespace postgresql \
        --image docker.io/bitnami/postgresql:17.4.0-debian-12-r2 --env="PGPASSWORD=k8stest" \
        --command -- psql --host postgresql -U k8stest -d k8stest -p 5432
```
Install redis
```
helm install --namespace=redis --create-namespace redis bitnami/redis --set auth.password=k8stest
```
Install kafka
```
helm install --namespace kafka --create-namespace kafka bitnami/kafka
```
Get kafka password
```
kubectl get secret kafka-user-passwords --namespace kafka -o jsonpath='{.data.client-passwords}' | base64 -d | cut -d , -f 1
```
Build this sample app
```
./mvnw spring-boot:build-image
```

Load image built into kluster
```
kind load docker-image k8stest:0.1.4
```

Deploy to k8s cluster
```
helm install --create-namespace --namespace k8stest k8stest charts/k8stest
```

Or after some changes upgrade to newer version
```
helm upgrade --namespace k8stest k8stest charts/k8stest
```

Get local ip of the k8s node. Access this app via it http://172.18.0.2:30080/load/factorial/10
```
docker inspect kind-control-plane | grep IPAddress
```

Install prom,am,grafana to k8s
```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://charts.helm.sh/stable
helm repo update
kubectl create namespace monitoring
helm install kind-prometheus prometheus-community/kube-prometheus-stack --namespace monitoring --set prometheus.service.nodePort=30000 --set prometheus.service.type=NodePort --set grafana.service.nodePort=31000 --set grafana.service.type=NodePort --set alertmanager.service.nodePort=32000 --set alertmanager.service.type=NodePort --set prometheus-node-exporter.service.nodePort=32001 --set prometheus-node-exporter.service.type=NodePort
```