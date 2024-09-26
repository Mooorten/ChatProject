package com.example.chatproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.chatproject", "service", "DBController"})
public class ChatProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatProjectApplication.class, args);
        TcpEchoServer t = new TcpEchoServer();
        t.startServer();
    }
}