# 多路复用器

## 常用命令

~~~shell
## 查看epoll红黑树的大小限制
[root@catface epoll]# cat /proc/sys/fs/epoll/max_user_watches
6684590
~~~

## 相关系统调用

* select(2)

* poll(2)

* epoll_create(2) epoll_ctl(2) epoll_wait(2)

## select poll epoll

select() 是POSIX标准,大部分操作系统都会实现 poll()和epoll()是linux系统的多路复用增强

相对于普通NIO,多路复用减少了读取socket数据时的系统调用次数.

普通NIO,需要用户线程不断轮询建立的socket连接.

select支持一次轮询最多1024个socket连接,只是获取socket连接的状态,并没有读取数据.

example:

现在有200个socket连接,其中50个已经处于可读取状态

~~~shell
## 普通NIO处理方式,需要200次系统调用
socket[] sokcets = socket[200];
for(int i=0;i<200;i++){
  sockets[i].read();
}

## 多路复用器的处理方式,需要51次系统调用
socket[] sokcets = socket[200];
socket[] canRead = select(sokcets);
for(int i=0;i<canRead.length;i++){
  sockets[i].read();
}
~~~

## epoll 系统调用分析

~~~shell
## 主进程ID 10226
## 创建socket server
socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 6
setsockopt(6, SOL_IPV6, IPV6_V6ONLY, [0], 4) = 0
setsockopt(6, SOL_SOCKET, SO_REUSEADDR, [1], 4) = 0
fcntl(6, F_GETFL)                       = 0x2 (flags O_RDWR)
fcntl(6, F_SETFL, O_RDWR|O_NONBLOCK)    = 0
## 绑定端口
bind(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, 28) = 0
## 开启监听
listen(6, 50)  
      
getsockname(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, [28]) = 0
getsockname(6, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, [28]) = 0

## 创建epoll
epoll_create(256)                       = 9

## 添加接收请求描述符到epoll
epoll_ctl(9, EPOLL_CTL_ADD, 6, {EPOLLIN, {u32=6, u64=139762530779142}}) = 0

## 调用 epoll_wait(),此时会阻塞
epoll_wait(9, [{EPOLLIN, {u32=6, u64=139762530779142}}], 8192, -1) = 1

## 接收到建立连接请求
accept(6, {sa_family=AF_INET6, sin6_port=htons(52380), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.1", &sin6_addr), sin6_scope_id=0}, [28]) = 10
## 设置新建立的连接未非阻塞
fcntl(10, F_GETFL) 
getsockname(10, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
getsockname(10, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.4", &sin6_addr), sin6_scope_id=0}, [28]) = 0
fcntl(10, F_GETFL)                      = 0x2 (flags O_RDWR)
fcntl(10, F_SETFL, O_RDWR|O_NONBLOCK)   = 0
## 将新建立的socket连接对应的文件描述符10加入到epoll
epoll_ctl(9, EPOLL_CTL_ADD, 10, {EPOLLIN, {u32=10, u64=139762530779146}}) = 0
## 调用epoll_waint()获取readAble或者acceptAble的文件描述符,此时会阻塞,可以设置超时时间
epoll_wait(9, [{EPOLLIN, {u32=10, u64=139762530779146}}], 8192, -1) = 1
## 读取 socket连接(文件描述符10)接收到的数据
read(10, "1212212121212\n", 1024)       = 14
## 非阻塞读取,返回-1,继续下一个socket读取或者继续epoll_wait()
read(10, 0x7f1d000eb1b0, 1024)          = -1 EAGAIN (Resource temporarily unavailable)
## 调用epoll_waint()获取readAble或者acceptAble的文件描述符,此时会阻塞,可以设置超时时间
epoll_wait(9, [{EPOLLIN, {u32=10, u64=139762530779146}}], 8192, -1) = 1
## 读取 socket连接(文件描述符10)接收到的数据
read(10, "565656567878\n", 1024)        = 13
read(10, 0x7f1d0010c100, 1024)          = -1 EAGAIN (Resource temporarily unavailable)
epoll_wait(9, [{EPOLLIN, {u32=6, u64=139762530779142}}], 8192, -1) = 1
## 接收到新的建立连接请求
accept(6, {sa_family=AF_INET6, sin6_port=htons(52391), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.1", &sin6_addr), sin6_scope_id=0}, [28]) = 12
fcntl(12, F_GETFL)                      = 0x2 (flags O_RDWR)
###.......
~~~


