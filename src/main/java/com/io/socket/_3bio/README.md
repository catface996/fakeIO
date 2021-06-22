# BIO 阻塞IO

在java中,请使用1.4的jdk进行验证

## strace 追踪

~~~shell
## 追踪进程产生的所有子进程,追踪网络相关的系统调用
strace -tt -T -v -ff -o out -s 128 -p 1234 -e trace=\%net
strace -ff -o out -s 128 -p 1234 -e trace=\%net

## 使用nc 建立socket连接
nc 192.168.162.4 9090


## BIO 系统调用过程
## 主线程 pid 4928
socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 6
setsockopt(6, SOL_IPV6, IPV6_V6ONLY, [0], 4) = 0
setsockopt(6, SOL_SOCKET, SO_REUSEADDR, [1], 4) = 0
bind(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, 28) = 0
listen(6, 50)                           = 0
## 主线程在调用accept(),如果没有请求,会被阻塞
## accept(6, 阻塞时
accept(6, {sa_family=AF_INET6, sin6_port=htons(64246), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.1", &sin6_addr), sin6_scope_id=0}, [28]) = 7
## 接收到请求后,返回新的文件描述符7,设置socket连接为阻塞模式
fcntl(7, F_GETFL)                       = 0x2 (flags O_RDWR)
getsockname(7, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
getsockname(7, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
## 子线程 pid 4997
## 子线程调用read()系统调用时,未读取到数据时,阻塞
read(7, "121212\n", 4096)               = 7
## 接收到数据后,做后续处理,继续读取,如果未读取到数据,继续阻塞
read(7, "323232323\n", 4096)            = 10
~~~