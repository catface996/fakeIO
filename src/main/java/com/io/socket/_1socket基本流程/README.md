# Socket

## 常用命令

~~~shell

## 查看网络连接
netstat -natp

## 监控系统调用
strace -tt -T -v -f -ff -o out -s 128 -p 1234

## 监控tcp连接
tcpdump -nn -i ens33 port 9090

## 查看进程信息
lsof -op pid

## tcp 连接工具
nc 192.168.168.11 9090

~~~

## socket 建立过程

~~~shell
## jps 获得java进程的pid
5728

### 三次无握手,客户端 192.168.162.1 
23:06:37.119321 IP 192.168.162.1.53092 > 192.168.162.4.9090: Flags [SEW], seq 2354840753, win 65535, options [mss 1460,nop,wscale 6,nop,nop,TS val 3154365891 ecr 0,sackOK,eol], length 0
23:06:37.119390 IP 192.168.162.4.9090 > 192.168.162.1.53092: Flags [S.E], seq 2548474671, ack 2354840754, win 1152, options [mss 1460,sackOK,TS val 2101506249 ecr 3154365891,nop,wscale 0], length 0
23:06:37.120620 IP 192.168.162.1.53092 > 192.168.162.4.9090: Flags [.], ack 1, win 2058, options [nop,nop,TS val 3154365891 ecr 2101506249], length 0

## 此时已经建立了tcp连接,但是连接未分配进程
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp6       0      0 :::9090                 :::*                    LISTEN      5728/java
tcp6       0      0 192.168.162.4:9090      192.168.162.1:53092     ESTABLISHED -   (已建立连接,但未分配进程,此时已经可以在传输层通信)

## 调用 Socket.accept()方法后,建立的连接归属给java进程
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp6       0      0 192.168.162.4:9090      192.168.162.1:53092     ESTABLISHED 5728/java

### 第一次客户端给服务端发送 "1\n",第二次发送"2\n"
23:08:54.311980 IP 192.168.162.1.53092 > 192.168.162.4.9090: Flags [P.], seq 1:3, ack 1, win 2058, options [nop,nop,TS val 3154502785 ecr 2101506249], length 2
23:08:54.312082 IP 192.168.162.4.9090 > 192.168.162.1.53092: Flags [.], ack 3, win 1150, options [nop,nop,TS val 2101643442 ecr 3154502785], length 0

23:09:03.291120 IP 192.168.162.1.53092 > 192.168.162.4.9090: Flags [P.], seq 3:5, ack 1, win 2058, options [nop,nop,TS val 3154511736 ecr 2101643442], length 2
23:09:03.291153 IP 192.168.162.4.9090 > 192.168.162.1.53092: Flags [.], ack 5, win 1148, options [nop,nop,TS val 2101652421 ecr 3154511736], length 0

