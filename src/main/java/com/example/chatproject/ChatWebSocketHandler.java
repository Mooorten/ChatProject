package com.example.chatproject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Holder styr på alle aktive sessions og deres tilknyttede brugernavne
    private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private Map<WebSocketSession, String> sessionUsernameMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        System.out.println("Forbindelse oprettet: " + username);

        sessions.add(session);
        sessionUsernameMap.put(session, username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = sessionUsernameMap.get(session);
        String payload = message.getPayload();

        // Unicast: "@modtager: besked"
        if (payload.startsWith("@")) {
            String[] parts = payload.split(":", 2);
            if (parts.length == 2) {
                String targetUsername = parts[0].substring(1).trim();
                String privateMessage = "<strong>" + username + ": </strong>" + parts[1].trim();

                WebSocketSession targetSession = getSessionForUser(targetUsername);
                if (targetSession != null && targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(privateMessage));
                    session.sendMessage(new TextMessage("Din private besked til <strong>" + targetUsername + "</strong>: " + parts[1].trim()));
                } else {
                    session.sendMessage(new TextMessage(targetUsername + "</strong> er ikke online."));
                }
            }
        } else {
            // Broadcast til alle
            String broadcastMessage = "<strong>" + username + ":</strong> " + payload;
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(broadcastMessage));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionUsernameMap.remove(session);
        System.out.println("Forbindelse lukket: " + session.getId());
    }

    public WebSocketSession getSessionForUser(String username) {
        for (Map.Entry<WebSocketSession, String> entry : sessionUsernameMap.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }
}