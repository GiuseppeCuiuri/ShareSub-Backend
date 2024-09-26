package com.example.progettonegozio.services;

import com.example.progettonegozio.entities.Chat;
import com.example.progettonegozio.entities.Message;
import com.example.progettonegozio.repositories.ChatRepository;
import com.example.progettonegozio.repositories.MessageRepository;
import com.example.progettonegozio.repositories.ProductHostedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProductHostedRepository productHostedRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, MessageRepository messageRepository, ProductHostedRepository productHostedRepository){
        this.chatRepository=chatRepository;
        this.messageRepository=messageRepository;
        this.productHostedRepository = productHostedRepository;
    }


    @Transactional(readOnly=false, isolation= Isolation.READ_COMMITTED)
    public void createChat(int productHostedId) {
        Chat chat = new Chat();
        chat.setProductHosted(productHostedRepository.findProductHostedById(productHostedId));
        chatRepository.save(chat);
    }

    //method to get all chats
    @Transactional(readOnly=true)
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
    
}
