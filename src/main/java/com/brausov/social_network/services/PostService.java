package com.brausov.social_network.services;

import com.brausov.social_network.models.Post;
import com.brausov.social_network.models.User;

import java.util.List;
import java.util.Set;

public interface PostService {

    void add(Post post);
    void delete(Post post);
    Post getById(long id);
    List<Post> getAllPostByIdUser(long id);
    void edit(Post post);
    List<Post> getAll();
    List<Post> getAllPostByListUsers(Set<User> users);

}
