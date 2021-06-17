package com.io.socket.socket基本流程;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/17 10:06 上午
 */
@Slf4j
public class SocketBase {

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
            System.in.read();
            // 创建server端
            server = new ServerSocket();
            server.bind(new InetSocketAddress(9090), BACK_LOG);
            server.setReceiveBufferSize(CLI_REC_BUF);
            server.setReuseAddress(CLI_REUSE_ADDR);
            server.setSoTimeout(CLI_TIMEOUT);
            System.out.println("Server up use 9090,准备开始接收数据,输入任意字符回车继续...");
            // 阻塞一下,此时kernel已经开辟好资源,已经完成初次的三次握手,服务端和客户端可以在传输层通信,但不会被业务进程处理
            System.in.read();
            System.in.read();
            System.out.println("等待客户端连接...");
            while (true) {
                // 开始接收请求
                Socket socket = server.accept();
                log.info("client:{}", socket);
                // 是否保持心跳检测
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
                                System.out.println("client read some data is :" + num + " val :" + new String(data, 0, num));
                            } else if (num == 0) {
                                System.out.println("client readed nothing!");
                            } else {
                                System.out.println("client readed -1...");
                                System.in.read();
                                socket.close();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Exception.", e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Exception.", e);
        }
    }
}
