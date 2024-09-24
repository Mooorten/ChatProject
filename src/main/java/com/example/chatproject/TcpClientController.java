package com.example.chatproject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@RestController
public class TcpClientController {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;

    @PostMapping("/send")
    public String sendMessageToTcpServer(@RequestBody MessageRequest messageRequest) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the message to the server
            out.println(messageRequest.getMessage());

            // Receive and return the echoed message from the server
            return in.readLine();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static class MessageRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}