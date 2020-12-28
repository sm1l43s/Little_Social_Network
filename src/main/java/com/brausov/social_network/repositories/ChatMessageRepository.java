package com.brausov.social_network.repositories;

import com.brausov.social_network.models.MessageChat;
import com.brausov.social_network.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ChatMessageRepository extends CrudRepository<MessageChat, Long> {

    Set<MessageChat> findAllByFromAndRecipient(User from, User recipient);

}
