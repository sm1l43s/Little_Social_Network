package com.brausov.social_network.controllers;

import com.brausov.social_network.models.Comment;
import com.brausov.social_network.models.Post;
import com.brausov.social_network.models.User;
import com.brausov.social_network.services.CommentService;
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
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/profile/addPost")
    public String addPost(@RequestParam(value = "message") String message,
                          @RequestParam("file") MultipartFile file) {
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        Post post = null;
        try {
            post = new Post(message, Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()),
                    Base64.getEncoder().encodeToString(file.getBytes()) ,user);
        } catch (IOException e) { e.printStackTrace(); }
        postService.add(post);

        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/removePost/{id}")
    public String removePost(@PathVariable(value = "id") long id, Model model) {
        postService.getById(id).getUsersWhoLiked().clear();
        postService.delete(postService.getById(id));
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));

        return "redirect:/profile/" + user.getId();
    }

    @ResponseBody
    @RequestMapping("/addComment_ajax")
    public String addComment(@RequestParam(value = "idPost") long idPost,
                             @RequestParam(value = "comment") String comment,
                             Model model) {
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        Post post = postService.getById(idPost);
        Comment c = new Comment(comment, Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()), post, user);
        commentService.add(c);
        post.addComment(c);
        postService.edit(post);

        return infoAboutComment(post, c);
    }

    @ResponseBody
    @RequestMapping("/deleteComment_ajax")
    public String romoveComment(@RequestParam(value = "idComment") long id, Model model) {
        Comment comment = commentService.getById(id);
        Post post = postService.getById(comment.getPost().getId());
        post.removeComment(comment);
        postService.edit(post);
        commentService.delete(comment);

        return "{totalComment:" + commentService.getAllByPost(post).size() + "}";
    }

    @ResponseBody
    @RequestMapping(value = "/profile/likePost_ajax", method = RequestMethod.POST)
    public String likePost(@RequestParam(value = "idPost") long idPost, Model model) {
        Post post = postService.getById(idPost);
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        post.likePost(user);
        post.setLikes(post.getLikes() + 1);
        postService.edit(post);

        return getInfoAboutPost(post, true);
    }

    @ResponseBody
    @RequestMapping(value = "/profile/dislikePost_ajax", method = RequestMethod.POST)
    public String disslikePost(@RequestParam(value = "idPost") long idPost, Model model) {
        Post post = postService.getById(idPost);
        User user = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        post.disslikePost(user);
        post.setLikes(post.getLikes() - 1);
        postService.edit(post);

        return getInfoAboutPost(post, false);
    }

    private Long getId(Authentication authentication) {
        int start = authentication.getName().indexOf("id=");
        int finish = authentication.getName().indexOf(",");
        String id = authentication.getName().substring(start + 3, finish);
        return Long.valueOf(id);
    }

    private String getInfoAboutPost(Post post, boolean flag) {
        int total = post.countLikes(postService.getAllPostByIdUser(post.getUser().getId()));
        String info = "{idPost:" + post.getId() + ", " +
                "likes:" + post.getLikes() + ", " +
                "totalLikes: " + total + ", " +
                "comment: 0" + ", " +
                "userIsLiked:" + flag +
                "}";
        return info;
    }

    private String infoAboutComment(Post post, Comment comment) {
       String info =  "{\"totalComment\": " + commentService.getAllByPost(post).size() + ", " +
                "\"idComment\": \"" + comment.getId() + "\", " +
                "\"text\": \"" + comment.getComment() + "\", " +
                "\"lastName\": \"" + comment.getUser().getLastName() + "\", " +
                "\"firstName\": \"" + comment.getUser().getFirstName() + "\", " +
                "\"idUser\": \"" + comment.getUser().getId() + "\", " +
                "\"date\": \"" + comment.getDate() + "\"" +
                "}";
        return info;
    }
}
