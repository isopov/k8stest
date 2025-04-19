# General log
- install ubuntu server 24.04.2 on box, name homelab1
- assign `192.168.1.101` to it on router
- `sudo apt install iperf3` for tests
- install ubuntu server on second box, homelab2
- assign 192.168.1.102 to it on router
- `sudo snap install microk8s --classic` on homelab1
- `sudo usermod -a -G microk8s isopov` on homelab1
- `sudo snap install microk8s --classic` on homelab2
- `sudo usermod -a -G microk8s isopov` on homelab2
- `microk8s add-node` on homelab1
- `microk8s join 192.168.1.101:25000/... -worker` on homelab2
- `microk8s.config` on homelab1 to get kubeconfig for connecting to this k8s
## metallb
#### From 
- https://microk8s.io/docs/addon-metallb 
- https://kubernetes.io/docs/tutorials/stateless-application/expose-external-ip-address/
#### Log
- `microk8s enable metallb:192.168.1.201-192.168.1.232` on homelab1
- `kubectl apply -f https://k8s.io/examples/service/load-balancer-example.yaml` to deploy to k8s
- `kubectl expose deployment hello-world --type=LoadBalancer --name=hello-world-service` to expose it via metallb
- `kubectl get services` to get this load balancer external address and port (got 192.168.1.201:8080)
## Dashboard
- `microk8s enable dashboard` on homelab1
- `kubectl expose deployment kubernetes-dashboard --type=LoadBalancer --name=dashboard-external --namespace=kube-system`
- `kubectl get services --namespace=kube-system`
- `kubectl create token default` to get access token
## metrics
- `microk8s enable observability` on homelab1
- `kubectl expose deployment kube-prom-stack-grafana --namespace=observability --type=LoadBalancer --name=grafana-external` - default grafana user/password is admin/prom-operator
- `kubectl expose service kube-prom-stack-kube-prome-prometheus --namespace=observability --type=LoadBalancer --name=prometheus-external`
- `kubectl get services --namespace=observability`