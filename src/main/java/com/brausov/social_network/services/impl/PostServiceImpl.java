package com.brausov.social_network.services.impl;

import com.brausov.social_network.models.Post;
import com.brausov.social_network.models.User;
import com.brausov.social_network.repositories.PostRepository;
import com.brausov.social_network.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    @Transactional
    public void add(Post post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post getById(long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        }
        return null;
    }

    @Override
    @Transactional
    public List<Post> getAllPostByIdUser(long id) {
        return postRepository.findAllPostsByUserIdOrderByIdDesc(id);
    }

    @Override
    @Transactional
    public void edit(Post post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public List<Post> getAll() {
        return (List<Post>) postRepository.findAll();
    }

    @Override
    @Transactional
    public List<Post> getAllPostByListUsers(Set<User> users) {
        ArrayList<Post> posts = new ArrayList<>();

        for (User user : users) {
            posts.addAll(postRepository.findAllPostsByUserIdOrderByIdDesc(user.getId()));
        }
        Collections.shuffle(posts);
        return posts;
    }
}
