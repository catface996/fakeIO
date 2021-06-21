# BIO 阻塞IO

在java中,请使用1.4的jdk进行验证

## strace 追踪

~~~shell
## 追踪进程产生的所有子进程,追踪网络相关的系统调用
strace -tt -T -v -ff -o out -s 128 -p 1234 -e trace=network

## 使用nc 建立socket连接
nc 192.168.162.4 9090

~~~