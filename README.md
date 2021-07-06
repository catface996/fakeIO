# fakeIO

IO相关内容

## linux 系统调函数

http://www.chinastor.com/manuals/linuxfunctions/

### 需要区分 read()/write(); recv()/send(); recvfrom()/sendto() 系统调用的区别

https://blog.csdn.net/sea_snow/article/details/112260750

## 常用工具

* nc(netcat)

* tcpdump

* lsof

* strace

## TCP 三次握手和四次分手

分手时,接收到对方的请求,不能立即断开,可能存在未完成的工作,待完成后,才能发送

TIME_WAIT 2倍的MSL,先发起关闭的一端等待,因为最后一次ACK未必能到达. 正常都出现在服务端. 多等待2倍时间,会消耗更多资源,消耗socket四元组的规则,相同的对端不能在短时间使用同一个四元组
不是DDOS攻击,别人的IP可以继续连接上来

可以通过修改内核配置调整等待时间

~~~shell
[root@catface epoll]# sysctl -a | grep reuse
net.ipv4.tcp_tw_reuse = 2
## 将net.ipv4.tcp_tw_reuse 配置成1可以快速重复使用
~~~

## [Buffer](src/main/java/com/io/buffer/README.md)



## [FileIO](src/main/java/com/io/file/README.md)



## [Socket](src/main/java/com/io/netty/README.md)



## [Netty](src/main/java/com/io/netty/README.md)



