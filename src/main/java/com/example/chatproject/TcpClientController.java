package com.example.chatproject;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.UserUsecase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@Controller
public class TcpClientController {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9090;

    @Autowired
    private UserUsecase userUsecase;

    // Når brugeren går til rod-URL'en "/"
    @GetMapping("/")
    public String homePage() {
        return "login"; // Returnér login.html fra /templates mappen
    }

    @PostMapping("/login")
    public String findLogin(@ModelAttribute User user, Model model, HttpSession session) {
        User authenticatedUser = userUsecase.findLogin(user.getUsername(), user.getPassword());
        if (authenticatedUser != null) {
            session.setAttribute("currentUser", authenticatedUser);
            session.setAttribute("username", authenticatedUser.getUsername()); // Gem brugernavnet i session

            // Opret en ny tråd til klientforbindelsen til chatserveren
            new Thread(() -> {
                try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Send en besked til serveren
                    out.println("Client connected: " + authenticatedUser.getUsername());

                    // Modtag og udskriv serverens respons
                    System.out.println("Server response: " + in.readLine());
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }).start();

            return "redirect:/menu"; // Redirecter til menu-siden
        } else {
            model.addAttribute("error", "Ugyldig email eller kodeord");
            return "login";
        }
    }

    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        return "menu"; // Returnér menu.html fra /templates mappen
    }

    @GetMapping("/chat-room")
    public String chatRoom(Model model, HttpSession session) {
        // Tjek om brugeren er logget ind
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            return "index"; // Returnér chatten's HTML-side (index.html)
        } else {
            return "redirect:/"; // Hvis ikke logget ind, send tilbage til login-siden
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login"; // Returnér login.html efter logout
    }

    // Opret bruger (hvis nødvendigt)
    @GetMapping("/createUser")
    public String saveUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createUser";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute User user) {
        userUsecase.createUser(user);
        return "login"; // Returner login-siden efter oprettelse af bruger
    }

    // Chat TCP Client - For at sende besked til serveren
    @PostMapping("/send")
    @ResponseBody
    public String sendMessageToTcpServer(@RequestBody MessageRequest messageRequest) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send beskeden til serveren
            out.println(messageRequest.getMessage());

            // Modtag og returnér svaret fra serveren
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