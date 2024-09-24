package com.example.chatproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatProjectApplication.class, args);
        TcpEchoServer tcpEchoServer = new TcpEchoServer();
        tcpEchoServer.startServer();
    }

}