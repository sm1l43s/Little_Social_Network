package com.brausov.social_network.services.impl;

import com.brausov.social_network.models.MessageChat;
import com.brausov.social_network.models.User;
import com.brausov.social_network.repositories.ChatMessageRepository;
import com.brausov.social_network.services.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public void add(MessageChat chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    @Transactional
    public void delete(MessageChat chatMessage) {
        chatMessageRepository.delete(chatMessage);
    }

    @Override
    @Transactional
    public void edit(MessageChat chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    @Transactional
    public Set<MessageChat> findAll() {
        return (Set<MessageChat>) chatMessageRepository.findAll();
    }

    @Override
    @Transactional
    public Set<MessageChat> findAllByFromAndRecipient(User from, User recipient) {
        return chatMessageRepository.findAllByFromAndRecipient(from, recipient);
    }
}
