package com.brausov.social_network.services.impl;

import com.brausov.social_network.models.Comment;
import com.brausov.social_network.models.Post;
import com.brausov.social_network.repositories.CommentRepository;
import com.brausov.social_network.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public List<Comment> getAll() {
        return (List<Comment>) commentRepository.findAll();
    }

    @Override
    @Transactional
    public List<Comment> getAllByPost(Post post) {
        return commentRepository.getAllByPost(post);
    }

    @Override
    @Transactional
    public void add(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void edit(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment getById(long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        }
        return null;
    }

    @Override
    public List<Comment> getListCommentByListPost(List<Post> posts) {
        List<Comment> comments = new ArrayList<>();

        for (Post post: posts) {
            comments.addAll(commentRepository.getAllByPost(post));
        }
        return comments;
    }
}
