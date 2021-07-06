# NIO和BIO比对

| system call() | bio              | nio        |
| ------------- | ---------------- | ---------- |
| socket()      | 同步不阻塞       | 同步不阻塞 |
| bind()        | 同步不阻塞       | 同步不阻塞 |
| listen()      | 同步不阻塞       | 同步不阻塞 |
| accept()      | 同步阻塞当前线程 | 异步       |
| recv()        | 同步阻塞子线程   | 异步       |
| read()          | 同步阻塞子线程   | 异步       |

PS:在NIO中,accept(),recv(),read()执行都是同步的,例如read(),在执行读取时,如果有数据,当前线程将被阻塞到内核返回读取的数据结束.但是如果内核告诉线程当前无可读取的数据,调用read()
的线程将会继续执行,不会阻塞.



## [Socket基本流程](./_1socket基本流程/README.md)



## [十万连接](./_2十万连接/README.md)



## [BIO](./_3bio/README.md)



## [NIO](./_3nio/README.md)



## [Selector](./_4selector/README.md)

### [SelectGroup](./_4selectorGroup/README.md)

