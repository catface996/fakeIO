# Socket

## 常用命令

~~~shell

## 查看网络连接
netstat -natp

## 监控tcp连接
tcpdump -nn -i ens33 port 9090

## 查看进程信息
lsof -op pid

## tcp 连接工具
nc 192.168.168.11 9090

~~~

## socket 建立过程


## tcp 窗口协商

~~~shell
ifconfig  
## 查看 MTU,数据包大小
## 三次握手中返回的 mss(窗口协商) 是排除包的头等信息后,实际可发送的消息大小
~~~

## tcp 拥塞






