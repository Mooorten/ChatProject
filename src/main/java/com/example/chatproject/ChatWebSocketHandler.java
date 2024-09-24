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
        // Log session og username
        String username = (String) session.getAttributes().get("username");
        System.out.println("Forbindelse oprettet: " + username);

        sessions.add(session);
        sessionUsernameMap.put(session, username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Hent brugernavnet til sessionen
        String username = sessionUsernameMap.get(session);
        String formattedMessage = username + ": " + message.getPayload();

        System.out.println("Modtaget besked fra " + username + ": " + message.getPayload());

        // Send beskeden til alle tilknyttede klienter
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                System.out.println("Sender besked til session: " + webSocketSession.getId());
                webSocketSession.sendMessage(new TextMessage(formattedMessage));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Når en klient lukker forbindelsen, fjerner vi sessionen fra listen og fjerner brugernavnet
        sessions.remove(session);
        sessionUsernameMap.remove(session);
        System.out.println("Forbindelse lukket: " + session.getId());
    }
}