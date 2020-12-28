package com.brausov.social_network.controllers;

import com.brausov.social_network.models.User;
import com.brausov.social_network.services.RoleService;
import com.brausov.social_network.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.time.LocalDate;

@Controller
public class AuthenticationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login(@RequestParam(name = "message", required = false) Boolean message, Model model) {

        if (message != null) {
            if (message) {
                model.addAttribute("logout", true);
            } else {
                model.addAttribute("signError", true);
            }
        }

        return "login";
    }

    @GetMapping("/")
    public String signin(Model model) {
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String lastName, @RequestParam String firstName, @RequestParam String email,
                               @RequestParam String password, Model model) {

        Date currentDate = Date.valueOf(LocalDate.now());
        User user = new User(lastName, firstName, email, passwordEncoder.encode(password), currentDate);
        user.setRole(roleService.getByName("user"));
        userService.add(user);
        return "redirect:/login";
    }


}