## 建立到通信过程产生的系统调用
## 创建socket,返回文件描述符26
7839  01:16:20.747512 socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 26 <0.000176>
## 设置socket相关参数,入参,文件描述符26,其他参数
7839  01:16:20.747810 setsockopt(26, SOL_IPV6, IPV6_V6ONLY, [0], 4) = 0 <0.000162>
7839  01:16:20.748523 setsockopt(26, SOL_SOCKET, SO_REUSEADDR, [1], 4) = 0 <0.000058>
## 绑定端口
7839  01:16:20.748760 bind(26, {sa_family=AF_INET6, sin6_port=htons(9090), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::", &sin6_addr), sin6_scope_id=0}, 28) = 0 <0.000150>
## 开始监听,入参,文件描述符26,其他参数
7839  01:16:20.749079 listen(26, 2)     = 0 <0.000093>
## 设置socket相关参数,入参,文件描述符26,其他参数
7839  01:16:20.749340 setsockopt(26, SOL_SOCKET, SO_RCVBUF, [1024], 4) = 0 <0.000081>
7839  01:16:20.749578 setsockopt(26, SOL_SOCKET, SO_REUSEADDR, [0], 4) = 0 <0.000154>
## 由于是监控的java进程的相关操作,socket开始接收请求之前的三次握手,tcp连接的建立,还未归属到进程7839,所以strace未能追踪到
## 开始接收连接
7839  01:16:32.389986 accept(26, {sa_family=AF_INET6, sin6_port=htons(54127), sin6_flowinfo=htonl(0), inet_pton(AF_INET6, "::ffff:192.168.162.1", &sin6_addr), sin6_scope_id=0}, [28]) = 27 <0.000255>
7839  01:16:32.391551 setsockopt(27, SOL_SOCKET, SO_KEEPALIVE, [0], 4) = 0 <0.000054>
7839  01:16:32.391815 setsockopt(27, SOL_SOCKET, SO_OOBINLINE, [0], 4) = 0 <0.000149>
7839  01:16:32.392174 setsockopt(27, SOL_SOCKET, SO_RCVBUF, [1024], 4) = 0 <0.000224>
7839  01:16:32.392659 setsockopt(27, SOL_SOCKET, SO_REUSEADDR, [0], 4) = 0 <0.000190>
7839  01:16:32.393146 setsockopt(27, SOL_SOCKET, SO_SNDBUF, [20], 4) = 0 <0.000156>
7839  01:16:32.393547 setsockopt(27, SOL_SOCKET, SO_LINGER, {l_onoff=1, l_linger=0}, 8) = 0 <0.000041>
7839  01:16:32.393763 setsockopt(27, SOL_TCP, TCP_NODELAY, [0], 4) = 0 <0.000165>
## java进程从tcp连接的缓冲区读取数据,第一次读取"1\n",第二次读取"2\n",recvfrom是非阻塞函数
## accept()返回新的文件描述符后,可以通过,read(fd)(阻塞读),recv(fd)(非阻塞读)
7915  01:16:32.511138 recvfrom(27, "1\n", 8192, 0, NULL, NULL) = 2 <1.115948>
7915  01:16:33.628513 recvfrom(27, "2\n", 8192, 0, NULL, NULL) = 2 <0.700276>
~~~

## tcp 窗口协商

~~~shell
ifconfig  
## 查看 MTU,数据包大小
## 三次握手中返回的 mss(窗口协商) 是排除包的头等信息后,实际可发送的消息大小
~~~

## tcp 拥塞

## 扩展阅读

~~~ shell
##man 2 recv

NAME
       recv, recvfrom, recvmsg - receive a message from a socket

SYNOPSIS
       #include <sys/types.h>
       #include <sys/socket.h>

       ssize_t recv(int sockfd, void *buf, size_t len, int flags);

       ssize_t recvfrom(int sockfd, void *buf, size_t len, int flags,
                        struct sockaddr *src_addr, socklen_t *addrlen);

       ssize_t recvmsg(int sockfd, struct msghdr *msg, int flags);

DESCRIPTION
       The recv(), recvfrom(), and recvmsg() calls are used to receive messages from a socket.  They may be used to receive data on both connectionless and connection-oriented sockets.  This page first describes common features of all three system
       calls, and then describes the differences between the calls.

       The only difference between recv() and read(2) is the presence of flags.  With a zero flags argument, recv() is generally equivalent to read(2) (but see NOTES).  Also, the following call

           recv(sockfd, buf, len, flags);

       is equivalent to

           recvfrom(sockfd, buf, len, flags, NULL, NULL);

       All three calls return the length of the message on successful completion.  If a message is too long to fit in the supplied buffer, excess bytes may be discarded depending on the type of socket the message is received from.

       If no messages are available at the socket, the receive calls wait for a message to arrive, unless the socket is nonblocking (see fcntl(2)), in which case the value -1 is returned and the external variable errno is set to EAGAIN or  EWOULD‐
       BLOCK.  The receive calls normally return any data available, up to the requested amount, rather than waiting for receipt of the full amount requested.

       An application can use select(2), poll(2), or epoll(7) to determine when more data arrives on a socket.
       
~~~





