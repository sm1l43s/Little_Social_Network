package com.brausov.social_network.services;

import com.brausov.social_network.models.Comment;
import com.brausov.social_network.models.Post;

import java.util.List;

public interface CommentService {

    List<Comment> getAll();
    List<Comment> getAllByPost(Post post);
    List<Comment> getListCommentByListPost(List<Post> posts);
    void add(Comment comment);
    void delete(Comment comment);
    void edit(Comment comment);
    Comment getById(long id);

}
