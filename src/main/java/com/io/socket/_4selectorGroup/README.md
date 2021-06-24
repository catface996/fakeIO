# Selector分组

## 独立线程执行accept(),可以使用阻塞模式

## socket分组注册到selector中,每组对应一个线程

存在的问题:

* Selector.select()方式是阻塞的,当有新的socket连接建立,加入了调用select()方法的Selector中, 此时由于select()执行完成,所以要等下一次调用select()
  时,才能获取到新建立的socket的状态,进而导致新建立的连接的数据读取有延时

