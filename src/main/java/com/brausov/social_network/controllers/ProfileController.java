package com.brausov.social_network.controllers;

import com.brausov.social_network.models.Post;
import com.brausov.social_network.models.User;
import com.brausov.social_network.services.PostService;
import com.brausov.social_network.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@Controller
public class ProfileController {

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

    @ModelAttribute("messagesUsers")
    public Set<User> globalUsersMessager(Model model) {
        Long id = getId(SecurityContextHolder.getContext().getAuthentication());
        User user = userService.getById(id);
        Set<User> users = user.getUsersFollowing();
        users.addAll(user.getUsersFollowers());
        model.addAttribute("messagesUsers", users);
        return users;
    }

    @GetMapping("/profile/{id}")
    public String profileAnotherUser(@PathVariable(value = "id") long id,
                                     Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("posts", postService.getAllPostByIdUser(user.getId()));
        model.addAttribute("suggestions", userService.getSuggestionUsers(user));
        return "profile";
    }

    @PostMapping("/profile/setAvatar")
    public String setAvatar(@RequestParam("avatar") MultipartFile file) {
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        try {
            user.setAvatar(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userService.edit(user);

        return "redirect:/profile/" + user.getId();
    }

    @ResponseBody
    @RequestMapping(value = "/profile/subscribe_ajax", method = RequestMethod.GET)
    public String subscribe(@RequestParam(value = "id_follower") long id_follower,
                            Model model) {
        User whoSubscriber = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        User whoAreFollower = userService.getById(id_follower);
        whoAreFollower.addFollowers(whoSubscriber);
        whoSubscriber.addFollowing(whoAreFollower);
        userService.edit(whoAreFollower);
        userService.edit(whoSubscriber);

        return getInfo(whoSubscriber, whoAreFollower);
    }

    @ResponseBody
    @RequestMapping(value = "/profile/unsubscribe_ajax", method = RequestMethod.POST)
    public String unSubscribe(@RequestParam(value = "id_follower")long id_follower,
                              Model model) {

        User whoSubscriber = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        User whoAreFollower = userService.getById(id_follower);
        whoAreFollower.removeFollowers(whoSubscriber);
        whoSubscriber.removeFollowing(whoAreFollower);
        userService.edit(whoAreFollower);
        userService.edit(whoSubscriber);

        return getInfo(whoSubscriber, whoAreFollower);
    }

    private Long getId(Authentication authentication) {
        int start = authentication.getName().indexOf("id=");
        int finish = authentication.getName().indexOf(",");
        String id = authentication.getName().substring(start + 3, finish);
        return Long.valueOf(id);
    }

    //whoSubscriber - кто подписывается
    //whoAreFollower - на кого подписываются
    private String getInfo(User whoSubscriber, User whoAreFollower) {
        Post post = new Post();
        int likes = post.countLikes(postService.getAllPostByIdUser(whoAreFollower.getId()));

        String info = "{postCount:" + postService.getAllPostByIdUser(whoAreFollower.getId()).size() + ", " +
                "followersCount:" + whoAreFollower.getUsersFollowers().size() + ", " +
                "followingCount:" + whoAreFollower.getUsersFollowing().size() + "," +
                "likesCount:" + likes + "}";

        return info;
    }

}
