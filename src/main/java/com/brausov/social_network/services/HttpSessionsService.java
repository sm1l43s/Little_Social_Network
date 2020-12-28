package com.brausov.social_network.services;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Service
public class HttpSessionsService {

    HashMap<Long, HttpSession> userAndSession = new HashMap<>();

    public void add(long userId, HttpSession session) {
        this.userAndSession.put(userId, session);
    }

    public HashMap<Long, HttpSession> getUserAndSession() {
        return userAndSession;
    }
}
