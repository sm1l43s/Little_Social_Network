package com.brausov.social_network.controllers;

import com.brausov.social_network.models.Post;
import com.brausov.social_network.repositories.PostRepository;
import com.brausov.social_network.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/blog")
    public String home(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("post", posts);
        return "blog-main";
    }

    @GetMapping("/blog/add")
    public String add(Model model) {
        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String postAdd(@RequestParam String title, @RequestParam String anons, @RequestParam String fullText, Model model) {

        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}")
    public String postDetails(@PathVariable(value = "id") long id, Model model) {
        Post post = fromOptionalToArrayList(postRepository.findById(id));
        model.addAttribute("post", post);
        return "blog-details";
    }

    @GetMapping("/blog/{id}/edit")
    public String postEdit(@PathVariable(value = "id") long id, Model model) {
        Post post = fromOptionalToArrayList(postRepository.findById(id));
        model.addAttribute("post", post);
        return "blog-edit";
    }

    private Post fromOptionalToArrayList(Optional<Post> posts) {
        Post post = null;
        if (posts.isPresent()) {
            post = posts.get();
        } else {
            post = new Post();
        }
        return post;
     }

    private Long getId(Authentication authentication) {
        int start = authentication.getName().indexOf("id=");
        int finish = authentication.getName().indexOf(",");
        String id = authentication.getName().substring(start + 3, finish);
        return Long.valueOf(id);
    }
}
