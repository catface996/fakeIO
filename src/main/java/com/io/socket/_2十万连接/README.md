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

服务端运行的代码:

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
                Socket socket = server.accept();
                log.info("client:{}", socket);
                socket.setKeepAlive(CLI_KEEPALIVE);
                socket.setOOBInline(CLI_OOB);
                socket.setReceiveBufferSize(CLI_REC_BUF);
                socket.setReuseAddress(CLI_REUSE_ADDR);
                socket.setSendBufferSize(CLI_SEND_BUF);
                socket.setSoLinger(CLI_LINGER, CLI_LINGER_N);
                socket.setSoTimeout(CLI_TIMEOUT);
                socket.setTcpNoDelay(CLI_NO_DELAY);
                new Thread(() -> {
                    try {
                        InputStream in = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        char[] data = new char[1024];
                        for (; ; ) {
                            int num = reader.read(data);
                            if (num > 0) {
                                log.info("socket:{},data:{}",socket,new String(data, 0, num));
                            } else if (num == 0) {
                                log.info("socket:{},read nothing!",socket);
                            } else {
                                log.info("socket:{},client readed -1...",socket);
                                System.in.read();
                                socket.close();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Exception.", e);
                    }
                }).start();
            }
        } catch (Exception e) {
            log.error("Exception.", e);
        }
    }
~~~

