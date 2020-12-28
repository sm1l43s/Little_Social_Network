package com.brausov.social_network.repositories;

import com.brausov.social_network.models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findAllPostsByUserIdOrderByIdDesc(long id);

}
