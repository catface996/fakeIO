
## jmv堆上分配

Buffer.allocate() 分配在jvm heap上
~~~shell
Memory                                             used             total            max              usage            GC
heap                                               1065M            1453M            7097M            15.01%           gc.ps_scavenge.count                                       1
ps_eden_space                                      16M              125M             2620M            0.64%            gc.ps_scavenge.time(ms)                                    12
ps_survivor_space                                  0K               20992K           20992K           0.00%            gc.ps_marksweep.count                                      1
ps_old_gen                                         1048M            1307M            5323M            19.70%           gc.ps_marksweep.time(ms)                                   51
nonheap(方法去)                                     30M              31M              -1               97.56%
code_cache                                         5M               5M               240M             2.41%
metaspace                                          21M              22M              -1               97.62%
compressed_class_space                             2M               2M               1024M            0.27%
direct                                             8K               8K               -                100.01%
mapped                                             0K               0K               -                0.00%
~~~

## java进程堆上分配(直接内存)

~~~shell
Memory                                             used             total            max              usage            GC
heap                                               38M              301M             7097M            0.55%            gc.ps_scavenge.count                                       1
ps_eden_space                                      14M              125M             2620M            0.55%            gc.ps_scavenge.time(ms)                                    12
ps_survivor_space                                  0K               20992K           20992K           0.00%            gc.ps_marksweep.count                                      1
ps_old_gen                                         24M              155M             5323M            0.46%            gc.ps_marksweep.time(ms)                                   31
nonheap                                            29M              30M              -1               96.27%
code_cache                                         4M               5M               240M             2.06%
metaspace                                          21M              22M              -1               97.13%
compressed_class_space                             2M               2M               1024M            0.27%
direct                                             1024M            1024M            -                100.00%
mapped                                             0K               0K               -                0.00%
~~~

## 查看进程使用的内存(VmRSS)

~~~shell
[root@catface 7300]# cat status
Name:	java
Umask:	0022
State:	S (sleeping)
Tgid:	7300
Ngid:	0
Pid:	7300
PPid:	5576
TracerPid:	0
Uid:	1000	1000	1000	1000
Gid:	1000	1000	1000	1000
FDSize:	256
Groups:	10 1000
NStgid:	7300
NSpid:	7300
NSpgid:	2384
NSsid:	2384
VmPeak:	13329436 kB
VmSize:	13329436 kB
VmLck:	       0 kB
VmPin:	       0 kB
VmHWM:	 1299220 kB
VmRSS:	 1299220 kB
RssAnon:	 1279640 kB
RssFile:	   19580 kB
RssShmem:	       0 kB
~~~

## 使用UnSafe分配内存


## 直接内存分配限制

~~~shell
## 对UnSafe无效,对DirectByteBuffer 有效
-XX:MaxDirectMemorySize
~~~

使用Unsafe分配的内存
~~~shell
## direct memory中未体现
Memory                                             used             total            max              usage            GC
heap                                               129M             301M             7097M            1.83%            gc.ps_scavenge.count                                       1
ps_eden_space                                      105M             125M             2620M            4.02%            gc.ps_scavenge.time(ms)                                    11
ps_survivor_space                                  0K               20992K           20992K           0.00%            gc.ps_marksweep.count                                      1
ps_old_gen                                         24M              155M             5323M            0.46%            gc.ps_marksweep.time(ms)                                   35
nonheap                                            31M              32M              -1               97.69%
code_cache                                         7M               7M               240M             2.95%
metaspace                                          22M              22M              -1               97.76%
compressed_class_space                             2M               2M               1024M            0.27%
direct                                             8K               8K               -                100.01%
mapped                                             0K               0K               -                0.00%

## linux java进程中,VmRSS中体现了2G内存的分配
[catface@catface 13899]$ cat /proc/13899/status
Name:	java
Umask:	0022
State:	S (sleeping)
Tgid:	13899
Ngid:	0
Pid:	13899
PPid:	5576
TracerPid:	0
Uid:	1000	1000	1000	1000
Gid:	1000	1000	1000	1000
FDSize:	256
Groups:	10 1000
NStgid:	13899
NSpid:	13899
NSpgid:	2384
NSsid:	2384
VmPeak:	14378012 kB
VmSize:	14378012 kB
VmLck:	       0 kB
VmPin:	       0 kB
VmHWM:	  256384 kB
VmRSS:	  252816 kB
RssAnon:	  233208 kB
RssFile:	   19608 kB
RssShmem:	       0 kB
VmData:	 2862424 kB
VmStk:	     136 kB
VmExe:	       4 kB
VmLib:	   20048 kB
VmPTE:	    1004 kB
VmSwap:	       0 kB
~~~

## mapped memory

~~~shell
## 使用arthas 追踪java进程
Memory                                             used             total            max              usage            GC
heap                                               41M              300M             7097M            0.58%            gc.ps_scavenge.count                                       1
ps_eden_space                                      17M              125M             2620M            0.65%            gc.ps_scavenge.time(ms)                                    17
ps_survivor_space                                  0K               20992K           20992K           0.00%            gc.ps_marksweep.count                                      1
ps_old_gen                                         24M              154M             5323M            0.46%            gc.ps_marksweep.time(ms)                                   32
nonheap                                            30M              30M              -1               97.59%
code_cache                                         5M               5M               240M             2.35%
metaspace                                          21M              22M              -1               97.55%
compressed_class_space                             2M               2M               1024M            0.27%
direct                                             8K               8K               -                100.01%
mapped                                             887M             887M             -                100.00%
~~~


## direct的优劣

* 优势
  * 不收GC影响,空间稳定
    
* 劣势
  * 需要自己申请和释放,溢出等问题难以排查
    
## mapped memory和direct memory的区别

direct memory 省去从jvm的heap中copy到java进程中的heap的操作,直接写入到java进程的heap中.

持久化到磁盘或者发送到网卡时,仍旧需要从java进程的内存空间copy到内核的内存空间.


mapped memory 直接将文件的page cache(内核空间的内存)映射给java进程的heap空间,java进程可以直接在自己的虚拟地址空间中操作page cache.

当文件要持久化到磁盘时,无需再从java进程的内存空间copy到page cache(内核的内存空间).
