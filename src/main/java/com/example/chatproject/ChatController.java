package com.example.chatproject;

import jakarta.servlet.http.HttpSession;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.UserUsecase;

@Controller
public class ChatController {

    @Autowired
    private UserUsecase userUsecase;

    @GetMapping("/")
    public String homePage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession session) {
        User authenticatedUser = userUsecase.findLogin(user.getUsername(), user.getPassword());
        if (authenticatedUser != null) {
            session.setAttribute("currentUser", authenticatedUser);
            session.setAttribute("username", authenticatedUser.getUsername());
            return "redirect:/menu";
        } else {
            model.addAttribute("error", "Ugyldig email eller kodeord");
            return "login";
        }
    }

    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        return "menu";
    }

    @GetMapping("/chat-room")
    public String chatRoom(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());
            return "index";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/createUser")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createUser";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute User user) {
        userUsecase.createUser(user);
        return "login";
    }
}
