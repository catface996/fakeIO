
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