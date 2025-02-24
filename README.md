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

Build this sample app
```
/mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=isopov/k8stest
```

Load image built into kluster
```
kind load docker-image isopov/k8stest
```

Deploy to k8s cluster
```
helm install --create-namespace --namespace k8stest k8stest charts/k8stest
```

Get local ip of the k8s node. Access this app via it http://172.18.0.2:30080/factorial/10
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