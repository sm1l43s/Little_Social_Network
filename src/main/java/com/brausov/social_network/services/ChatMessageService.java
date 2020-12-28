package com.brausov.social_network.services;

import com.brausov.social_network.models.MessageChat;
import com.brausov.social_network.models.User;

import java.util.Set;

public interface ChatMessageService {

    void add(MessageChat chatMessage);
    void delete(MessageChat chatMessage);
    void edit(MessageChat chatMessage);
    Set<MessageChat> findAll();
    Set<MessageChat> findAllByFromAndRecipient(User from, User recipient);

}
