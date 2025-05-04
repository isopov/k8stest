Create local k8s cluster with kind (based on https://medium.com/@tylerauerbeck/metallb-and-kind-loads-balanced-locally-1992d60111d8)
```
kind create cluster --config kind-config.yaml
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.14.9/config/manifests/metallb-native.yaml
kubectl apply -f metallb-config.yaml
```

Install metrics-server to it (without it HPA will not work)
```
helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
helm repo update
helm upgrade --install --set args={--kubelet-insecure-tls} metrics-server metrics-server/metrics-server --namespace kube-system
```

Build this sample app and upload to local kind cluster
```
./build.sh
```

Deploy to k8s cluster (postgresql, kafka, redis will be installed as held deps)
```
helm upgrade --dependency-update --install --create-namespace --namespace k8stest k8stest charts/k8stest
```

Check IP metallb given to this app loadbalancer
```
$ kubectl get services -n k8stest
```
Access API with something like http://172.18.255.1:8080/load/factorial/5

Install prom,am,grafana to k8s
```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://charts.helm.sh/stable
helm repo update
helm install kind-prometheus prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace -f prom-stack-values.yaml
kubectl get services -n monitoring
```
Access grafana with something like http://172.18.255.2/
