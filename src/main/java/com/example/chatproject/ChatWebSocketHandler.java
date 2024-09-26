package com.example.chatproject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private Set<WebSocketSession> activeSessions = Collections.synchronizedSet(new HashSet<>());
    private Map<WebSocketSession, String> sessionUsernames = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");
        activeSessions.add(session);
        sessionUsernames.put(session, username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String username = sessionUsernames.get(session);
        String payload = message.getPayload();

        if (payload.startsWith("@")) {
            // Håndter private beskeder
            String[] messageParts = payload.split(":", 2);
            if (messageParts.length == 2) {
                String targetUser = messageParts[0].substring(1).trim();
                String privateMessage = "<strong>" + username + ":</strong> " + messageParts[1].trim();
                WebSocketSession targetSession = getSessionByUsername(targetUser);

                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(privateMessage));
                    session.sendMessage(new TextMessage("Private besked til <strong>" + targetUser + ":</strong> " + messageParts[1].trim()));
                } else {
                    session.sendMessage(new TextMessage(targetUser + " er ikke online."));
                }
            }
        } else if (payload.startsWith("/file:")) {
            // Håndter fildeling
            String filename = payload.substring(6).trim();
            String fileMessage = "<strong>" + username + ":</strong> Har delt en fil: <a href='/download-file?filename=" + filename + "'>" + filename + "</a>";
            broadcastToAll(fileMessage);
        } else {
            // Broadcast almindelige beskeder til alle
            String broadcastMessage = "<strong>" + username + ":</strong> " + payload;
            broadcastToAll(broadcastMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activeSessions.remove(session);
        sessionUsernames.remove(session);
    }

    private WebSocketSession getSessionByUsername(String username) {
        return sessionUsernames.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(username))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void broadcastToAll(String message) {
        for (WebSocketSession session : activeSessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception ignored) {
                    // Ignorer fejl under udsendelse
                }
            }
        }
    }
}
