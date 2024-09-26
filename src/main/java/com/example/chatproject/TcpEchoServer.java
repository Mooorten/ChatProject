package com.example.chatproject;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpEchoServer {

    private static final int PORT = 9090;

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("TCP Echo Server is running on port " + PORT);

                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        System.out.println("Client connected: " + clientSocket.getInetAddress());

                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

                        String receivedMessage;
                        while ((receivedMessage = in.readLine()) != null) {
                            System.out.println("Received: " + receivedMessage);
                            out.println("Echo: " + receivedMessage);
                        }
                    } catch (Exception e) {
                        System.out.println("Connection error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("Server error: " + e.getMessage());
            }
        }).start();




        }

    }

