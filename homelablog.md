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
## Dashboard
- `microk8s enable dashboard` on homelab1
- `kubectl expose deployment kubernetes-dashboard --type=LoadBalancer --name=dashboard-external --namespace=kube-system`
- `kubectl get services --namespace=kube-system`
- `kubectl create token default` to get access token
## Registry
- `microk8s enable registry --size=40Gi` on homelab1
#### On homelab1 and homelab2
- `sudo mkdir -p /var/snap/microk8s/current/args/certs.d/192.168.1.101:32000`
- `sudo touch /var/snap/microk8s/current/args/certs.d/192.168.1.101:32000/hosts.toml`
- `sudo nano /var/snap/microk8s/current/args/certs.d/192.168.1.101:32000/hosts.toml` with
``` 
# /var/snap/microk8s/current/args/certs.d/192.168.1.101:32000/hosts.toml
server = "http://192.168.1.101:32000"

[host."http://192.168.1.101:32000"]
capabilities = ["pull", "resolve"]
```
#### on client computer
- `sudo nano /etc/docker/daemon.json` with
```
{
  "insecure-registries" : ["localhost:32000"]
}
```
## metrics
- `helm install prom-stack prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace -f prom-stack-values.yaml`