package com.brausov.social_network.services;

import com.brausov.social_network.models.User;

import java.util.List;

public interface UserService {

    void add(User user);
    void delete(User user);
    User getByEmail(String email);
    User getById(long id);
    User getByLastNameAndFirstName(String lastName, String firstName);
    void edit(User user);
    List<User> getAll();
    List<User> getSuggestionUsers(User user);

}
