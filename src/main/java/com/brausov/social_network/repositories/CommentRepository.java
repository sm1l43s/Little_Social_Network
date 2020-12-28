package com.brausov.social_network.repositories;

import com.brausov.social_network.models.Comment;
import com.brausov.social_network.models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    List<Comment> getAllByPost(Post post);

}
