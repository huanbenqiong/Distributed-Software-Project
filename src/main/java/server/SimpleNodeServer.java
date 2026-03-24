package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 分布式节点服务端（基础骨架）
 * TODO: 后续接入线程池处理高并发，并实现心跳检测
 */
public class SimpleNodeServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println(">>> 正在启动分布式节点服务端...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(">>> 服务端已启动，监听端口: " + PORT);
            
            // 保持监听状态，等待客户端节点连接
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(">>> 收到新节点连接: " + clientSocket.getInetAddress().getHostAddress());
                
                // 暂时仅作连接打印，具体业务逻辑交由后续的 Handler 线程处理
                // new Thread(new NodeRequestHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("服务端启动或运行异常: " + e.getMessage());
        }
    }
}
