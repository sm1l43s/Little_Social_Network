package com.brausov.social_network.services.impl;

import com.brausov.social_network.models.User;
import com.brausov.social_network.repositories.UserRepository;
import com.brausov.social_network.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void add(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    @Transactional
    public User getById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    @Transactional
    public User getByLastNameAndFirstName(String lastName, String firstName) {
        Optional<User> user = userRepository.findByLastNameAndFirstName(lastName, firstName);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    @Transactional
    public void edit(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional
    public List<User> getSuggestionUsers(User user) {
        List<User> userList = (List<User>) userRepository.findAll();
        if (userList.size() > 5) {
            userList = getRandomCountElementsList((List<User>) user.getUsersFollowing(), 5);
        }

        return userList;
    }

    private List<User> getRandomCountElementsList(List<User> users, int countElements) {
        Random rand = new Random();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < countElements; i++) {
            int randomIndex = rand.nextInt(users.size());
            User randomElement = users.get(randomIndex);
            users.remove(randomIndex);
        }
        return list;
    }
}
