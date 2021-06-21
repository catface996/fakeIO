# 单机10万连接

定义一个socket连接是通过四元组唯一确定的 clientIP:clientPort->serverIP:serverPort

例如: 192.168.162.5:10000->192.168.162.4:9090 和 192.168.162.6:10000->192.168.162.4:9090 是两个tcp连接,即便

192.168.162.5和192.168.162.6数据同一台物理机.

客户端物理机网络配置:

<img src="https://tva1.sinaimg.cn/large/008i3skNly1grokivolkvj30vw0ty0ug.jpg" alt="image-20210620112512903" style="zoom:50%;" />

服务端物理机网络配置:

<img src="https://tva1.sinaimg.cn/large/008i3skNly1grokt8urkqj60w20u0jt402.jpg" alt="image-20210620113512792" style="zoom:50%;" />

客户端运行的代码:

~~~java
    public static void main(String[] args) {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        InetSocketAddress serverAddress = new InetSocketAddress("192.168.162.4", 9090);
        // 端口号的范围为65535,一个short,两字节,16位,2^16=65535
        for (int i = 10000; i < 65000; i++) {

            try {
                SocketChannel client1 = SocketChannel.open();
                SocketChannel client2 = SocketChannel.open();

                // 首先要保证执行当前代码的机器的网卡上绑定了两个ip,分别为 192.168.162.5 , 192.168.162.6
                // 客户端1和客户端2,端口冲10000到65000
                // 客户端1连接到服务端
                client1.bind(new InetSocketAddress("192.168.162.5", i));
                client1.connect(serverAddress);
                clients.add(client1);

                // 客户端2连接到服务端
                client2.bind(new InetSocketAddress("192.168.162.6", i));
                client2.connect(serverAddress);
                clients.add(client1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("clients " + clients.size());
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
~~~

服务端运行的代码1:

~~~java
    //server socket listen property:
    private static final int RECEIVE_BUFFER = 10;
    private static final int SO_TIMEOUT = 0;
    private static final boolean REUSE_ADDR = false;
    private static final int BACK_LOG = 2;
    //client socket listen property on server endpoint:
    private static final boolean CLI_KEEPALIVE = false;
    private static final boolean CLI_OOB = false;
    /**
     * 接收请求的缓存空间大小
     */
    private static final int CLI_REC_BUF = 20;
    /**
     * 是否重复利用连接
     */
    private static final boolean CLI_REUSE_ADDR = false;
    private static final int CLI_SEND_BUF = 20;
    private static final boolean CLI_LINGER = true;
    private static final int CLI_LINGER_N = 0;
    private static final int CLI_TIMEOUT = 0;
    private static final boolean CLI_NO_DELAY = false;
/*

    StandardSocketOptions.TCP_NODELAY
    StandardSocketOptions.SO_KEEPALIVE
    StandardSocketOptions.SO_LINGER
    StandardSocketOptions.SO_RCVBUF
    StandardSocketOptions.SO_SNDBUF
    StandardSocketOptions.SO_REUSEADDR

 */

    public static void main(String[] args) {

        ServerSocket server;
        try {
            // 阻塞一下
            System.out.println("准备开始创建socket server,输入任意字符回车继续...");
            // 创建server端
            server = new ServerSocket();
            server.bind(new InetSocketAddress(9090), BACK_LOG);
            server.setReceiveBufferSize(CLI_REC_BUF);
            server.setReuseAddress(CLI_REUSE_ADDR);
            server.setSoTimeout(CLI_TIMEOUT);
            System.out.println("Server up use 9090,准备开始接收数据,输入任意字符回车继续...");
            // 阻塞一下,此时kernel已经开辟好资源,已经完成初次的三次握手,服务端和客户端可以在传输层通信,但不会被业务进程处理
            System.in.read();
            System.out.println("等待客户端连接...");
            while (true) {
    Socket socket=server.accept();
    log.info("client:{}",socket);
    socket.setKeepAlive(CLI_KEEPALIVE);
    socket.setOOBInline(CLI_OOB);
    socket.setReceiveBufferSize(CLI_REC_BUF);
    socket.setReuseAddress(CLI_REUSE_ADDR);
    socket.setSendBufferSize(CLI_SEND_BUF);
    socket.setSoLinger(CLI_LINGER,CLI_LINGER_N);
    socket.setSoTimeout(CLI_TIMEOUT);
    socket.setTcpNoDelay(CLI_NO_DELAY);
    // 此处抛出线程来处理新建立的tcp,性能极差,而且会受操作系统允许创建的线程数的限制,可以做代码优化
    // 参考服务端运行代码2
    new Thread(()->{
    try{
    InputStream in=socket.getInputStream();
    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
    char[]data=new char[1024];
    for(;;){
    int num=reader.read(data);
    if(num>0){
    log.info("socket:{},data:{}",socket,new String(data,0,num));
    }else if(num==0){
                                log.info("socket:{},read nothing!",socket);
                            } else {
                                log.info("socket:{},client readed -1...",socket);
                                System.in.read();
                                socket.close();
                                break;
                            }
                        }
                    } catch (Exception e) {
    log.error("Exception.",e);
    }
    }).start();
    }
    }catch(Exception e){
    log.error("Exception.",e);
    }
    }
~~~

服务端运行代码2:

~~~java
    //server socket listen property:
private static final int RECEIVE_BUFFER=10;
private static final int SO_TIMEOUT=0;
private static final boolean REUSE_ADDR=false;
private static final int BACK_LOG=2;
//client socket listen property on server endpoint:
private static final boolean CLI_KEEPALIVE=false;
private static final boolean CLI_OOB=false;
/**
 * 接收请求的缓存空间大小
 */
private static final int CLI_REC_BUF=20;
/**
 * 是否重复利用连接
 */
private static final boolean CLI_REUSE_ADDR=false;
private static final int CLI_SEND_BUF=20;
private static final boolean CLI_LINGER=true;
private static final int CLI_LINGER_N=0;
private static final int CLI_TIMEOUT=0;
private static final boolean CLI_NO_DELAY=false;
/*

    StandardSocketOptions.TCP_NODELAY
    StandardSocketOptions.SO_KEEPALIVE
    StandardSocketOptions.SO_LINGER
    StandardSocketOptions.SO_RCVBUF
    StandardSocketOptions.SO_SNDBUF
    StandardSocketOptions.SO_REUSEADDR

 */

public static void main(String[]args){

    ServerSocket server;
    try{
    // 阻塞一下
    System.out.println("准备开始创建socket server,输入任意字符回车继续...");
    // 创建server端
    server=new ServerSocket();
    server.bind(new InetSocketAddress(9090),BACK_LOG);
    server.setReceiveBufferSize(CLI_REC_BUF);
    server.setReuseAddress(CLI_REUSE_ADDR);
    server.setSoTimeout(CLI_TIMEOUT);
    System.out.println("Server up use 9090,准备开始接收数据,输入任意字符回车继续...");
    // 阻塞一下,此时kernel已经开辟好资源,已经完成初次的三次握手,服务端和客户端可以在传输层通信,但不会被业务进程处理
    System.in.read();
    System.out.println("等待客户端连接...");
    LinkedList<Socket> clients=new LinkedList<>();
    new Thread(()->{
    try{
    for(;;){
    TimeUnit.SECONDS.sleep(10);
    System.out.println("client num:"+clients.size());
    }
    }catch(Exception e){
    e.printStackTrace();
    }
    }).start();
    while(true){
    Socket socket=server.accept();
    socket.setKeepAlive(CLI_KEEPALIVE);
    socket.setOOBInline(CLI_OOB);
    socket.setReceiveBufferSize(CLI_REC_BUF);
    socket.setReuseAddress(CLI_REUSE_ADDR);
    socket.setSendBufferSize(CLI_SEND_BUF);
    socket.setSoLinger(CLI_LINGER,CLI_LINGER_N);
    socket.setSoTimeout(CLI_TIMEOUT);
    socket.setTcpNoDelay(CLI_NO_DELAY);
    clients.add(socket);
    }
    }catch(Exception e){
    log.error("Exception.",e);
    }
    }
~~~

服务端通过 netstat -natp | grep 192.168.162.5 | wc -l 和 netstat -natp | grep 192.168.162.6 | wc -l 对tcp连接数进行统计

~~~shell
[root@catface ~]# ifconfig
ens33: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.162.4  netmask 255.255.255.0  broadcast 192.168.162.255
        inet6 fe80::20c:29ff:fe15:8f23  prefixlen 64  scopeid 0x20<link>
        ether 00:0c:29:15:8f:23  txqueuelen 1000  (Ethernet)
        RX packets 642794  bytes 45524359 (43.4 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 531074  bytes 39664716 (37.8 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 66049  bytes 4986569 (4.7 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 66049  bytes 4986569 (4.7 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

virbr0: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
        inet 192.168.122.1  netmask 255.255.255.0  broadcast 192.168.122.255
        ether 52:54:00:68:bc:af  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 0  bytes 0 (0.0 B)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

[root@catface ~]# netstat -natp | grep 192.168.162.5 | wc -l
54999
[root@catface ~]# netstat -natp | grep 192.168.162.6 | wc -l
54999
[root@catface ~]#
~~~

客户端通过 netstat -natp | grep 192.168.162.4:9090 | wc -l

~~~shell
[root@catface ~]# ifconfig
ens33: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.162.5  netmask 255.255.255.0  broadcast 192.168.162.255
        inet6 fe80::20c:29ff:feab:b019  prefixlen 64  scopeid 0x20<link>
        ether 00:0c:29:ab:b0:19  txqueuelen 1000  (Ethernet)
        RX packets 529654  bytes 37318394 (35.5 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 642469  bytes 50233635 (47.9 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 6385  bytes 753815 (736.1 KiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 6385  bytes 753815 (736.1 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

virbr0: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
        inet 192.168.122.1  netmask 255.255.255.0  broadcast 192.168.122.255
        ether 52:54:00:68:bc:af  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 0  bytes 0 (0.0 B)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

[root@catface ~]# netstat -natp | grep 192.168.162.4:9090 | wc -l
109998
[root@catface ~]#
~~~

