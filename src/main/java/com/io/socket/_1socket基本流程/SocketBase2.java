package com.io.socket._1socket基本流程;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/17 10:06 上午
 */
@Slf4j
public class SocketBase2 {

    /**
     * server socket listen property:
     */
    private static final int RECEIVE_BUFFER = 10;
    private static final int SO_TIMEOUT = 0;
    private static final boolean REUSE_ADDR = false;
    private static final int BACK_LOG = 2;
    /**
     * client socket listen property on server endpoint:
     */
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
            LinkedList<Socket> clients = new LinkedList<>();
            new Thread(() -> {
                try {
                    for (; ; ) {
                        TimeUnit.SECONDS.sleep(10);
                        System.out.println("client num:" + clients.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            while (true) {
                Socket socket = server.accept();
                socket.setKeepAlive(CLI_KEEPALIVE);
                socket.setOOBInline(CLI_OOB);
                socket.setReceiveBufferSize(CLI_REC_BUF);
                socket.setReuseAddress(CLI_REUSE_ADDR);
                socket.setSendBufferSize(CLI_SEND_BUF);
                socket.setSoLinger(CLI_LINGER, CLI_LINGER_N);
                socket.setSoTimeout(CLI_TIMEOUT);
                socket.setTcpNoDelay(CLI_NO_DELAY);
                clients.add(socket);
            }
        } catch (Exception e) {
            log.error("Exception.", e);
        }
    }
}
