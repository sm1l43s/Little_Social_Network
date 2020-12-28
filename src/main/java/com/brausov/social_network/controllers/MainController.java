package com.brausov.social_network.controllers;

import com.brausov.social_network.models.Post;
import com.brausov.social_network.models.User;
import com.brausov.social_network.services.HttpSessionsService;
import com.brausov.social_network.services.PostService;
import com.brausov.social_network.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    public HttpSessionsService httpSessionsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @ModelAttribute("authUser")
    public User globalUserObject(Model model) {
        Long id = getId(SecurityContextHolder.getContext().getAuthentication());
        User user = userService.getById(id);
        model.addAttribute("authUser", user);
        return user;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Long id = getId(SecurityContextHolder.getContext().getAuthentication());
        User user = userService.getById(id);
        httpSessionsService.add(id, session);
        List<Post> posts = postService.getAllPostByListUsers(user.getUsersFollowing());
        model.addAttribute("posts", posts);
        return "home";
    }

    private Long getId(Authentication authentication) {
        int start = authentication.getName().indexOf("id=");
        int finish = authentication.getName().indexOf(",");
        String id = authentication.getName().substring(start + 3, finish);
        return Long.valueOf(id);
    }

}
