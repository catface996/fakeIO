# NIO

在java中是 new io,新一代io包装了linux内核的非阻塞io 在linux内核中是 none block io

### 非阻塞的优势

* 可以在一个线程中完成 accept()和对已经建立的所有socket连接的读取,节省线程,提升系统吞吐量

