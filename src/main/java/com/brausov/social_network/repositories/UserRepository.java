package com.brausov.social_network.repositories;

import com.brausov.social_network.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    Optional<User> findByLastNameAndFirstName(String lastName, String firstName);
}
