package com.brausov.social_network.controllers;

import com.brausov.social_network.models.MessageChat;
import com.brausov.social_network.models.User;
import com.brausov.social_network.services.ChatMessageService;
import com.brausov.social_network.services.UserService;
import com.brausov.social_network.util.ActiveUserChangeListener;
import com.brausov.social_network.util.ActiveUserManager;
import com.brausov.social_network.util.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Set;

@Controller
public class WebSocketChatController implements ActiveUserChangeListener {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveUserManager activeUserManager;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        activeUserManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeUserManager.removeListener(this);
    }

    @MessageMapping("/chat")
    public void send(SimpMessageHeaderAccessor sha, @Payload ChatMessage chatMessage) throws Exception {
        String sender = sha.getUser().getName();
        ChatMessage message = new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), chatMessage.getRecipient());

        User from = userService.getByLastNameAndFirstName(getPartsFullName(chatMessage.getFrom())[0],
                getPartsFullName(chatMessage.getFrom())[1]);

        User recipient = userService.getByLastNameAndFirstName(getPartsFullName(chatMessage.getRecipient())[0],
                getPartsFullName(chatMessage.getRecipient())[1]);

        MessageChat msg = new MessageChat(from, recipient, chatMessage.getText(), Time.valueOf(LocalTime.now()));
        chatMessageService.add(msg);

        if (!sender.equals(chatMessage.getRecipient())) {
            webSocket.convertAndSendToUser(sender, "/queue/messages", message);
        }
        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", message);
    }

    @ResponseBody
    @RequestMapping("/privateMessage")
    public String addComment(@RequestParam(value = "username") String userName) {
        User from = userService.getByLastNameAndFirstName(getPartsFullName(userName)[0], getPartsFullName(userName)[1]);
        User recipient = userService.getById(getId(SecurityContextHolder.getContext().getAuthentication()));
        Set<MessageChat> allMessages = chatMessageService.findAllByFromAndRecipient(from, recipient);
        allMessages.addAll(chatMessageService.findAllByFromAndRecipient(recipient, from));
        return String.valueOf(allMessages);
    }

    @Override
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeUserManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }

    private String[] getPartsFullName(String fullName) {
        return fullName.split(" ");
    }

    private Long getId(Authentication authentication) {
        int start = authentication.getName().indexOf("id=");
        int finish = authentication.getName().indexOf(",");
        String id = authentication.getName().substring(start + 3, finish);
        return Long.valueOf(id);
    }
}
