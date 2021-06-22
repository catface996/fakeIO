# NIO

在java中是 new io,新一代io包装了linux内核的非阻塞io 在linux内核中是 none block io

## 非阻塞的优势

* 可以在一个线程中完成 accept()和对已经建立的所有socket连接的读取,节省线程,提升系统吞吐量

## strace 追踪

~~~shell
## 追踪进程产生的所有子进程,追踪网络相关的系统调用
strace -tt -T -v -ff -o out -s 128 -p 1234 -e trace=network

## 使用nc 建立socket连接
nc 192.168.162.4 9090


## 系统调用过程
## 主线程 pid 5964
socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 6
setsockopt(6, SOL_IPV6, IPV6_V6ONLY, [0], 4) = 0
setsockopt(6, SOL_SOCKET, SO_REUSEADDR, [1], 4) = 0
bind(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, 28) = 0
listen(6, 50)                           = 0
getsockname(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, [28]) = 0
getsockname(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, [28]) = 0
fcntl(6, F_GETFL)                       = 0x2 (flags O_RDWR)
fcntl(6, F_SETFL, O_RDWR|O_NONBLOCK)    = 0
accept(6, 0x7f09800ead10, [28])         = -1 EAGAIN (Resource temporarily unavailable)
accept(6, 0x7f09800ead10, [28])         = -1 EAGAIN (Resource temporarily unavailable)
##若干次accept()调用...
accept(6, 0x7f09800ead10, [28])         = -1 EAGAIN (Resource temporarily unavailable)
accept(6, 0x7f09800ead10, [28])         = -1 EAGAIN (Resource temporarily unavailable)
## 接收到请求,返回文件描述符7
accept(6, {sa_family=AF_INET6, sin6_port=htons(64511), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.1", &sin6_addr), sin6_scope_id=0}, [28]) = 7
## 设置新建立的socket连接为非阻塞
fcntl(7, F_GETFL)                       = 0x2 (flags O_RDWR)
getsockname(7, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
getsockname(7, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
fcntl(7, F_GETFL)                       = 0x2 (flags O_RDWR)
fcntl(7, F_SETFL, O_RDWR|O_NONBLOCK)    = 0
## 主线程中for循环遍历所有的socket连接,读取数据,如果连接中无可读取的数据,系统调用read()返回-1
read(7, 0x7f08f25ecca0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
read(7, 0x7f08f25ecca0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
## 中间有若干次read()调用...
read(7, 0x7f08f25edcb0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
read(7, 0x7f08f25eecc0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
## 主线程读取socket连接,接收到数据
read(7, "121212\n", 4096)               = 7
## 主线程中for循环遍历所有的socket连接,读取数据,如果连接中无可读取的数据,系统调用read()返回-1
read(7, 0x7f08f25ecca0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
read(7, 0x7f08f25ecca0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
## 中间有若干次read()调用...
read(7, 0x7f08f25edcb0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
read(7, 0x7f08f25eecc0, 4096)           = -1 EAGAIN (Resource temporarily unavailable)
## 主线程读取socket连接,接收到数据
read(7, "33333\n", 4096)                = 6
~~~
