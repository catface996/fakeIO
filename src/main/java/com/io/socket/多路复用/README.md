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


