
# 磁盘文件读写

通过sysctl查看脏页的相关配置
~~~shell
[root@catface vm]# sysctl -a | grep dirty
vm.dirty_background_bytes = 0
vm.dirty_background_ratio = 10
vm.dirty_bytes = 0
vm.dirty_expire_centisecs = 3000
vm.dirty_ratio = 30
vm.dirty_writeback_centisecs = 500
vm.dirtytime_expire_seconds = 43200
~~~

对应的内核映射文件
~~~shell
[root@catface ~]# ll /proc/sys/vm | grep dirty
-rw-r--r--. 1 root root 0 6月  16 02:02 dirty_background_bytes
-rw-r--r--. 1 root root 0 6月  15 23:30 dirty_background_ratio
-rw-r--r--. 1 root root 0 6月  16 02:02 dirty_bytes
-rw-r--r--. 1 root root 0 6月  16 02:02 dirty_expire_centisecs
-rw-r--r--. 1 root root 0 6月  15 23:30 dirty_ratio
-rw-r--r--. 1 root root 0 6月  16 02:02 dirtytime_expire_seconds
-rw-r--r--. 1 root root 0 6月  16 02:02 dirty_writeback_centisecs
~~~

## 配置项解释

vm.dirty_background_ratio:这个参数指定了当文件系统缓存脏页数量达到系统内存百分之多少时（如5%）就会触发pdflush/flush/kdmflush等后台回写进程运行，将一定缓存的脏页异步地刷入外存；

vm.dirty_ratio:而这个参数则指定了当文件系统缓存脏页数量达到系统内存百分之多少时（如10%），系统不得不开始处理缓存脏页（因为此时脏页数量已经比较多，为了避免数据丢失需要将一定脏页刷入外存）；在此过程中很多应用进程可能会因为系统转而处理文件IO而阻塞。

之前一直错误的一位dirty_ratio的触发条件不可能达到，因为每次肯定会先达到vm.dirty_background_ratio的条件，后来才知道自己理解错了。确实是先达到vm.dirty_background_ratio的条件然后触发flush进程进行异步的回写操作，但是这一过程中应用进程仍然可以进行写操作，如果多个应用进程写入的量大于flush进程刷出的量那自然会达到vm.dirty_ratio这个参数所设定的坎，此时操作系统会转入同步地处理脏页的过程，阻塞应用进程

## 原文解释
~~~shell
$ sysctl -a | grep dirty
vm.dirty_background_ratio = 10
vm.dirty_background_bytes = 0
vm.dirty_ratio = 20
vm.dirty_bytes = 0
vm.dirty_writeback_centisecs = 500
vm.dirty_expire_centisecs = 3000
~~~

vm.dirty_background_ratio is the percentage of system memory that can be filled with “dirty” pages — memory pages that still need to be written to disk — before the pdflush/flush/kdmflush background processes kick in to write it to disk. My example is 10%, so if my virtual server has 32 GB of memory that’s 3.2 GB of data that can be sitting in RAM before something is done.

vm.dirty_ratio is the absolute maximum amount of system memory that can be filled with dirty pages before everything must get committed to disk. When the system gets to this point all new I/O blocks until dirty pages have been written to disk. This is often the source of long I/O pauses, but is a safeguard against too much data being cached unsafely in memory.

vm.dirty_background_bytes and vm.dirty_bytes are another way to specify these parameters. If you set the _bytes version the _ratio version will become 0, and vice-versa.

vm.dirty_expire_centisecs is how long something can be in cache before it needs to be written. In this case it’s 30 seconds. When the pdflush/flush/kdmflush processes kick in they will check to see how old a dirty page is, and if it’s older than this value it’ll be written asynchronously to disk. Since holding a dirty page in memory is unsafe this is also a safeguard against data loss.

vm.dirty_writeback_centisecs is how often the pdflush/flush/kdmflush processes wake up and check to see if work needs to be done.

You can also see statistics on the page cache in /proc/vmstat:

$ cat /proc/vmstat | egrep "dirty|writeback"
nr_dirty 878
nr_writeback 0
nr_writeback_temp 0
In my case I have 878 dirty pages waiting to be written to disk.