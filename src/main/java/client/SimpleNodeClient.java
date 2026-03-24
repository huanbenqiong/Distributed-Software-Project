package client;

import java.io.IOException;
import java.net.Socket;

/**
 * 分布式节点客户端（基础骨架）
 * 用于模拟请求节点，向主节点或对等节点发起连接
 */
public class SimpleNodeClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println(">>> 正在初始化客户端节点...");
        
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println(">>> 成功连接到远程节点: " + SERVER_HOST + ":" + SERVER_PORT);
            
            // TODO: 后续添加序列化消息发送逻辑
            // OutputStream os = socket.getOutputStream();
            // os.write("Hello Distributed System".getBytes());
            
        } catch (IOException e) {
            System.err.println("节点连接失败，请检查目标服务端是否存活: " + e.getMessage());
        }
    }
}
